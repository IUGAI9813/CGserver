package com.example.cgserver.domain.user.service;

import com.example.cgserver.domain.user.dto.LoginRequest;
import com.example.cgserver.domain.user.dto.RefreshTokenRequest;
import com.example.cgserver.domain.user.dto.RegisterRequest;
import com.example.cgserver.domain.user.dto.TokenResponse;
import com.example.cgserver.domain.user.dto.UserResponse;
import com.example.cgserver.domain.user.entity.UserEntity;
import com.example.cgserver.domain.user.repository.UserRepository;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import com.example.cgserver.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일은 필수 입력값입니다.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호는 필수 입력값입니다.");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이름(사용자명)은 필수 입력값입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION, "이미 등록된 이메일입니다: " + request.getEmail());
        }

        String role = (request.getRoleCode() != null && !request.getRoleCode().isBlank())
                ? request.getRoleCode()
                : "ROLE_USER";

        String userId = "USR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        UserEntity user = UserEntity.builder()
                .userId(userId)
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .roleCode(role)
                .isActive("Y")
                .failedAttempts(0)
                .build();

        UserEntity savedUser = userRepository.save(user);

        return createTokenResponse(savedUser);
    }

    @Transactional(noRollbackFor = BusinessException.class)
    public TokenResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일과 비밀번호를 모두 입력해 주세요.");
        }

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "등록되지 않은 이메일입니다: " + request.getEmail()));

        if (!"Y".equalsIgnoreCase(user.getIsActive())) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "계정이 비활성화되어 있거나 잠겨 있습니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            int currentAttempts = (user.getFailedAttempts() != null ? user.getFailedAttempts() : 0);
            int failedAttempts = currentAttempts + 1;
            user.setFailedAttempts(failedAttempts);

            if (failedAttempts >= 5) {
                user.setIsActive("N");
            }

            userRepository.save(user);

            if (failedAttempts >= 5) {
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "비밀번호 5회 연속 오류로 계정이 잠겼습니다.");
            }

            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다. (실패 횟수: " + failedAttempts + "/5)");
        }

        user.setFailedAttempts(0);
        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        return createTokenResponse(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Refresh Token을 입력해 주세요.");
        }

        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않거나 만료된 Refresh Token입니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다: " + email));

        if (!"Y".equalsIgnoreCase(user.getIsActive())) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "계정이 비활성화되어 있거나 잠겨 있습니다.");
        }

        return createTokenResponse(user);
    }

    private TokenResponse createTokenResponse(UserEntity user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getUserId(), user.getRoleCode());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getUserId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessExpiration())
                .user(UserResponse.fromEntity(user))
                .build();
    }
}

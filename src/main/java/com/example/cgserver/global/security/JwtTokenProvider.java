package com.example.cgserver.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";

    @Value("${jwt.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-expiration:900000}")
    private long accessExpiration; // 15 минут

    @Getter
    @Value("${jwt.refresh-expiration:10800000}")
    private long refreshExpiration; // 3 часа

    private final CustomUserDetailsService userDetailsService;
    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (Exception e) {
            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String email, String userId, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email, String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // Сохраняем обратную совместимость
    public String createToken(String email, String userId, String role) {
        return createAccessToken(email, userId, role);
    }

    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String getUserId(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    public String getTokenType(String token) {
        return parseClaims(token).get(CLAIM_TYPE, String.class);
    }

    public boolean isRefreshToken(String token) {
        try {
            return TYPE_REFRESH.equals(getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            String type = getTokenType(token);
            return type == null || TYPE_ACCESS.equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}

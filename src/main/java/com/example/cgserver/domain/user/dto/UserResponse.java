package com.example.cgserver.domain.user.dto;

import com.example.cgserver.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String userId;
    private String email;
    private String username;
    private String roleCode;
    private String isActive;

    public static UserResponse fromEntity(UserEntity entity) {
        return UserResponse.builder()
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .roleCode(entity.getRoleCode())
                .isActive(entity.getIsActive())
                .build();
    }
}

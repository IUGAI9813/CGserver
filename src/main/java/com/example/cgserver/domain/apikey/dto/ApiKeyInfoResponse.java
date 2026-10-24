package com.example.cgserver.domain.apikey.dto;

import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.entity.KeyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyInfoResponse {

    private Long keyId;
    private String keyName;
    private KeyType keyType;
    private String status;
    private boolean isDemo;
    private Integer dailyLimit;
    private String createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime revokedAt;

    public static ApiKeyInfoResponse from(ApiKeyEntity entity) {
        return ApiKeyInfoResponse.builder()
                .keyId(entity.getKeyId())
                .keyName(entity.getKeyName())
                .keyType(entity.getKeyType())
                .status(entity.getStatus())
                .isDemo(entity.isDemo())
                .dailyLimit(entity.getDailyLimit())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }
}

package com.example.cgserver.domain.apikey.service;

import com.example.cgserver.domain.apikey.dto.ApiKeyAddReq;
import com.example.cgserver.domain.apikey.dto.ApiKeyInfoResponse;
import com.example.cgserver.domain.apikey.dto.ApiKeyResponse;
import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.repository.ApiKeyRepository;
import com.example.cgserver.global.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository keyRepository;
    private final Helper helper;

    // API키 저장(시뮬레이터 연동)
    @Transactional
    public ApiKeyResponse save(ApiKeyAddReq req , String userId){

        String rawApiKey = "cg_live_" + UUID.randomUUID().toString().replace("-", "");

        int dayLimit = (req.isDemo() && req.getKeyType() != null)
                ? req.getKeyType().getDemoDailyLimit()
                : 0;

        // 키 암호화
        String hashKey = helper.sha256(rawApiKey);

        OffsetDateTime revokedAt = (req.getRevokedAt() != null)
                ? req.getRevokedAt().atStartOfDay().atOffset(ZoneOffset.ofHours(9))
                : null;

        ApiKeyEntity entity = ApiKeyEntity.builder()
                .keyName(req.getKeyName())
                .apiKey(hashKey)
                .keyType(req.getKeyType())
                .createdBy(userId)
                .isDemo(req.isDemo())
                .dailyLimit(dayLimit)
                .revokedAt(revokedAt)
                .build();

        keyRepository.save(entity);

        return ApiKeyResponse.of(rawApiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyInfoResponse> getList() {
        return keyRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ApiKeyInfoResponse::from)
                .toList();
    }

}

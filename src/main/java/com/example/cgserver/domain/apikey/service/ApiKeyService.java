package com.example.cgserver.domain.apikey.service;

import com.example.cgserver.domain.apikey.dto.ApiKeyAddReq;
import com.example.cgserver.domain.apikey.dto.ApiKeyResponse;
import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.repository.ApiKeyRepository;
import com.example.cgserver.global.Helper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyService {

    private ApiKeyRepository keyRepository;
    private Helper helper;



    // API키 저장(시뮬레이터 연동)
    public ApiKeyResponse save(ApiKeyAddReq req , String userId){

        String rawApiKey = "cg_live_" + UUID.randomUUID().toString().replace("-", "");

        int dayLimit = 0;
        if (req.isDemo() && req.getKeyType() == "TELEMETRY"){
            dayLimit = 5000;
        } else if (req.isDemo() && req.getKeyType() == "VEHICLE") {
            dayLimit = 50;
        }


        // 키 암호화
        String hashKey = helper.sha256(rawApiKey);

        ApiKeyEntity entity = ApiKeyEntity.builder()
                .key_name(req.getKeyName())
                .apiKey(hashKey)
                .key_type(req.getKeyType())
                .createdBy(userId)
                .isDemo(req.isDemo())
                .dailyLimit(dayLimit)
                .build();

        keyRepository.save(entity);

        return ApiKeyResponse.of(rawApiKey);
    }

}

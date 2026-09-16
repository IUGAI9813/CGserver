package com.example.cgserver.domain.apikey.dto;


import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyAddReq {

    private String keyName;
    private String keyType;
    private String revoked_at;
    private boolean isDemo;


}

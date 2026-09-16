package com.example.cgserver.domain.apikey.dto;


import com.example.cgserver.domain.apikey.repository.ApiKeyRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApiKeyResponse {


    private String rawKey;

    public static ApiKeyResponse of(String rawKey){


        return  ApiKeyResponse.builder()
                .rawKey(rawKey).build();
    }
}

package com.example.cgserver.domain.apikey.dto;


import com.example.cgserver.domain.apikey.entity.KeyType;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyAddReq {

    private String keyName;
    private KeyType keyType;
    private LocalDate revokedAt;
    private boolean isDemo;


}

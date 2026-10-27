package com.example.cgserver.apiKey;

import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.entity.KeyType;
import com.example.cgserver.domain.apikey.repository.ApiKeyRepository;
import com.example.cgserver.domain.apikey.service.ApiKeyService;
import com.example.cgserver.global.Helper;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiKeyServiceValidationTest {


    @Mock
    private ApiKeyRepository keyRepository;

    @Spy
    private Helper helper = new Helper();

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    @DisplayName("apiKeyValidate - 정상 키와 올바른 타입이면 검증에 성공하고 엔티티를 반환한다")
    void apiKeyValideSuccess(){

        String rawKey = "cg_live_valid12345";
        String hashedKey = helper.sha256(rawKey);
        ApiKeyEntity mockEntity = ApiKeyEntity.builder()
                .keyId(1L)
                .keyName("vehicle-key")
                .apiKey(hashedKey)
                .keyType(KeyType.VEHICLE)
                .status("ACTIVE")
                .isDemo(true)
                .dailyLimit(10)
                .build();

        when(keyRepository.findByApiKey(hashedKey)).thenReturn(Optional.of(mockEntity));


        ApiKeyEntity result = apiKeyService.apiKeyValidate(rawKey, KeyType.VEHICLE);
        assertThat(result).isNotNull();

    }


    @Test
    @DisplayName("API key null")
    void apiKeyIsNull() {
        assertThatThrownBy( () -> apiKeyService.apiKeyValidate(null , KeyType.DEVICE))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.API_KEY_REQUIRED);
    }
}

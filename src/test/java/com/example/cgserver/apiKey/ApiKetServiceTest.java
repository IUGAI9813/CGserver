package com.example.cgserver.apiKey;

import com.example.cgserver.domain.apikey.dto.ApiKeyAddReq;
import com.example.cgserver.domain.apikey.dto.ApiKeyResponse;
import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.entity.KeyType;
import com.example.cgserver.domain.apikey.repository.ApiKeyRepository;
import com.example.cgserver.domain.apikey.service.ApiKeyService;
import com.example.cgserver.global.Helper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiKetServiceTest {

    @Mock
    private ApiKeyRepository keyRepository;

    @Spy
    private Helper helper = new Helper();

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    @DisplayName("데모 키(TELEMETRY) 생성 시 일일 제한 5000건 설정 및 cg_live_ 접두사 검증")
    void save_telemetryDemoKey_success() {
        // given
        LocalDate expiryDate = LocalDate.now().plusMonths(3);
        ApiKeyAddReq req = ApiKeyAddReq.builder()
                .keyName("seoul-telemetry-key")
                .keyType(KeyType.TELEMETRY)
                .isDemo(true)
                .revokedAt(expiryDate)
                .build();
        String userId = "operator01";

        when(keyRepository.save(any(ApiKeyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ApiKeyResponse response = apiKeyService.save(req, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getRawKey()).startsWith("cg_live_");
        assertThat(response.getRawKey()).hasSize(8 + 32); // "cg_live_" (8) + UUID without dashes (32)

        ArgumentCaptor<ApiKeyEntity> entityCaptor = ArgumentCaptor.forClass(ApiKeyEntity.class);
        verify(keyRepository, times(1)).save(entityCaptor.capture());

        ApiKeyEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getKeyName()).isEqualTo("seoul-telemetry-key");
        assertThat(savedEntity.getKeyType()).isEqualTo(KeyType.TELEMETRY);
        assertThat(savedEntity.isDemo()).isTrue();
        assertThat(savedEntity.getDailyLimit()).isEqualTo(5000);
        assertThat(savedEntity.getCreatedBy()).isEqualTo(userId);
        assertThat(savedEntity.getApiKey()).isEqualTo(helper.sha256(response.getRawKey()));
        assertThat(savedEntity.getRevokedAt()).isEqualTo(expiryDate.atStartOfDay().atOffset(ZoneOffset.ofHours(9)));
    }

    @Test
    @DisplayName("데모 키(DEVICE) 생성 시 일일 제한 50건으로 설정된다")
    void save_deviceDemoKey_setsDailyLimit50() {
        // given
        ApiKeyAddReq req = ApiKeyAddReq.builder()
                .keyName("device-demo-key")
                .keyType(KeyType.DEVICE)
                .isDemo(true)
                .build();

        // when
         apiKeyService.save(req, "admin");

        // then
        ArgumentCaptor<ApiKeyEntity> entityCaptor = ArgumentCaptor.forClass(ApiKeyEntity.class);
        verify(keyRepository).save(entityCaptor.capture());

        ApiKeyEntity saved = entityCaptor.getValue();
        assertThat(saved.getKeyType()).isEqualTo(KeyType.DEVICE);
        assertThat(saved.isDemo()).isTrue();
        assertThat(saved.getDailyLimit()).isEqualTo(50);
    }

    @Test
    @DisplayName("운영(일반) 키 생성 시 데모가 아니면 일일 제한이 0으로 설정된다")
    void save_productionKey_setsDailyLimitZero() {

        LocalDate expiryDate = LocalDate.now().plusMonths(3);
        //given
        ApiKeyAddReq req = ApiKeyAddReq.builder()
                .keyName("no-expiration-key")
                .keyType(KeyType.TELEMETRY)
                .isDemo(false)
                .revokedAt(expiryDate)
                .build();

       //when
        apiKeyService.save(req , "USR-598e06c0bc62");

        //then

        ArgumentCaptor<ApiKeyEntity>  entityCaptor =  ArgumentCaptor.forClass(ApiKeyEntity.class);
        verify(keyRepository).save(entityCaptor.capture());

        ApiKeyEntity saved  = entityCaptor.getValue();

        assertThat(saved.getDailyLimit()).isEqualTo(0);
        assertThat(saved.isDemo()).isFalse();
    }

    @Test
    @DisplayName("만료일(revokedAt)이 null이어도 NPE 없이 정상 저장된다")
    void save_nullRevokedAt_successWithoutNpe() {
        // given
        ApiKeyAddReq req = ApiKeyAddReq.builder()
                .keyName("no-expiration-key")
                .keyType(KeyType.TELEMETRY)
                .isDemo(false)
                .revokedAt(null)
                .build();

        // when
        apiKeyService.save(req, "admin");

        // then
        ArgumentCaptor<ApiKeyEntity> entityCaptor = ArgumentCaptor.forClass(ApiKeyEntity.class);
        verify(keyRepository).save(entityCaptor.capture());

        ApiKeyEntity saved = entityCaptor.getValue();
        assertThat(saved.getRevokedAt()).isNull();
    }

    @Test
    @DisplayName("DB에는 원본 키가 아닌 SHA-256 해시값만 저장된다")
    void save_storesOnlyHashedKeyInDatabase() {
        // given
        ApiKeyAddReq req = ApiKeyAddReq.builder()
                .keyName("security-check-key")
                .keyType(KeyType.TELEMETRY)
                .isDemo(false)
                .build();

        // when
        ApiKeyResponse response = apiKeyService.save(req, "security_team");

        // then
        ArgumentCaptor<ApiKeyEntity> entityCaptor = ArgumentCaptor.forClass(ApiKeyEntity.class);
        verify(keyRepository).save(entityCaptor.capture());

        ApiKeyEntity saved = entityCaptor.getValue();
        // 원본 키와 저장된 해시값은 절대 같지 않아야 함
        assertThat(saved.getApiKey()).isNotEqualTo(response.getRawKey());
        // 해시값은 SHA-256 64자리 16진수여야 함
        assertThat(saved.getApiKey()).hasSize(64);
        assertThat(saved.getApiKey()).isEqualTo(helper.sha256(response.getRawKey()));
    }

    @Test
    @DisplayName("API 키 목록 조회 시 민감한 해시/비밀키 없이 안전한 메타데이터만 반환된다")
    void getList_returnsSafeKeyListWithoutSensitiveHash() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        ApiKeyEntity key1 = ApiKeyEntity.builder()
                .keyId(1L)
                .keyName("seoul-bus-telemetry")
                .apiKey("secret_sha256_hash_11111111111111111111111111111111111111111111")
                .keyType(KeyType.TELEMETRY)
                .status("ACTIVE")
                .createdBy("admin")
                .isDemo(true)
                .dailyLimit(5000)
                .createdAt(now)
                .revokedAt(now.plusDays(90))
                .build();

        ApiKeyEntity key2 = ApiKeyEntity.builder()
                .keyId(2L)
                .keyName("busan-device-register")
                .apiKey("secret_sha256_hash_22222222222222222222222222222222222222222222")
                .keyType(KeyType.DEVICE)
                .status("REVOKED")
                .createdBy("admin")
                .isDemo(false)
                .dailyLimit(0)
                .createdAt(now.minusDays(10))
                .revokedAt(now.minusDays(1))
                .build();

        when(keyRepository.findAllByOrderByCreatedAtDesc()).thenReturn(java.util.List.of(key1, key2));

        // when
        java.util.List<com.example.cgserver.domain.apikey.dto.ApiKeyInfoResponse> result = apiKeyService.getList();

        // then
        assertThat(result).hasSize(2);

        com.example.cgserver.domain.apikey.dto.ApiKeyInfoResponse first = result.get(0);
        assertThat(first.getKeyId()).isEqualTo(1L);
        assertThat(first.getKeyName()).isEqualTo("seoul-bus-telemetry");
        assertThat(first.getKeyType()).isEqualTo(KeyType.TELEMETRY);
        assertThat(first.getStatus()).isEqualTo("ACTIVE");
        assertThat(first.isDemo()).isTrue();
        assertThat(first.getDailyLimit()).isEqualTo(5000);
        assertThat(first.getCreatedBy()).isEqualTo("admin");
        assertThat(first.getCreatedAt()).isEqualTo(now);
        assertThat(first.getRevokedAt()).isEqualTo(now.plusDays(90));

        // ApiKeyInfoResponse не содержит поля с сырым ключом или хешем
    }
}

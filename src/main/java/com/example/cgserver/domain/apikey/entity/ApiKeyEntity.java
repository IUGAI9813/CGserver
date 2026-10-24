package com.example.cgserver.domain.apikey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_API_KEYS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApiKeyEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KEY_ID")
    private Long keyId;

    @Column(name ="KEY_NAME", nullable = false, length = 100)
    private String keyName;

    @Column(name="API_KEY_HASH", nullable = false, length = 255)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(name="KEY_TYPE", nullable = false, length = 30)
    private KeyType keyType;

    @Column(name="STATUS", nullable = false, length = 20)
    private String status;

    @Column(name="CREATED_BY", nullable = false, length = 50)
    private String createdBy;

    @Column(name="CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name="REVOKED_AT")
    private OffsetDateTime revokedAt;

    @Column(name="IS_DEMO", nullable = false)
    private boolean isDemo;

    @Column(name="DAILY_LIMIT")
    private Integer dailyLimit;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }

        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }
}

package com.example.cgserver.domain.audit.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "TB_AUDIT_LOGS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;

    @Column(name = "ACTION_NAME", nullable = false, length = 100)
    private String actionName;

    @Column(name = "TARGET_ENTITY", nullable = false, length = 50)
    private String targetEntity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "DETAILS", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "PREVIOUS_HASH", length = 64)
    private String previousHash;

    @Column(name = "CRYPTO_HASH", nullable = false, length = 64)
    private String cryptoHash;

    @Column(name = "LEVEL" , nullable = false, length = 20)
    private String level;

    @Column(name = "SOURCE", nullable = false, length = 100)
    private String source;

    @Column(name = "PERFORMED_AT", nullable = false, updatable = false)
    private OffsetDateTime performedAt;

    @PrePersist
    public void prePersist() {
        if (this.performedAt == null) {
            this.performedAt = OffsetDateTime.now();
        }
    }
}

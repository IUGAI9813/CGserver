package com.example.cgserver.domain.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_COMMON_CODE")
@IdClass(CommonCodeId.class) // 👈 ВОТ
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonCodeEntity {

    @Id
    @Column(name = "GROUP_CODE", length = 50, nullable = false)
    private String groupCode;

    @Id
    @Column(name = "CODE", length = 50, nullable = false)
    private String code;

    @Column(name = "CODE_NAME", length = 100, nullable = false)
    private String codeName;

    @Column(name = "SORT_ORDER", nullable = false)
    @Builder.Default
    private Integer sortOrder = 1;

    @Column(name = "IS_ACTIVE", length = 1, nullable = false)
    @Builder.Default
    private String isActive = "Y";

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

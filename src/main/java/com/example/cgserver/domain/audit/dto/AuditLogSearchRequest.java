package com.example.cgserver.domain.audit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AuditLogSearchRequest {
    private String level;
    private String userId;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
}

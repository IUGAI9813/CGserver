package com.example.cgserver.domain.audit.dto;

import com.example.cgserver.domain.audit.entity.AuditEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class AuditLogResponse {

    private Long logId;
    private String userId;
    private String actionName;
    private Map<String, Object> details;


    public static AuditLogResponse fromEntity(AuditEntity entity) {
        return  AuditLogResponse.builder().
                logId(entity.getLogId()).
                userId(entity.getUserId()).
                actionName(entity.getActionName()).
                details((entity.getDetails())).
                build();
    }
}

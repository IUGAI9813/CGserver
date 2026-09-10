package com.example.cgserver.domain.audit.dto;

import com.example.cgserver.domain.audit.entity.AuditEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Builder
public class AuditLogResponse {

    private Long logId;
    private String userId;
    private String actionName;
    private Map<String, Object> details;
    private OffsetDateTime performedAt;
    private String crypthoHas;
    private String level;
    private String levelName;
    private String source;


    public static AuditLogResponse fromEntity(AuditEntity entity , String levelName) {
        return  AuditLogResponse.builder().
                logId(entity.getLogId()).
                userId(entity.getUserId()).
                actionName(entity.getActionName()).
                details((entity.getDetails())).
                levelName((levelName)).
                crypthoHas((entity.getCryptoHash())).
                level((entity.getLevel())).
                source((entity.getSource())).
                performedAt((entity.getPerformedAt())).
                build();
    }
}

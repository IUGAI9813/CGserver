package com.example.cgserver.domain.audit.service;

import com.example.cgserver.domain.audit.dto.AuditLogResponse;
import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditEntity;
import com.example.cgserver.domain.audit.entity.AuditLevel;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.repository.AuditLogRepository;
import com.example.cgserver.domain.common.service.CommonCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuditLogService {

    private AuditLogRepository repository;

    private CommonCodeService commonCodeService;

    public List<AuditLogResponse> getList(){
        return repository.findAll().stream().map
                (entity -> {
                    String codeName = commonCodeService.getCache("AUDIT_LEVEL", entity.getLevel());
                    return  AuditLogResponse.fromEntity(entity, codeName);
                }).toList();
    }

  // 사용 차량 정지 로그 기록
    @Transactional
    public void record(String userId, AuditLevel auditLevel, AuditAction auditAction, TargetEntity targetEntity, Map<String, Object> details){

        String previousHash = repository.findFirstByOrderByLogIdDesc().map(AuditEntity::getCryptoHash).orElse("0000000000000000000000000000000000000000000000000000000000000000");

        String clientIp = getClientIp();

        String rawData =  previousHash + userId + auditAction.name() + targetEntity.name() + (details != null ? details.toString() : "");

        String cryptoHash = sha256(rawData);

        AuditEntity entity = AuditEntity.builder()
                .userId(userId)
                .actionName(auditAction.name())
                .targetEntity(targetEntity.name())
                .details(details)
                .level(auditLevel.name())
                .source(clientIp)
                .previousHash(previousHash)
                .cryptoHash(cryptoHash)
                .build();

        repository.save(entity);


    }


    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return (ip != null && !ip.isEmpty()) ? ip : "127.0.0.1";
        }
        return "SYSTEM"; //  kafka/cron
    }
    // 암호화키 생성
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return "hash_error";
        }
    }


}

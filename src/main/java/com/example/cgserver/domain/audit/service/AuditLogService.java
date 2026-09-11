package com.example.cgserver.domain.audit.service;

import com.example.cgserver.domain.audit.dto.AuditLogResponse;
import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditEntity;
import com.example.cgserver.domain.audit.entity.AuditLevel;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.repository.AuditLogRepository;
import com.example.cgserver.domain.audit.dto.AuditLogSearchRequest;
import com.example.cgserver.domain.common.service.CommonCodeService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@AllArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;

    private final CommonCodeService commonCodeService;

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getList(AuditLogSearchRequest request, Pageable pageable) {
        Specification<AuditEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request != null) {
                if (StringUtils.hasText(request.getLevel())) {
                    predicates.add(cb.equal(root.get("level"), request.getLevel().trim()));
                }
                if (StringUtils.hasText(request.getUserId())) {
                    predicates.add(cb.like(cb.lower(root.get("userId")), "%" + request.getUserId().trim().toLowerCase() + "%"));
                }
                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("performedAt"), request.getStartDate()));
                }
                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("performedAt"), request.getEndDate()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable).map(entity -> {
            String codeName = commonCodeService.getCache("AUDIT_LEVEL", entity.getLevel());
            return AuditLogResponse.fromEntity(entity, codeName);
        });
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

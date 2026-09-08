package com.example.cgserver.domain.audit.service;

import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditEntity;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuditLogService {

    private AuditLogRepository repository;


    @Transactional
    public void record(String userId, AuditAction auditAction, TargetEntity targetEntity, Map<String, Object> details){

        String previousHash = repository.findFirstByOrderByLogIdDesc().map(AuditEntity::getCryptoHash).orElse("0000000000000000000000000000000000000000000000000000000000000000");

        String rawData =  previousHash + userId + auditAction.name() + targetEntity.name() + (details != null ? details.toString() : "");

        String cryptoHash = sha256(rawData);

        AuditEntity entity = AuditEntity.builder()
                .userId(userId)
                .actionName(auditAction.name())
                .targetEntity(targetEntity.name())
                .details(details)
                .previousHash(previousHash)
                .cryptoHash(cryptoHash)
                .build();

        repository.save(entity);


    }

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

package com.example.cgserver.domain.audit.repository;

import com.example.cgserver.domain.audit.entity.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditEntity, Long>, JpaSpecificationExecutor<AuditEntity> {

    Optional<AuditEntity> findFirstByOrderByLogIdDesc();
}



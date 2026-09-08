package com.example.cgserver.domain.audit.repository;

import com.example.cgserver.domain.audit.entity.AuditEntity;
import org.hibernate.annotations.SQLSelect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditEntity, String> {



    Optional<AuditEntity> findFirstByOrderByLogIdDesc();

}

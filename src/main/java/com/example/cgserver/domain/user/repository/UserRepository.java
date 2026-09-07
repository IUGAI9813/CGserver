package com.example.cgserver.domain.user.repository;

import com.example.cgserver.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

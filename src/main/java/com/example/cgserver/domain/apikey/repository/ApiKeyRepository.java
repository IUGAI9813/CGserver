package com.example.cgserver.domain.apikey.repository;

import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity , Long> {
}

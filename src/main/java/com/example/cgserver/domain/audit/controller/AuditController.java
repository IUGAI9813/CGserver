package com.example.cgserver.domain.audit.controller;


import com.example.cgserver.domain.audit.dto.AuditLogResponse;
import com.example.cgserver.domain.audit.service.AuditLogService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@AllArgsConstructor
public class AuditController {

    private AuditLogService service;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getList(){
        return  ResponseEntity.ok(service.getList());
    }
}

package com.example.cgserver.domain.audit.controller;

import com.example.cgserver.domain.audit.dto.AuditLogResponse;
import com.example.cgserver.domain.audit.dto.AuditLogSearchRequest;
import com.example.cgserver.domain.audit.service.AuditLogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
@AllArgsConstructor
public class AuditController {

    private final AuditLogService service;

    @PostMapping
    public ResponseEntity<Page<AuditLogResponse>> getList(
            @RequestBody(required = false) AuditLogSearchRequest request,
            @PageableDefault(size = 20, sort = "logId", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getList(request, pageable));
    }
}




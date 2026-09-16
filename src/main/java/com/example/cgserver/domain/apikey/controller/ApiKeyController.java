package com.example.cgserver.domain.apikey.controller;


import com.example.cgserver.domain.apikey.dto.ApiKeyAddReq;
import com.example.cgserver.domain.apikey.dto.ApiKeyResponse;
import com.example.cgserver.domain.apikey.service.ApiKeyService;
import com.example.cgserver.global.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/key")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService service;


    @PostMapping
    public ResponseEntity<ApiKeyResponse> save(@RequestBody ApiKeyAddReq req , @AuthenticationPrincipal CustomUserDetails userDetails){

        String userId = (userDetails != null && userDetails.getUser() != null)
                ? userDetails.getUser().getUserId()
                : "SYSTEM";
        return  ResponseEntity.ok(service.save(req, userId));
    }
}

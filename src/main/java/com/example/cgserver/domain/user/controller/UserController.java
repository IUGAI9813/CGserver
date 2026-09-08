package com.example.cgserver.domain.user.controller;

import com.example.cgserver.domain.user.dto.UserResponse;
import com.example.cgserver.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // 사용자 리스트
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){

        return  ResponseEntity.ok(service.getAllUsers());
    }

    // 사용자 정보
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable  String email){
        return ResponseEntity.ok(service.getUser(email));
    }


}

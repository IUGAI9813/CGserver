package com.example.cgserver.domain.vehicle.controller;


import com.example.cgserver.domain.vehicle.dto.VehicleLockReq;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.service.VehicleService;
import com.example.cgserver.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService service;

    // 차량 리스트
    @GetMapping
    public ResponseEntity<List<VehicleReponse>> getList(){
        return  ResponseEntity.ok(service.getAllVehicles());
    }

    // 차량 정보
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleReponse> getById(@PathVariable String vehicleId){
        return  ResponseEntity.ok(service.getById(vehicleId));
    }

    // 차량 비상 정지
    @PostMapping("/lokdown/{vehicleId}")
    public ResponseEntity<VehicleReponse> lokdownVehice(
            @PathVariable String vehicleId,
            @RequestBody VehicleLockReq request,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        String reason = (request != null && request.reason() != null) ? request.reason() : "";

        String userId = userDetails.getUser().getUserId();

        return  ResponseEntity.ok(service.lockdownVehicle(vehicleId, userId, reason));
    }







}

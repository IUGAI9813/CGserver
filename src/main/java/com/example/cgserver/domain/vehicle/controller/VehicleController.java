package com.example.cgserver.domain.vehicle.controller;


import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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





}

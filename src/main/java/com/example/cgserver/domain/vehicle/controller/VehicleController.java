package com.example.cgserver.domain.vehicle.controller;

import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.entity.KeyType;
import com.example.cgserver.domain.apikey.service.ApiKeyService;
import com.example.cgserver.domain.telemetry.service.TelemetryService;
import com.example.cgserver.domain.vehicle.dto.VehicleLockReq;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import com.example.cgserver.domain.vehicle.service.VehicleService;
import com.example.cgserver.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final ApiKeyService apiKeyService;
    private final TelemetryService telemetryService;

    // 차량 리스트
    @GetMapping
    public ResponseEntity<List<VehicleReponse>> getList() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }



    @GetMapping("/{vehicle_id}/devices")
   public  ResponseEntity<List<VehicleReponse.DeviceResponse>> getVehicleDevices(@PathVariable String vehicle_id){

        return ResponseEntity.ok(vehicleService.getVehiclesDevices(vehicle_id));
    }


    // 차량 등록
    @PostMapping
    public ResponseEntity<VehicleReponse> addVehicle(
            @RequestBody VehicleSaveReq request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 키 검증
        ApiKeyEntity key =  apiKeyService.apiKeyValidate(request.getApiKey(), KeyType.VEHICLE);

        String userId = (userDetails != null && userDetails.getUser() != null)
                ? userDetails.getUser().getUserId()
                : (key.getCreatedBy() != null ? key.getCreatedBy() : "SYSTEM");

        VehicleReponse vehicleReponse = vehicleService.addVehicle(request, userId);


        telemetryService.saveDtl(request);
        

        apiKeyService.consumeLimit(key);

        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleReponse);
    }

    // 차량 정보
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleReponse> getById(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.getById(vehicleId));
    }



    // 차량 비상 정지
    @PostMapping("/loсkdown/{vehicleId}")
    public ResponseEntity<VehicleReponse> lokdownVehice(
            @PathVariable String vehicleId,
            @RequestBody VehicleLockReq request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String reason = (request != null && request.reason() != null) ? request.reason() : "";
        String userId = userDetails != null && userDetails.getUser() != null
                ? userDetails.getUser().getUserId()
                : "SYSTEM";

        return ResponseEntity.ok(vehicleService.lockdownVehicle(vehicleId, userId, reason));
    }
}

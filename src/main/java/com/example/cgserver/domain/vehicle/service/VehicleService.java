package com.example.cgserver.domain.vehicle.service;

import com.example.cgserver.domain.apikey.repository.ApiKeyRepository;
import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditLevel;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.service.AuditLogService;
import com.example.cgserver.domain.common.service.CommonCodeService;
import com.example.cgserver.domain.device.entity.DeviceEntity;
import com.example.cgserver.domain.device.repository.DeviceRepository;
import com.example.cgserver.domain.telemetry.entity.TelemetryLogEntity;
import com.example.cgserver.domain.telemetry.repository.TelemetryLogRepository;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DeviceRepository deviceRepository;
    private final AuditLogService auditLogService;
    private final ApiKeyRepository apiKeyRepository;

    private final TelemetryLogRepository telemetryLogRepository;
    private final CommonCodeService commonCodeService;



    public List<VehicleReponse.DeviceResponse> getVehiclesDevices(String vehicleId) {

        if (!vehicleRepository.existsById(vehicleId)){
            throw new BusinessException(ErrorCode.VEHICLE_NOT_FOUND);
        }

         List<DeviceEntity> deviceEntities = deviceRepository.findByVehicle_VehicleId(vehicleId);

         return  deviceEntities.stream().map(device -> {
             TelemetryLogEntity entity = telemetryLogRepository.findByDeviceIdOrderByRecordedAtDesc(device.getDeviceId()).orElse(null);
             return  VehicleReponse.DeviceResponse.fromEntity(device, entity);
         }).toList();
    }


    @Transactional(readOnly = true)
    public List<VehicleReponse> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(VehicleReponse::fromEntity).toList();
    }

    public VehicleReponse getById(String vehicleId) {
        VehicleEntity vehicle = vehicleRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        return VehicleReponse.fromEntity(vehicle);
    }

    @Transactional
    public VehicleReponse addVehicle(VehicleSaveReq req, String userId) {

          if (req.getApiKey() == null) {
            throw  new BusinessException(ErrorCode.API_KEY_REQUIRED);
          }


        if (req.getVehicleId() != null && !req.getVehicleId().isBlank() && vehicleRepository.existsById(req.getVehicleId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 존재하는 차량 ID입니다: " + req.getVehicleId());
        }


        if (req.getVin() != null && vehicleRepository.existsByVin(req.getVin())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 존재하는 차대번호(VIN)입니다: " + req.getVin());
        }


        if (req.getVehicleType() != null && !commonCodeService.isValidCode("VEH_TYPE", req.getVehicleType())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 차량 타입 코드입니다");
        }

        if (req.getStatus() != null && !commonCodeService.isValidCode("VEH_STATUS", req.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 차량 상태 코드입니다: " + req.getStatus());
        }

        if (req.getDevices() != null && !req.getDevices().isEmpty()) {
            for (VehicleSaveReq.DeviceSaveReq dev : req.getDevices()) {
                if (dev.getDeviceType() != null && !commonCodeService.isValidCode("DEVICE_TYPE", dev.getDeviceType())) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 디바이스 타입 코드입니다: " + dev.getDeviceType());
                }
                if (dev.getStatus() != null && !commonCodeService.isValidCode("DEVICE_STATUS", dev.getStatus())) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 디바이스 상태 코드입니다: " + dev.getStatus());
                }
            }
        }


        VehicleEntity vehicle = req.toEntity();
        VehicleEntity saved = vehicleRepository.save(vehicle);

    
        String operatorId = (userId != null && !userId.isBlank()) ? userId : "SYSTEM";
        auditLogService.record(
                operatorId,
                AuditLevel.AUDIT_INFO,
                AuditAction.CREATE_VEHICLE,
                TargetEntity.TB_VEHICLES,
                Map.of(
                        "vehicleId", saved.getVehicleId(),
                        "vin", saved.getVin(),
                        "deviceCount", saved.getDevices() != null ? String.valueOf(saved.getDevices().size()) : "0"
                )
        );

        return VehicleReponse.fromEntity(saved);
    }

    @Transactional
    public VehicleReponse lockdownVehicle(String vehicleId, String userId, String reason) {
        VehicleEntity vehicleEntity = vehicleRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicleEntity.setStatus("VEH_EMERGENCY_STOP");
        vehicleEntity.setSpeedLimit(0.0);

        auditLogService.record(
                userId,
                AuditLevel.AUDIT_SECURITY,
                AuditAction.EMERGENCY_STOP,
                TargetEntity.TB_VEHICLES,
                Map.of(
                        "vehicleId", vehicleId,
                        "reason", reason,
                        "vin", vehicleEntity.getVin()
                )
        );

        return VehicleReponse.fromEntity(vehicleEntity);
    }
}

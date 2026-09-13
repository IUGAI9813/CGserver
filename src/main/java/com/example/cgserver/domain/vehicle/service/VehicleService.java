package com.example.cgserver.domain.vehicle.service;

import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditLevel;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.service.AuditLogService;
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
    private final AuditLogService auditLogService;

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
        // 1. Проверка на дубликат ID машины
        if (vehicleRepository.existsById(req.getVehicleId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 존재하는 차량 ID입니다: " + req.getVehicleId());
        }

        // 2. Проверка на дубликаты deviceId внутри входящего массива
        if (req.getDevices() != null && !req.getDevices().isEmpty()) {
            long uniqueCount = req.getDevices().stream()
                    .map(VehicleSaveReq.DeviceSaveReq::getDeviceId)
                    .distinct()
                    .count();
            if (uniqueCount != req.getDevices().size()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "중복된 Device ID가 존재합니다.");
            }
        }

        // 3. Сохранение машины и её девайсов через каскад
        VehicleEntity vehicle = req.toEntity();
        VehicleEntity saved = vehicleRepository.save(vehicle);

        // 4. Запись в аудит
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

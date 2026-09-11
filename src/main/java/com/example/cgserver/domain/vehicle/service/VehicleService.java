package com.example.cgserver.domain.vehicle.service;

import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditEntity;
import com.example.cgserver.domain.audit.entity.AuditLevel;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.repository.AuditLogRepository;
import com.example.cgserver.domain.audit.service.AuditLogService;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {


    private final VehicleRepository vehicleRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public List<VehicleReponse> getAllVehicles(){
        return  vehicleRepository.findAll().stream().map(VehicleReponse::fromEntity).toList();
    }

    public  VehicleReponse getById(String vehicleId){

     VehicleEntity vehicle = vehicleRepository.findByVehicleId(vehicleId).orElseThrow( () -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        return VehicleReponse.fromEntity(vehicle);
    }


    @Transactional
    public VehicleReponse lockdownVehicle(String vehicleId, String userId, String reason){
        VehicleEntity vehicleEntity = vehicleRepository.findByVehicleId(vehicleId).orElseThrow( () ->
                new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicleEntity.setStatus("VEH_EMERGENCY_STOP");
        vehicleEntity.setSpeedLimit(0.0);
       // vehicleRepository.save(vehicleEntity);

        auditLogService.record(userId,
                AuditLevel.AUDIT_SECURITY,
                AuditAction.EMERGENCY_STOP,
                TargetEntity.TB_VEHICLES,
                Map.of("vehicleId",vehicleId,
                        "reason", reason,
                        "vin", vehicleEntity.getVin())
                );


        return  VehicleReponse.fromEntity(vehicleEntity);

    }
}

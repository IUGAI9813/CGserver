package com.example.cgserver.domain.vehicle.dto;

import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import lombok.*;

import java.time.OffsetDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReponse {

    private String vehicleId;
    private String vin;
    private String model;
    private String status;
    private Double speedLimit;
    private String assignedZone;
    private String firmwareVersion;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime lastPingAt;
    private OffsetDateTime createdAt;

    public static VehicleReponse fromEntity(VehicleEntity entity){
        return VehicleReponse.builder()
                .vehicleId(entity.getVehicleId())
                .vin(entity.getVin())
                .model(entity.getModel())
                .status(entity.getStatus())
                .speedLimit(entity.getSpeedLimit())
                .assignedZone(entity.getAssignedZone())
                .firmwareVersion(entity.getFirmwareVersion())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .lastPingAt(entity.getLastPingAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

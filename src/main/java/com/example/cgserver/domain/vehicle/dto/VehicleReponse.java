package com.example.cgserver.domain.vehicle.dto;

import com.example.cgserver.domain.device.entity.DeviceEntity;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReponse {

    private String vehicleId;
    private String vin;
    private String vehicleType;
    private String model;
    private String status;
    private Double speedLimit;
    private String assignedZone;
    private String firmwareVersion;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime lastPingAt;
    private OffsetDateTime createdAt;

    private List<DeviceResponse> devices;

    public static VehicleReponse fromEntity(VehicleEntity entity) {
        return VehicleReponse.builder()
                .vehicleId(entity.getVehicleId())
                .vehicleType(entity.getVehicleType())
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
                .devices(entity.getDevices() != null
                        ? entity.getDevices().stream().map(DeviceResponse::fromEntity).toList()
                        : List.of())
                .build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceResponse {
        private String deviceId;
        private String deviceType;
        private String serialNumber;
        private String status;
        private String firmwareVersion;
        private OffsetDateTime lastDiagnosticAt;
        private OffsetDateTime createdAt;

        public static DeviceResponse fromEntity(DeviceEntity entity) {
            return DeviceResponse.builder()
                    .deviceId(entity.getDeviceId())
                    .deviceType(entity.getDeviceType())
                    .serialNumber(entity.getSerialNumber())
                    .status(entity.getStatus())
                    .firmwareVersion(entity.getFirmwareVersion())
                    .lastDiagnosticAt(entity.getLastDiagnosticAt())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}

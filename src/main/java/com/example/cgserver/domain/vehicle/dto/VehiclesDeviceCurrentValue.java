package com.example.cgserver.domain.vehicle.dto;


import com.example.cgserver.domain.telemetry.dto.TelemetryResponse;
import com.example.cgserver.domain.telemetry.entity.TelemetryDtlEntity;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclesDeviceCurrentValue {


    private String vehicleId;
    private String vin;
    private String vehicleType;
    private String model;
    private String status;
    private Integer battery;
    private Double speedLimit;
    private String assignedZone;
    private String firmwareVersion;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime lastPingAt;
    private OffsetDateTime createdAt;

    private List<DeviceCurrentDto> device;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceCurrentDto {
        private String deviceId;
        private String metricType;
        private Double currentValue;
        private String unit;
        private OffsetDateTime updatedAt;
        public static DeviceCurrentDto fromEntity(TelemetryDtlEntity entity) {
            return DeviceCurrentDto.builder()
                    .deviceId(entity.getDeviceId())
                    .metricType(entity.getMetricType())
                    .currentValue(entity.getCurrentValue())
                    .unit(entity.getUnit())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    public static VehiclesDeviceCurrentValue fromEntity(VehicleEntity entity, List<TelemetryDtlEntity> dtlEntities) {
        return VehiclesDeviceCurrentValue.builder()
                .vehicleId(entity.getVehicleId())
                .vehicleType(entity.getVehicleType())
                .vin(entity.getVin())
                .model(entity.getModel())
                .status(entity.getStatus())
                .battery(entity.getBattery())
                .speedLimit(entity.getSpeedLimit())
                .assignedZone(entity.getAssignedZone())
                .firmwareVersion(entity.getFirmwareVersion())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .lastPingAt(entity.getLastPingAt())
                .createdAt(entity.getCreatedAt())
                .device(dtlEntities != null
                        ? dtlEntities.stream().map(DeviceCurrentDto::fromEntity).toList()
                        : List.of())
                .build();
    }



}

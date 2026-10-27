package com.example.cgserver.domain.vehicle.dto;

import com.example.cgserver.domain.device.entity.DeviceEntity;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSaveReq {

    private String vehicleId;
    private String apiKey;
    private String vehicleType;
    private String vin;
    private String model;
    private String status;
    private Integer battery;
    private Double speedLimit;
    private String assignedZone;
    private String firmwareVersion;
    private Double latitude;
    private Double longitude;

    private List<DeviceSaveReq> devices;

    public VehicleEntity toEntity() {
        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleId(this.vehicleId)
                .vehicleType(this.vehicleType)
                .vin(this.vin)
                .model(this.model)
                .status(this.status != null ? this.status : "VEH_ACTIVE")
                .battery(this.battery != null ? this.battery : 100)
                .speedLimit(this.speedLimit != null ? this.speedLimit : 60.0)
                .assignedZone(this.assignedZone)
                .firmwareVersion(this.firmwareVersion)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();

        if (this.devices != null) {
            this.devices.stream()
                    .map(DeviceSaveReq::toEntity)
                    .forEach(vehicle::addDevice);
        }

        return vehicle;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceSaveReq {
        private String deviceId;
        private String deviceType;
        private String serialNumber;
        private String status;
        private String metricType;
        private String firmwareVersion;

        public DeviceEntity toEntity() {
            return DeviceEntity.builder()
                    .deviceId(this.deviceId)
                    .deviceType(this.deviceType)
                    .serialNumber(this.serialNumber)
                    .status(this.status != null ? this.status : "DEV_ONLINE")
                    .firmwareVersion(this.firmwareVersion)
                    .build();
        }
    }
}

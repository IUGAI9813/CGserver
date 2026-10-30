package com.example.cgserver.domain.vehicle.entity;

import com.example.cgserver.domain.device.entity.DeviceEntity;
import com.example.cgserver.domain.telemetry.entity.TelemetryDtlEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_VEHICLES")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VehicleEntity {

    @Id
    @Column(name = "VEHICLE_ID", length = 50)
    private String vehicleId;

    @Column(name = "VEHICLE_TYPE", nullable = false, updatable = false)
    private String vehicleType;

    @Column(name = "VIN", nullable = false, unique = true, length = 17)
    private String vin;

    @Column(name = "MODEL", nullable = false, length = 50)
    private String model;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @Column(name = "BATTERY", nullable = false)
    @Builder.Default
    private Integer battery = 100;

    @Column(name = "SPEED_LIMIT", nullable = false)
    @Builder.Default
    private Double speedLimit = 60.0;

    @Column(name = "ASSIGNED_ZONE", length = 100)
    private String assignedZone;

    @Column(name = "FIRMWARE_VERSION", length = 30)
    private String firmwareVersion;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "LAST_PING_AT")
    private OffsetDateTime lastPingAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeviceEntity> devices = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        if (this.vehicleId == null || this.vehicleId.isBlank()) {
            this.vehicleId = "VEH-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        }
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        if (this.battery == null) {
            this.battery = 100;
        }
    }

    public void addDevice(DeviceEntity device) {
        devices.add(device);
        device.setVehicle(this);
    }
}

package com.example.cgserver.domain.device.entity;

import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_DEVICES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DeviceEntity {

    @Id
    @Column(name = "DEVICE_ID", length = 50)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID", nullable = false)
    private VehicleEntity vehicle;

    @Column(name = "DEVICE_TYPE", nullable = false, length = 30)
    private String deviceType;

    @Column(name = "SERIAL_NUMBER", nullable = false, length = 50)
    private String serialNumber;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @Column(name = "FIRMWARE_VERSION", length = 30)
    private String firmwareVersion;

    @Column(name = "LAST_DIAGNOSTIC_AT")
    private OffsetDateTime lastDiagnosticAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.deviceId == null || this.deviceId.isBlank()) {
            this.deviceId = "DEV-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        }
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }
}

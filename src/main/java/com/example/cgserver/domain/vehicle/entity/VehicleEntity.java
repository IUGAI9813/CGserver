package com.example.cgserver.domain.vehicle.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_VEHICLES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VehicleEntity {

    @Id
    @Column(name = "VEHICLE_ID", length = 50)
    private String vehicleId;

    @Column(name = "VIN", nullable = false, unique = true, length = 17)
    private String vin;

    @Column(name = "MODEL", nullable = false, length = 50)
    private String model;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

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

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }



}

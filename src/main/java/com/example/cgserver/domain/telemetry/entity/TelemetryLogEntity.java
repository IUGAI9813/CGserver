package com.example.cgserver.domain.telemetry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_TELEMETRY_LOGS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TelemetryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TELEMETRY_ID")
    private Long telemetryId;

    @Column(name = "VEHICLE_ID", nullable = false, length = 50)
    private String vehicleId;

    @Column(name = "DEVICE_ID", length = 50)
    private String deviceId;

    @Column(name = "METRIC_TYPE", nullable = false, length = 50)
    private String metricType;

    @Column(name = "METRIC_VALUE", nullable = false)
    private Double metricValue;

    @Column(name = "UNIT", length = 30)
    private String unit;

    @Column(name = "RECORDED_AT", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "RECEIVED_AT", nullable = false, updatable = false)
    private OffsetDateTime receivedAt;

    @PrePersist
    public void prePersist() {
        if (this.receivedAt == null) {
            this.receivedAt = OffsetDateTime.now();
        }
    }
}

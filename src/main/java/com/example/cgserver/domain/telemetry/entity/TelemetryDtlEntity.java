package com.example.cgserver.domain.telemetry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_TELEMETRY_DTL")
@IdClass(TelemetryDtlId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryDtlEntity {

    @Id
    @Column(name = "VEHICLE_ID", length = 50, nullable = false)
    private String vehicleId;

    @Id
    @Column(name = "DEVICE_ID", length = 50, nullable = false)
    private String deviceId;

    @Id
    @Column(name = "METRIC_TYPE", length = 50, nullable = false)
    private String metricType;

    @Column(name = "CURRENT_VALUE", nullable = false)
    private Double currentValue;

    @Column(name = "UNIT", length = 30)
    private String unit;

    @Column(name = "RECORDED_AT", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        if (this.updatedAt == null) {
            this.updatedAt = OffsetDateTime.now();
        }
        if (this.recordedAt == null) {
            this.recordedAt = OffsetDateTime.now();
        }
    }
}

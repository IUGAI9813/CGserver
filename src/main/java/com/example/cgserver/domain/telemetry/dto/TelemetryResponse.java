package com.example.cgserver.domain.telemetry.dto;

import com.example.cgserver.domain.telemetry.entity.TelemetryLogEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponse {

    private Long telemetryId;
    private String vehicleId;
    private String deviceId;
    private String metricType;
    private Double metricValue;
    private String unit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime recordedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime receivedAt;

    public static TelemetryResponse fromEntity(TelemetryLogEntity entity) {
        return TelemetryResponse.builder()
                .telemetryId(entity.getTelemetryId())
                .vehicleId(entity.getVehicleId())
                .deviceId(entity.getDeviceId())
                .metricType(entity.getMetricType())
                .metricValue(entity.getMetricValue())
                .unit(entity.getUnit())
                .recordedAt(entity.getRecordedAt())
                .receivedAt(entity.getReceivedAt())
                .build();
    }
}

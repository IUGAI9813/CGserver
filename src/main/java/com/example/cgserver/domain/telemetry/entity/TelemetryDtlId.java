package com.example.cgserver.domain.telemetry.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TelemetryDtlId implements Serializable {

    private String vehicleId;
    private String deviceId;
    private String metricType;
}

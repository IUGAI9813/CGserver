package com.example.cgserver.domain.telemetry.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetrySaveReq {

    private String vehicleId;
    private String deviceId;
    private String metricType;
    private Double metricValue;
    private String unit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordedAt;
}

package com.example.cgserver.domain.telemetry.controller;

import com.example.cgserver.domain.telemetry.dto.TelemetryResponse;
import com.example.cgserver.domain.telemetry.dto.TelemetrySaveReq;
import com.example.cgserver.domain.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;


    @PostMapping
    public ResponseEntity<TelemetryResponse> saveTelemetry(@RequestBody TelemetrySaveReq request) {
        TelemetryResponse response = telemetryService.saveTelemetry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<TelemetryResponse>> saveBatchTelemetry(@RequestBody List<TelemetrySaveReq> requests) {
        List<TelemetryResponse> responses = telemetryService.saveBatchTelemetry(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }


}

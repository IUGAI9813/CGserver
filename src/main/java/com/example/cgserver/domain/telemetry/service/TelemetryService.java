package com.example.cgserver.domain.telemetry.service;

import com.example.cgserver.domain.device.repository.DeviceRepository;
import com.example.cgserver.domain.telemetry.dto.TelemetryResponse;
import com.example.cgserver.domain.telemetry.dto.TelemetrySaveReq;
import com.example.cgserver.domain.telemetry.entity.TelemetryLogEntity;
import com.example.cgserver.domain.telemetry.repository.TelemetryLogRepository;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryLogRepository telemetryLogRepository;
    private final DeviceRepository deviceRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public TelemetryResponse saveTelemetry(TelemetrySaveReq req) {
        // Проверяем существование транспортного средства
        if (!vehicleRepository.existsById(req.getVehicleId())) {
            throw new BusinessException(ErrorCode.VEHICLE_NOT_FOUND);
        }

        if (!deviceRepository.existsById(req.getDeviceId())){
            throw  new BusinessException(ErrorCode.DEVICE_NOT_FOUND);
        }

        OffsetDateTime recordedAt = req.getRecordedAt() != null
                ? req.getRecordedAt().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                : OffsetDateTime.now();



        TelemetryLogEntity entity = TelemetryLogEntity.builder()
                .vehicleId(req.getVehicleId())
                .deviceId(req.getDeviceId())
                .metricType(req.getMetricType())
                .metricValue(req.getMetricValue())
                .unit(req.getUnit())
                .recordedAt(recordedAt)
                .build();

        TelemetryLogEntity saved = telemetryLogRepository.save(entity);
        return TelemetryResponse.fromEntity(saved);
    }

    @Transactional
    public List<TelemetryResponse> saveBatchTelemetry(List<TelemetrySaveReq> requests) {
        List<TelemetryLogEntity> entities = requests.stream().map(req -> {
            if (!vehicleRepository.existsById(req.getVehicleId())) {
                throw new BusinessException(ErrorCode.VEHICLE_NOT_FOUND);
            }

            OffsetDateTime recordedAt = req.getRecordedAt() != null
                    ? req.getRecordedAt().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    : OffsetDateTime.now();

            return TelemetryLogEntity.builder()
                    .vehicleId(req.getVehicleId())
                    .deviceId(req.getDeviceId())
                    .metricType(req.getMetricType())
                    .metricValue(req.getMetricValue())
                    .unit(req.getUnit())
                    .recordedAt(recordedAt)
                    .build();
        }).toList();

        List<TelemetryLogEntity> saved = telemetryLogRepository.saveAll(entities);
        return saved.stream().map(TelemetryResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> getRecentByVehicle(String vehicleId) {
        return telemetryLogRepository.findByVehicleIdOrderByRecordedAtDesc(vehicleId)
                .stream()
                .map(TelemetryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> getByVehicleAndMetric(String vehicleId, String metricType) {
        return telemetryLogRepository.findByVehicleIdAndMetricTypeOrderByRecordedAtDesc(vehicleId, metricType)
                .stream()
                .map(TelemetryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> getByRange(String vehicleId, OffsetDateTime from, OffsetDateTime to) {
        return telemetryLogRepository.findByVehicleIdAndRecordedAtBetweenOrderByRecordedAtAsc(vehicleId, from, to)
                .stream()
                .map(TelemetryResponse::fromEntity)
                .toList();
    }
}

package com.example.cgserver.domain.telemetry.repository;

import com.example.cgserver.domain.telemetry.entity.TelemetryLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryLogRepository extends JpaRepository<TelemetryLogEntity, Long> {

    List<TelemetryLogEntity> findByVehicleId(String vehicleId);

    List<TelemetryLogEntity> findByVehicleIdOrderByRecordedAtDesc(String vehicleId);

    List<TelemetryLogEntity> findByVehicleIdAndMetricTypeOrderByRecordedAtDesc(String vehicleId, String metricType);

    List<TelemetryLogEntity> findByVehicleIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            String vehicleId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    Optional<TelemetryLogEntity> findTopByVehicleIdAndMetricTypeOrderByRecordedAtDesc(String vehicleId, String metricType);
}

package com.example.cgserver.domain.telemetry.repository;

import com.example.cgserver.domain.telemetry.entity.TelemetryDtlEntity;
import com.example.cgserver.domain.telemetry.entity.TelemetryDtlId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryDtlRepository extends JpaRepository<TelemetryDtlEntity, TelemetryDtlId> {

    List<TelemetryDtlEntity> findByVehicleId(String vehicleId);

    List<TelemetryDtlEntity> findByVehicleIdAndDeviceId(String vehicleId, String deviceId);

    Optional<TelemetryDtlEntity> findByVehicleIdAndDeviceIdAndMetricType(String vehicleId, String deviceId, String metricType);

    void deleteByVehicleId(String vehicleId);
}

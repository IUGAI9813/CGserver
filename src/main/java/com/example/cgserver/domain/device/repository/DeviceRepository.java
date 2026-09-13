package com.example.cgserver.domain.device.repository;

import com.example.cgserver.domain.device.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {

    List<DeviceEntity> findByVehicle_VehicleId(String vehicleId);
}

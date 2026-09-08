package com.example.cgserver.domain.vehicle.repository;

import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {

   Optional<VehicleEntity> findByVehicleId(String vehicleId);
}

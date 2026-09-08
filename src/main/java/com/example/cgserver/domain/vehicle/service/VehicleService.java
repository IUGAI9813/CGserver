package com.example.cgserver.domain.vehicle.service;

import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {


    private final VehicleRepository repository;

    @Transactional(readOnly = true)
    public List<VehicleReponse> getAllVehicles(){
        return  repository.findAll().stream().map(VehicleReponse::fromEntity).toList();
    }

    public  VehicleReponse getById(String vehicleId){

     VehicleEntity vehicle = repository.findByVehicleId(vehicleId).orElseThrow( () -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        return VehicleReponse.fromEntity(vehicle);
    }
}

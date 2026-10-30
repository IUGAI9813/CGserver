package com.example.cgserver.domain.vehicle.service;


import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.entity.KeyType;
import com.example.cgserver.domain.apikey.service.ApiKeyService;
import com.example.cgserver.domain.telemetry.service.TelemetryService;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleRegisterService {

    private final VehicleService vehicleService;

    private final TelemetryService telemetryService;

    private final ApiKeyService apiKeyService;



    @Transactional
    public VehicleReponse registVehicle(VehicleSaveReq request ,String userId ){

        ApiKeyEntity key =  apiKeyService.apiKeyValidate(request.getApiKey(), KeyType.VEHICLE);

        if (userId == null) {
            userId = key.getCreatedBy() != null ? key.getCreatedBy() : "SYSTEM";
        }

        VehicleReponse vehicleReponse = vehicleService.addVehicle(request, userId);

        telemetryService.saveDtl(request);

        apiKeyService.consumeLimit(key);

        return  vehicleReponse;
    }



}

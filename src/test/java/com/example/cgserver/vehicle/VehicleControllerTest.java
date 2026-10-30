package com.example.cgserver.vehicle;


import com.example.cgserver.domain.apikey.entity.ApiKeyEntity;
import com.example.cgserver.domain.apikey.entity.KeyType;
import com.example.cgserver.domain.apikey.service.ApiKeyService;
import com.example.cgserver.domain.telemetry.controller.TelemetryController;
import com.example.cgserver.domain.telemetry.service.TelemetryService;
import com.example.cgserver.domain.vehicle.controller.VehicleController;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import com.example.cgserver.domain.vehicle.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {


    @Mock
    private ApiKeyService apiKeyService;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private TelemetryService telemetryService;



    @InjectMocks
    private VehicleController vehicleController;

    @Test
    void shouldReturnVehicleById(){

        VehicleReponse vehicleReponse = mock(VehicleReponse.class);
        String vehId = "VEH-002";

        when(vehicleService.getById(vehId)).thenReturn(vehicleReponse);

        ResponseEntity<VehicleReponse>  response  = vehicleController.getById(vehId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleReponse);

        verify(vehicleService).getById(vehId);


    }



    @Test
    void shouldReturnAllVehicles(){

        List<VehicleReponse> vehicleReponseList = List.of(
                mock(VehicleReponse.class),
                mock(VehicleReponse.class)
        );

        when(vehicleService.getAllVehicles()).thenReturn(vehicleReponseList);

        ResponseEntity<List<VehicleReponse>> response = vehicleController.getList();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleReponseList);

        verify(vehicleService).getAllVehicles();


    }

    @Test
    void shouldReturnOneVehicle() {

        List<VehicleReponse.DeviceResponse> vehicleReponseList = List.of(
                mock(VehicleReponse.DeviceResponse.class)
        );

        when(vehicleService.getVehiclesDevices("VEH-01")).thenReturn(vehicleReponseList);

        ResponseEntity<List<VehicleReponse.DeviceResponse>> response = vehicleController.getVehicleDevices("VEH-01");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(vehicleReponseList);

        verify(vehicleService).getVehiclesDevices("VEH-01");



    }



    @Test
    @DisplayName("차량등록시 예외가 발생하면 텔레메트리 키 사용 한도가 차감/처리되지 않아야 한다")
    void shouldNotConsumeTelemetryKeyQuotaWhenVehicleRegistrationFails() {

        VehicleSaveReq saveReq = VehicleSaveReq.builder()
                .apiKey("test-api-key")
                .vehicleId("VEH-001")
                .build();


        ApiKeyEntity key = mock(ApiKeyEntity.class);

        when(key.getCreatedBy()).thenReturn("some-user");

        when(apiKeyService.apiKeyValidate(saveReq.getApiKey(), KeyType.VEHICLE)).thenReturn(key);


        when(vehicleService.addVehicle(saveReq, "test-user")).thenThrow(new RuntimeException("DB error"));


        assertThatThrownBy(() -> vehicleController.addVehicle(saveReq , null)).isInstanceOf(RuntimeException.class);


        verify(telemetryService, never()).saveDtl(any());
        verify(apiKeyService, never()).consumeLimit(any());


    }


}

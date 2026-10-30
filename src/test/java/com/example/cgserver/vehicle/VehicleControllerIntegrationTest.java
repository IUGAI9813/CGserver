package com.example.cgserver.vehicle;

import com.example.cgserver.domain.telemetry.service.TelemetryService;
import com.example.cgserver.domain.vehicle.controller.VehicleController;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class VehicleControllerIntegrationTest {

    @Autowired
    private VehicleController vehicleController;

    @Autowired
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private TelemetryService telemetryService;

    @Test
    void shouldCheckVehiclePersistenceWhenTelemetryFails() {
        String vehicleId = "VEH-TEST-001";

        VehicleSaveReq request = VehicleSaveReq.builder()
                .apiKey("valid-test-api-key")
                .vehicleId(vehicleId)
                .vehicleType("VEH_TYPE_TEST")
                .vin("TEST-VIN-001")
                .model("Test Model")
                .status("VEH_ACTIVE")
                .battery(100)
                .speedLimit(60.0)
                .assignedZone("ZONE-001")
                .firmwareVersion("1.0.0")
                .build();


        doThrow(new RuntimeException("Telemetry DB error"))
                .when(telemetryService)
                .saveDtl(request);
        try {
            vehicleController.addVehicle(request, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        assertThat(vehicleRepository.existsById(vehicleId))
                .isTrue();
    }


}

package com.example.cgserver.vehicle;


import com.example.cgserver.domain.audit.service.AuditLogService;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import com.example.cgserver.domain.vehicle.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VehicleServiceIntegrationTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private AuditLogService auditLogService;

    @Autowired
    private ApplicationContext context;


    @Test
    void checkBeans() {

        String[] beans = context.getBeanDefinitionNames();

        Arrays.stream(beans)
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void  checkBean() {

        VehicleService test = context.getBean(VehicleService.class);
        VehicleService test2 = context.getBean(VehicleService.class);

        System.out.println(test);
        System.out.println(test2);

        assertThat(test).isEqualTo(test2);

    }

    @Test
    void whenAuditLogFails_vehicleRegistrationShouldRollback() {

        // Arrange

        String vehicleId = "ROBO-ROLLBACK-01";

        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("test-api-key")
                .vehicleId(vehicleId)
                .vehicleType("ROBOTAXI")
                .model("Hyndai")
                .status("VEH_ACTIVE")
                .battery(100)
                .speedLimit(40.0)
                .assignedZone("Test")
                .firmwareVersion("F23")
                .latitude(36.77)
                .latitude(38.34)
                .vin("VIN-ROLLBACK-01")
                .devices(List.of())
                .build();

        doThrow(new RuntimeException("Audit log failed"))
                .when(auditLogService)
                .record(
                        anyString(),
                        any(),
                        any(),
                        any(),
                        anyMap()
                );

        // Act & Assert

        assertThatThrownBy(() ->
                vehicleService.addVehicle(req, "USER-01")
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Audit log failed");




        verify(auditLogService, times(1))
                .record(
                        anyString(),
                        any(),
                        any(),
                        any(),
                        anyMap()
                );


        assertThat(vehicleRepository.existsById(vehicleId))
                .isFalse();
    }



}

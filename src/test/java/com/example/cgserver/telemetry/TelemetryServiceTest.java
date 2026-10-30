package com.example.cgserver.telemetry;


import com.example.cgserver.domain.telemetry.entity.TelemetryDtlEntity;
import com.example.cgserver.domain.telemetry.repository.TelemetryDtlRepository;
import com.example.cgserver.domain.telemetry.service.TelemetryService;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TelemetryServiceTest {



    @Mock
    private  TelemetryDtlRepository telemetryDtlRepository;

    @InjectMocks
    private TelemetryService telemetryService;

    @Test
    @DisplayName("차량등록 시 텔레메트리 성공적으로 등록합니다")
    void save() {
        VehicleSaveReq.DeviceSaveReq device1 = VehicleSaveReq.DeviceSaveReq.builder()
                .deviceId("DEVICE-01")
                .deviceType("DEV_RADAR")
                .serialNumber("AES0001")
                .status("ACTIVE")
                .metricType("RADAR_GPS")
                .firmwareVersion("V1")
                .build();

        VehicleSaveReq vehicleSaveReq = VehicleSaveReq.builder()
                .vehicleId("VEHI1")
                .devices(List.of(device1))
                .build();

        telemetryService.saveDtl(vehicleSaveReq);

        ArgumentCaptor<TelemetryDtlEntity> entityCaptor = ArgumentCaptor.forClass(TelemetryDtlEntity.class);
        verify(telemetryDtlRepository).save(entityCaptor.capture());

        TelemetryDtlEntity result = entityCaptor.getValue();

        assertThat(result).isNotNull();
        assertThat(0.0).isEqualTo(result.getCurrentValue());
        assertThat(result.getVehicleId()).isEqualTo("VEHI1");
        assertThat(result.getDeviceId()).isEqualTo("DEVICE-01");
        assertThat(result.getMetricType()).isEqualTo("RADAR_GPS");
        assertThat(result.getRecordedAt()).isNotNull();


    }


}

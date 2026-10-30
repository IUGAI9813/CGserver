package com.example.cgserver.vehicle;

import com.example.cgserver.domain.audit.entity.AuditAction;
import com.example.cgserver.domain.audit.entity.AuditLevel;
import com.example.cgserver.domain.audit.entity.TargetEntity;
import com.example.cgserver.domain.audit.service.AuditLogService;
import com.example.cgserver.domain.common.service.CommonCodeService;
import com.example.cgserver.domain.vehicle.dto.VehicleReponse;
import com.example.cgserver.domain.vehicle.dto.VehicleSaveReq;
import com.example.cgserver.domain.vehicle.entity.VehicleEntity;
import com.example.cgserver.domain.vehicle.repository.VehicleRepository;
import com.example.cgserver.domain.vehicle.service.VehicleService;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {


    @Mock
    private VehicleRepository repository;

    @Mock
    private  AuditLogService auditLogService;

    @Mock
    private CommonCodeService codeService;

    @InjectMocks
    private VehicleService service;

    @Test
    @DisplayName("여러 디바이스가 포함된 차량 등록 성공")
    void saveSuccessfully_withMultipleDevices() {
        String userId = "USER-01";

        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("saved-key")
                .vin("VIN-001")
                .vehicleType("TAXI")
                .devices(List.of(
                        VehicleSaveReq.DeviceSaveReq.builder().deviceType("LIDAR").build(),
                        VehicleSaveReq.DeviceSaveReq.builder().deviceType("RADAR").build(),
                        VehicleSaveReq.DeviceSaveReq.builder().deviceType("CAMERA").build()
                ))
                .vehicleId("ROBO-01").build();


        when(repository.existsById("ROBO-01")).thenReturn(false);

        when(repository.existsByVin("VIN-001")).thenReturn(false);

        when(codeService.isValidCode("DEVICE_TYPE", req.getDevices().get(0).getDeviceType())).thenReturn(true);
        when(codeService.isValidCode("DEVICE_TYPE", req.getDevices().get(1).getDeviceType())).thenReturn(true);
        when(codeService.isValidCode("DEVICE_TYPE", req.getDevices().get(2).getDeviceType())).thenReturn(true);


        when(codeService.isValidCode("VEH_TYPE", "TAXI"))
                .thenReturn(true);

        when(repository.save(any(VehicleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        service.addVehicle(req, userId);

        ArgumentCaptor<VehicleEntity> captor = ArgumentCaptor.forClass(VehicleEntity.class);

        verify(repository).save(captor.capture());

        VehicleEntity entity = captor.getValue();

        assertThat(entity.getDevices())
                .extracting("deviceType")
                .containsExactly("LIDAR", "RADAR", "CAMERA");
        assertThat(entity.getDevices()).hasSize(3);

        verify(auditLogService).record(
                eq(userId),
                eq(AuditLevel.AUDIT_INFO),
                eq(AuditAction.CREATE_VEHICLE),
                eq(TargetEntity.TB_VEHICLES),
                anyMap()
        );

    }

    @Test
    @DisplayName("잘 못 된 차량의 디바이스 타입")
    void whenRegistIncorrectDeviceTypeValue_throwException() {
        String userId = "USER-01";



        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("saved-key")
                .vin("VIN-001")
                .vehicleType("POLICE")
                .devices(List.of( VehicleSaveReq.DeviceSaveReq.builder()
                        .deviceType("INVALID_DEVICE")
                        .build()))
                .vehicleId("ROBO-01").build();


        when(repository.existsById("ROBO-01")).thenReturn(false);
        when(repository.existsByVin("VIN-001")).thenReturn(false);

        when(codeService.isValidCode("DEVICE_TYPE", req.getDevices().get(0).getDeviceType())).thenReturn(false);
        when(codeService.isValidCode("VEH_TYPE", "POLICE"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.addVehicle(req, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("유효하지 않은 디바이스 타입 코드입니다: " + req.getDevices().get(0).getDeviceType());

        verifyNoInteractions(auditLogService);
        verify(repository, never()).save(any(VehicleEntity.class));


    }

    @Test
    @DisplayName("잘 못 된 차량 타입")
    void whenRegistIncorrectVehicleTypeValue_throwException() {
        String userId = "USER-01";


        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("saved-key")
                .vin("VIN-001")
                .vehicleType("POLICE")
                .vehicleId("ROBO-01").build();


        when(codeService.isValidCode("VEH_TYPE", req.getVehicleType())).thenReturn(false);


        assertThatThrownBy(() -> service.addVehicle(req, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("유효하지 않은 차량 타입 코드입니다");

        verify(codeService).isValidCode("VEH_TYPE","POLICE");
        verifyNoInteractions(auditLogService);
        verify(repository, never()).save(any(VehicleEntity.class));


    }

    @Test
    @DisplayName("등록할 VIN 이미 존재하면 예외 발생")
    void whenRegistExistedVin_throwException(){
        String userId = "USER-01";

        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("saved-key")
                .vin("VIN-001")
                .vehicleId("ROBO-01").build();

        when(repository.existsByVin("VIN-001")).thenReturn(true);

        assertThatThrownBy(() -> service.addVehicle(req, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 차대번호(VIN)입니다: " + req.getVin());

        verify(repository).existsByVin("VIN-001");
        verifyNoInteractions(auditLogService);
        verify(repository, never()).save(any(VehicleEntity.class));


    }


    @Test
    @DisplayName("등록할 차량 아이디가 이미 존재하면 예외 발생")
    void whenRegistExistedVehicleId_throwException(){
        String userId = "USER-01";


        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("saved-key")
                .vin("VIN-001")
                .vehicleId("ROBO-01").build();

        when(repository.existsById("ROBO-01")).thenReturn(true);

        assertThatThrownBy(() -> service.addVehicle(req, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 존재하는 차량 ID입니다: " + req.getVehicleId());

        verify(repository).existsById("ROBO-01");
        verifyNoInteractions(auditLogService);
        verify(repository, never()).save(any(VehicleEntity.class));


    }

    @Test
    @DisplayName("API Key가 없으면 차량 등록에 실패한다")
    void whenDoNotProvideApiKey_throwException(){
        String userId = "USER-01";


        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey(null)
                .vin("VIN-001")
                .vehicleId("ROBO-01")
                .build();

        assertThatThrownBy(() -> service.addVehicle(req,  userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.API_KEY_REQUIRED.getMessage());


        verifyNoInteractions(repository);
        verifyNoInteractions(auditLogService);

    }


    @Test
    void whenAddVehicle_successfulyRegist(){
        String userId = "USER-01";


        VehicleSaveReq req = VehicleSaveReq.builder()
                .apiKey("saveCar")
                .vin("VIN-001")
                .vehicleId("ROBO-01")
                .build();

        when(repository.existsById("ROBO-01")).thenReturn(false);

        when(repository.existsByVin("VIN-001")).thenReturn(false);

        when(repository.save(any(VehicleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        service.addVehicle(req, userId);

        ArgumentCaptor<VehicleEntity> captor = ArgumentCaptor.forClass(VehicleEntity.class);

        verify(repository).save(captor.capture());

        VehicleEntity vehicle = captor.getValue();

        assertThat(vehicle.getVehicleId()).isEqualTo("ROBO-01");
        assertThat(vehicle.getVin()).isEqualTo("VIN-001");

        verify(auditLogService).record(
                eq(userId),
                eq(AuditLevel.AUDIT_INFO),
                eq(AuditAction.CREATE_VEHICLE),
                eq(TargetEntity.TB_VEHICLES),
                anyMap()
        );
    }



    @Test
    @DisplayName("차량 등록 시 감사 로그에 정확한 정보가 기록된다")
    void whenAddVehicle_thenAuditLogContainsCorrectData(){

        String vehicleId = "ROBO-01";
        String userId = "USER-01";
        String vin = "VIN-001";


        VehicleSaveReq req = VehicleSaveReq.builder()
                    .apiKey("saveCar")
                    .vin(vin)
                .vehicleId(vehicleId)
                .build();


        VehicleEntity vehicle = req.toEntity();

        when(repository.save(any(VehicleEntity.class))).thenReturn(vehicle);


        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);


        service.addVehicle(req, userId);

        verify(auditLogService).record(
                eq(userId),
                eq(AuditLevel.AUDIT_INFO),
                eq(AuditAction.CREATE_VEHICLE),
                eq(TargetEntity.TB_VEHICLES),
                captor.capture()
        );

        Map<String, Object> capturedMap = captor.getValue();

        assertThat(capturedMap)
                .containsEntry("vehicleId", vehicleId)
                .containsEntry("vin", vin)
                .containsEntry("deviceCount", "0");

    }


    @Test
    @DisplayName("비상 정치 정상적으로 동작한다")
    void whenLockDown_thenSuccessfullSetState(){

        String vehicleId = "ROBO-01";

        String userId = "USER-01";

        String reason  = "테스트 청지";

        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleId("ROBO-01")
                .status("NORMAL")
                .speedLimit(60.0)
                .vin("VIN-991")
                .build();

        when(repository.findByVehicleId(vehicleId)).thenReturn(Optional.of(vehicle));

        VehicleReponse response = service.lockdownVehicle(vehicleId, userId , reason);


        assertThat(response.getStatus()).isEqualTo("VEH_EMERGENCY_STOP");
        assertThat(response.getSpeedLimit()).isEqualTo(0.0);



    }

    @Test
    @DisplayName("존재하지 않는 차량이면 예외가 발생한다")
    void whenVehicleNotFound_throwException(){

        String vehicleId = "ROBO-03";

        String userId = "USER-01";

        String reason  = "테스트 청지";


        when(repository.findByVehicleId(vehicleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.lockdownVehicle(vehicleId,  userId, reason))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.VEHICLE_NOT_FOUND.getMessage());


        verifyNoInteractions(auditLogService);

    }


    @Test
    @DisplayName("비상 정지 시 감사 로그에 정확한 정보가 기록된다")
    void whenLockdown_thenAuditLogContainsCorrectData() {

        String vehicleId = "ROBO-01";
        String userId = "USER-01";
        String reason = "테스트 정지";

        VehicleEntity vehicle = VehicleEntity.builder()
                .vehicleId("ROBO-01")
                .status("NORMAL")
                .speedLimit(60.0)
                .vin("VIN-001")
                .build();

        when(repository.findByVehicleId(vehicleId))
                .thenReturn(Optional.of(vehicle));


        ArgumentCaptor<Map<String, Object>> captor =
                ArgumentCaptor.forClass(Map.class);

        service.lockdownVehicle(vehicleId, userId, reason);

        verify(auditLogService).record(
                eq(userId),
                eq(AuditLevel.AUDIT_SECURITY),
                eq(AuditAction.EMERGENCY_STOP),
                eq(TargetEntity.TB_VEHICLES),
                captor.capture()
        );

        Map<String, Object> capturedMap = captor.getValue();

        assertThat(capturedMap)
                .containsEntry("vehicleId", vehicleId)
                .containsEntry("reason", reason)
                .containsEntry("vin", "VIN-001");

         }
    }

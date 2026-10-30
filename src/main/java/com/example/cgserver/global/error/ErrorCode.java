package com.example.cgserver.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "U002", "이미 등록된 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U003", "비밀번호가 일치하지 않습니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "U004", "계정이 잠겨 있습니다."),


    //Vehicle
    VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "V001", "차량 찾을 수 없습니다."),
    //DEVICE
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "디바이스 찾을 수 없습니다"),

    // Auth & JWT
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),

    // 2. Когда API-ключ неверный или не найден в базе
    INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않은 API 인증 키입니다."),
    // 3. (Полезно) Когда ключ отозван (REVOKED) или истёк
    EXPIRED_API_KEY(HttpStatus.UNAUTHORIZED, "A005", "폐기되었거나 만료된 API 키입니다."),
    // 4. (Полезно) Когда ключ рабочий, но его тип не подходит (например, TELEMETRY вместо VEHICLE)
    FORBIDDEN_KEY_TYPE(HttpStatus.FORBIDDEN, "A006", "차량 등록 권한이 없는 API 키 유형입니다."),
    DAILY_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "A007", "한도 초과되었습니다"),
    API_KEY_REQUIRED(HttpStatus.UNAUTHORIZED, "A003", "API 인증 키가 누락되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

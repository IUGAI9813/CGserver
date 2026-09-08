package com.example.cgserver.domain.audit.entity;

public enum AuditAction {
    EMERGENCY_STOP,
    VEHICLE_LOCKDOWN,
    UPDATE_THRESHOLDS,
    CREATE_POLICY,
    UPDATE_POLICY,
    DELETE_POLICY,
    ACK_INCIDENT,
    RESOLVE_INCIDENT,
    CREATE_API_KEY,
    REVOKE_API_KEY,
    USER_LOGIN,
    USER_LOGOUT,
    ACCOUNT_LOCKED
}

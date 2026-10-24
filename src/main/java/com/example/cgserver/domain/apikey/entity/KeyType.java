package com.example.cgserver.domain.apikey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyType {
    TELEMETRY(5000),
    DEVICE(50),
    VEHICLE(50);

    private final int demoDailyLimit;
}

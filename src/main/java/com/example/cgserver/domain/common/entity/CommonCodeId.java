package com.example.cgserver.domain.common.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommonCodeId implements Serializable {

    private String groupCode;
    private String code;
}

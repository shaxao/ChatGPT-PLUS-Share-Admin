package com.louwei.gptresource.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NumberValue {

    REFRESHCOUNT(5);

    private Integer value;
}

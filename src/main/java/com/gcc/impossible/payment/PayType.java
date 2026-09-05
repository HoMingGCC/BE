package com.gcc.impossible.payment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PayType {
    CHECK("check"),
    CREDIT("credit"),
    PREPAID("prepaid");

    private final String value;

    PayType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}

package com.gcc.impossible.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** 국세청 사업자등록상태 조회 결과 — FE src/types/index.ts Store.status: 'open' | 'closed' 와 동일 */
public enum StoreStatus {
    OPEN("open"),
    CLOSED("closed");

    private final String value;

    StoreStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static StoreStatus fromValue(String value) {
        for (StoreStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("알 수 없는 가게 상태: " + value);
    }
}

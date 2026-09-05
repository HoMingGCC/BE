package com.gcc.impossible.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** FE src/types/index.ts Payment.status: 'approved' | 'canceled' 와 동일 */
public enum PaymentStatus {
    APPROVED("approved"),
    CANCELED("canceled");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("알 수 없는 결제 상태: " + value);
    }
}

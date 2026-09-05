package com.gcc.impossible.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * v1/v2/v3 데이터 확보 단계 구분 (FE Payment.source 와 동일).
 * localpay = 지역화폐(자사 원장, 동의 불필요) / im-card = 당행 카드 / other-card = 타 카드사 마이데이터
 */
public enum PaySource {
    LOCALPAY("localpay"),
    IM_CARD("im-card"),
    OTHER_CARD("other-card");

    private final String value;

    PaySource(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static PaySource fromValue(String value) {
        for (PaySource source : values()) {
            if (source.value.equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("알 수 없는 결제 소스: " + value);
    }
}

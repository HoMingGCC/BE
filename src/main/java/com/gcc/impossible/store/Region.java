package com.gcc.impossible.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * FE src/types/index.ts Store.region: 'daegu' | 'pohang' 와 동일.
 * SEOUL은 이주감지 트리거용 가상 가게(행복공인중개사·이케아)에만 쓰인다 — FE 타입엔 아직 없음, 추가 필요.
 */
public enum Region {
    DAEGU("daegu"),
    POHANG("pohang"),
    SEOUL("seoul");

    private final String value;

    Region(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Region fromValue(String value) {
        for (Region region : values()) {
            if (region.value.equalsIgnoreCase(value)) {
                return region;
            }
        }
        throw new IllegalArgumentException("알 수 없는 지역: " + value);
    }
}

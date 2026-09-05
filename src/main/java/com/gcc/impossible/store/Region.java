package com.gcc.impossible.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** FE src/types/index.ts Store.region: 'daegu' | 'pohang' 와 동일 */
public enum Region {
    DAEGU("daegu"),
    POHANG("pohang");

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

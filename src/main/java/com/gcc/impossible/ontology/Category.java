package com.gcc.impossible.ontology;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** FE src/lib/ontology.ts 의 Category 와 동일 */
public enum Category {
    MEAL("meal"),
    CAFE("cafe"),
    DRINK("drink"),
    LIFE("life");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Category fromValue(String value) {
        for (Category category : values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("알 수 없는 카테고리: " + value);
    }
}

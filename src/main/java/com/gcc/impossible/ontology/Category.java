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

    /**
     * 업종(분식/카페/미용실/문구/술집 등) → 지도 아이콘·랭킹 필터용 4종 버킷.
     * 데이터팀 자료엔 없는, 순수 화면 표시용 그룹핑이라 여기 고정값으로 둔다 (FE ontology.ts와 동일).
     */
    public static Category bucketOf(String category) {
        if (category == null) {
            return null;
        }
        return switch (category) {
            case "분식" -> MEAL;
            case "카페" -> CAFE;
            case "술집" -> DRINK;
            default -> LIFE;
        };
    }
}

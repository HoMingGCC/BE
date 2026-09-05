package com.gcc.impossible.store;

import com.gcc.impossible.ontology.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/** FE src/types/index.ts 의 Store 타입과 필드를 맞춘 응답 DTO */
public record StoreDto(
        @Schema(example = "514-81-10001") String regno,
        @Schema(example = "봉덕 분식") String name,
        @Schema(example = "대구 남구 봉덕로 21") String address,
        @Schema(example = "35.8421") double lat,
        @Schema(example = "128.5936") double lng,
        @Schema(example = "분식") String industry,
        Category category,
        StoreStatus status,
        @Schema(example = "2024-12-11") LocalDate closedAt,
        @Schema(example = "true") boolean localPayMerchant,
        @Schema(example = "10:00–20:00") String hours,
        @Schema(example = "일요일") String holiday,
        Region region,
        @Schema(example = "남구") String district) {

    public static StoreDto from(Store store) {
        return new StoreDto(
                store.getRegno(),
                store.getName(),
                store.getAddress(),
                store.getLat(),
                store.getLng(),
                store.getIndustry(),
                store.getCategory(),
                store.getStatus(),
                store.getClosedAt(),
                store.isLocalPayMerchant(),
                store.getHours(),
                store.getHoliday(),
                store.getRegion(),
                store.getDistrict());
    }
}

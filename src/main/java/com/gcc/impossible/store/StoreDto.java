package com.gcc.impossible.store;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * 가게 마스터 응답 DTO — 데이터팀 가게마스터 필드명 그대로 (`category` = 업종, 온톨로지 매칭용).
 * FE src/types/index.ts 의 Store 타입은 아직 industry/category 두 필드로 나뉘어 있어 이 시점 기준으로는 안 맞음 — FE 연동 시 함께 정리 필요.
 */
public record StoreDto(
        @Schema(example = "111-11-11111") String regno,
        @Schema(example = "태산만두 본점") String name,
        @Schema(example = "대구 중구 달구벌대로 2109-32 1층") String address,
        @Schema(example = "35.866038") double lat,
        @Schema(example = "128.593745") double lng,
        @Schema(example = "분식") String category,
        StoreStatus status,
        @Schema(example = "2024-12-11") LocalDate closedAt,
        @Schema(example = "true") boolean localPayMerchant,
        @Schema(example = "10:00–20:00") String hours,
        @Schema(example = "일요일") String holiday,
        Region region,
        @Schema(example = "중구") String district) {

    public static StoreDto from(Store store) {
        return new StoreDto(
                store.getRegno(),
                store.getName(),
                store.getAddress(),
                store.getLat(),
                store.getLng(),
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

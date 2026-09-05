package com.gcc.impossible.store;

import com.gcc.impossible.ontology.Category;
import java.time.LocalDate;

/** FE src/types/index.ts 의 Store 타입과 필드를 맞춘 응답 DTO */
public record StoreDto(
        String regno,
        String name,
        String address,
        double lat,
        double lng,
        String industry,
        Category category,
        StoreStatus status,
        LocalDate closedAt,
        boolean localPayMerchant,
        String hours,
        String holiday,
        Region region,
        String district) {

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

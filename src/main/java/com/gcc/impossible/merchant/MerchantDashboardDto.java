package com.gcc.impossible.merchant;

import com.gcc.impossible.store.StoreDto;

/** FE src/types/index.ts 의 MerchantDashboard 타입과 필드를 맞춘 응답 DTO */
public record MerchantDashboardDto(
        StoreDto store,
        int regularCount,
        int dormantCount,
        int newThisMonth,
        int rankThreshold,
        int weeklySlotUsed,
        int weeklySlotTotal) {
}

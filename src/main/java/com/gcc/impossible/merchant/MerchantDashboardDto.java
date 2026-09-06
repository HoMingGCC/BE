package com.gcc.impossible.merchant;

import com.gcc.impossible.store.StoreDto;
import io.swagger.v3.oas.annotations.media.Schema;

/** FE src/types/index.ts 의 MerchantDashboard 타입과 필드를 맞춘 응답 DTO */
public record MerchantDashboardDto(
        StoreDto store,
        @Schema(example = "12") int regularCount,
        @Schema(example = "3") int dormantCount,
        @Schema(example = "2") int newThisMonth,
        @Schema(example = "20") int rankThreshold,
        @Schema(example = "1") int weeklySlotUsed,
        @Schema(example = "2") int weeklySlotTotal) {
}

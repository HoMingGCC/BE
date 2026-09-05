package com.gcc.impossible.regular;

import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.store.StoreDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** FE src/types/index.ts 의 RegularStatus 타입과 필드를 맞춘 응답 DTO */
public record RegularStatusDto(
        StoreDto store,
        @Schema(example = "27") int visits,
        @Schema(example = "2021-03-15T00:00:00Z") Instant firstVisit,
        @Schema(example = "2025-11-20T00:00:00Z") Instant lastVisit,
        @Schema(example = "229500") long totalAmount,
        @Schema(example = "10") int threshold,
        @Schema(example = "true") boolean isRegular,
        @Schema(example = "4.7") double yearsSpan,
        int[] monthlyVisits,
        PaySource source) {
}

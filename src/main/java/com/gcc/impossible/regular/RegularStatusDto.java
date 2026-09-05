package com.gcc.impossible.regular;

import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.store.StoreDto;
import java.time.Instant;

/** FE src/types/index.ts 의 RegularStatus 타입과 필드를 맞춘 응답 DTO */
public record RegularStatusDto(
        StoreDto store,
        int visits,
        Instant firstVisit,
        Instant lastVisit,
        long totalAmount,
        int threshold,
        boolean isRegular,
        double yearsSpan,
        int[] monthlyVisits,
        PaySource source) {
}

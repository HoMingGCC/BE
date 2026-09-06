package com.gcc.impossible.substitute;

import com.gcc.impossible.store.StoreDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * @param visits    이 사용자의 해당 가게 결제 횟수 (콜드스타트로 채워진 후보는 0)
 * @param lastVisit 이 사용자의 마지막 결제일 (콜드스타트 후보는 결제 이력이 없어 null)
 */
public record SubstituteDto(
        StoreDto store,
        @Schema(example = "3") long visits,
        @Schema(example = "2026-05-22T18:30:00Z") Instant lastVisit) {
}

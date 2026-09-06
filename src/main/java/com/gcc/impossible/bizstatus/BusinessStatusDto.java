package com.gcc.impossible.bizstatus;

import com.gcc.impossible.store.StoreStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * @param source     "nts"(실시간 국세청 조회 성공) 또는 "seed-fallback"(등록되지 않은 사업자번호라 시드값 사용)
 * @param message    화면에 보여줄 근거 문구
 */
public record BusinessStatusDto(
        @Schema(example = "111-11-11111") String regno,
        StoreStatus status,
        @Schema(example = "nts") String source,
        @Schema(example = "국세청 사업자등록상태 조회 결과 영업중입니다.") String message,
        @Schema(example = "2026-01-15T02:00:00Z") Instant checkedAt) {
}

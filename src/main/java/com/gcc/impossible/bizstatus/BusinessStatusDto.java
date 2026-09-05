package com.gcc.impossible.bizstatus;

import com.gcc.impossible.store.StoreStatus;
import java.time.Instant;

/**
 * @param source     "nts"(실시간 국세청 조회 성공) 또는 "seed-fallback"(등록되지 않은 사업자번호라 시드값 사용)
 * @param message    화면에 보여줄 근거 문구
 */
public record BusinessStatusDto(String regno, StoreStatus status, String source, String message, Instant checkedAt) {
}

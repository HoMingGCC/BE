package com.gcc.impossible.migration;

import com.gcc.impossible.store.Region;
import com.gcc.impossible.store.StoreDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * @param detected            대구 결제 공백 + 정착업종 결제가 모두 있어야 true (둘 중 하나만으론 오탐 방지를 위해 false)
 * @param newRegion           정착 결제가 발생한 지역 (감지 안 됐으면 null)
 * @param gapDays             마지막 대구 결제 이후 경과 일수
 * @param lastRegionPaymentAt 마지막 대구 결제 일시
 * @param evidenceStores      판정 근거가 된 정착업종 결제 가게 목록
 */
public record MigrationStatusDto(
        @Schema(example = "true") boolean detected,
        Region newRegion,
        @Schema(example = "51") int gapDays,
        @Schema(example = "2026-07-17T19:31:00Z") Instant lastRegionPaymentAt,
        List<StoreDto> evidenceStores) {

    public static MigrationStatusDto notDetected() {
        return new MigrationStatusDto(false, null, 0, null, List.of());
    }
}

package com.gcc.impossible.ranking;

import com.gcc.impossible.store.StoreDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record DistrictRankingItemDto(
        StoreDto store,
        @Schema(example = "84") int regularCount,
        @Schema(example = "4.1") double avgYears,
        @Schema(example = "62") int recent6mVisitors,
        @Schema(example = "2") int walkMinutes,
        @Schema(example = "true") boolean isOpenNow) {
}

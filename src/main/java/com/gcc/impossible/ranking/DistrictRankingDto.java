package com.gcc.impossible.ranking;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DistrictRankingDto(
        @Schema(example = "dongseongro") String districtCode,
        @Schema(example = "중구") String districtName,
        List<DistrictRankingItemDto> items) {
}

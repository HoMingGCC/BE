package com.gcc.impossible.ranking;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DistrictRankingDto(
        @Schema(example = "seomun") String districtCode,
        @Schema(example = "서문시장") String districtName,
        List<DistrictRankingItemDto> items) {
}

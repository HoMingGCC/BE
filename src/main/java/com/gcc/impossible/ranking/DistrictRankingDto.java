package com.gcc.impossible.ranking;

import java.util.List;

public record DistrictRankingDto(String districtCode, String districtName, List<DistrictRankingItemDto> items) {
}

package com.gcc.impossible.ranking;

import com.gcc.impossible.store.StoreDto;

public record DistrictRankingItemDto(
        StoreDto store, int regularCount, double avgYears, int recent6mVisitors, int walkMinutes, boolean isOpenNow) {
}

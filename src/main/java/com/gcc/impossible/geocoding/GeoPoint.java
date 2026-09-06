package com.gcc.impossible.geocoding;

import io.swagger.v3.oas.annotations.media.Schema;

public record GeoPoint(
        @Schema(example = "35.866038") double lat,
        @Schema(example = "128.593745") double lng,
        @Schema(example = "대구 중구 달구벌대로 2109-32 1층") String matchedName) {
}

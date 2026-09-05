package com.gcc.impossible.geocoding;

import io.swagger.v3.oas.annotations.media.Schema;

public record GeoPoint(
        @Schema(example = "35.8421") double lat,
        @Schema(example = "128.5936") double lng,
        @Schema(example = "대구 남구 봉덕로 21") String matchedName) {
}

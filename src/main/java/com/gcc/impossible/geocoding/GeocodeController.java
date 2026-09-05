package com.gcc.impossible.geocoding;

import com.gcc.impossible.common.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지오코딩", description = "카카오 API로 주소를 좌표로 변환하는 API")
@RestController
@RequestMapping("/api/geocode")
public class GeocodeController {

    private final GeocodingClient geocodingClient;

    public GeocodeController(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    @Operation(summary = "주소 → 좌표 변환")
    @GetMapping
    public GeoPoint geocode(
            @Parameter(example = "대구 남구 봉덕로 21") @RequestParam String query) {
        return geocodingClient
                .geocode(query)
                .orElseThrow(() -> ApiException.notFound("좌표를 찾을 수 없습니다 (카카오 REST 키 미설정 또는 검색결과 없음): " + query));
    }
}

package com.gcc.impossible.geocoding;

import com.gcc.impossible.common.ApiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocode")
public class GeocodeController {

    private final GeocodingClient geocodingClient;

    public GeocodeController(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    @GetMapping
    public GeoPoint geocode(@RequestParam String query) {
        return geocodingClient
                .geocode(query)
                .orElseThrow(() -> ApiException.notFound("좌표를 찾을 수 없습니다 (카카오 REST 키 미설정 또는 검색결과 없음): " + query));
    }
}

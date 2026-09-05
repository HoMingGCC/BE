package com.gcc.impossible.geocoding;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 카카오맵 좌표 매칭 — 마스터에 없는 가게를 상호명으로 검색해 좌표를 보완한다.
 * REST 키가 아직 없어서(카카오 디벨로퍼스에서 발급 필요) 지금은 구조만 잡고,
 * 키가 비어있으면 기동도 정상적으로 되고 호출 시 빈 결과만 돌려준다.
 */
@Slf4j
@Component
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoLocalGeocodingClient implements GeocodingClient {

    private final RestClient restClient;
    private final KakaoProperties properties;

    public KakaoLocalGeocodingClient(RestClient.Builder restClientBuilder, KakaoProperties properties) {
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.properties = properties;
    }

    @Override
    public Optional<GeoPoint> geocode(String query) {
        if (!properties.isConfigured() || query == null || query.isBlank()) {
            return Optional.empty();
        }

        try {
            KakaoKeywordResponse response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.queryParam("query", query).build())
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + properties.getRestKey())
                    .retrieve()
                    .body(KakaoKeywordResponse.class);

            if (response == null || response.documentsOrEmpty().isEmpty()) {
                return Optional.empty();
            }

            KakaoKeywordResponse.Document top = response.documentsOrEmpty().get(0);
            return Optional.of(new GeoPoint(Double.parseDouble(top.y()), Double.parseDouble(top.x()), top.place_name()));
        } catch (RestClientException e) {
            log.warn("카카오 좌표 검색 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }
}

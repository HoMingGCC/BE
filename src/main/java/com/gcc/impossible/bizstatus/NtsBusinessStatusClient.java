package com.gcc.impossible.bizstatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 국세청_사업자등록정보 진위확인 및 상태조회 서비스 (공공데이터포털) 클라이언트.
 * 1회 호출 최대 100건 — 시드 12개 가게 정도는 한 번에 배치 조회한다.
 */
@Slf4j
@Component
@EnableConfigurationProperties(NtsProperties.class)
public class NtsBusinessStatusClient {

    private final RestClient restClient;
    private final NtsProperties properties;

    public NtsBusinessStatusClient(RestClient.Builder restClientBuilder, NtsProperties properties) {
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.properties = properties;
    }

    /**
     * @param regnos 사업자등록번호 목록('-' 포함 가능, 내부에서 제거)
     * @return b_no(숫자 10자리) → 조회 결과. 서비스키 미설정이거나 호출 실패 시 빈 맵.
     */
    public Map<String, NtsStatusItem> lookupStatuses(List<String> regnos) {
        if (!properties.isConfigured() || regnos.isEmpty()) {
            return Map.of();
        }

        List<String> bnoList = regnos.stream().map(r -> r.replace("-", "")).toList();

        try {
            NtsStatusResponse response = restClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/status")
                            .queryParam("serviceKey", properties.getServiceKey())
                            .queryParam("returnType", "JSON")
                            .build())
                    .body(new NtsStatusRequest(bnoList))
                    .retrieve()
                    .body(NtsStatusResponse.class);

            Map<String, NtsStatusItem> result = new HashMap<>();
            if (response != null) {
                for (NtsStatusItem item : response.items()) {
                    result.put(item.b_no(), item);
                }
            }
            return result;
        } catch (RestClientException e) {
            log.warn("국세청 사업자상태 조회 실패, seed 값으로 폴백합니다: {}", e.getMessage());
            return Map.of();
        }
    }
}

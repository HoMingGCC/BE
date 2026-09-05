package com.gcc.impossible.geocoding;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 카카오 로컬 API(키워드 검색) 연동 설정. REST 키가 없으면 GeocodingClient는 항상 빈 결과를 돌려준다. */
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private String restKey = "";

    private String baseUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";

    public String getRestKey() {
        return restKey;
    }

    public void setRestKey(String restKey) {
        this.restKey = restKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isConfigured() {
        return restKey != null && !restKey.isBlank();
    }
}

package com.gcc.impossible.bizstatus;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 국세청_사업자등록정보 진위확인 및 상태조회 서비스 (공공데이터포털) 연동 설정.
 * 서비스키는 로컬 application.yaml 에만 두고 커밋하지 않는다.
 */
@ConfigurationProperties(prefix = "nts")
public class NtsProperties {

    private String serviceKey = "";

    private String baseUrl = "https://api.odcloud.kr/api/nts-businessman/v1";

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isConfigured() {
        return serviceKey != null && !serviceKey.isBlank();
    }
}

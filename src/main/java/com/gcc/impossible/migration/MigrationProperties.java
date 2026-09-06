package com.gcc.impossible.migration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 이주 감지 파라미터를 코드가 아니라 데이터(migration.yml)로 정의한다 — 온톨로지와 같은 설계 원칙.
 * 기준이 바뀌어도(공백 일수, 정착업종 목록) 로직을 안 건드리기 위함.
 */
@ConfigurationProperties(prefix = "migration")
public class MigrationProperties {

    /** 최근 대구 결제가 이만큼(일) 없어야 "공백"으로 본다 */
    private int gapDays = 30;

    /** 공백 기간 중 이 업종 결제가 있어야 "정착"으로 본다 (예: 부동산, 가구) */
    private List<String> settlementIndustries = List.of();

    public int getGapDays() {
        return gapDays;
    }

    public void setGapDays(int gapDays) {
        this.gapDays = gapDays;
    }

    public List<String> getSettlementIndustries() {
        return settlementIndustries;
    }

    public void setSettlementIndustries(List<String> settlementIndustries) {
        this.settlementIndustries = settlementIndustries;
    }
}

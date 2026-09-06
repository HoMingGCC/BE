package com.gcc.impossible.seed.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 데이터팀(주희) stores.json 원본 스키마 그대로 매핑.
 *
 * 원본은 가게 그룹별로 가맹 정보 필드명이 다르다 — 태산만두~사과문구(분식/카페/미용실/문구)는
 * easyPayAffiliated(간편결제 가맹 여부)+easyPayNote를, 나머지(부동산/가구/랭킹용)는
 * localCurrencyAffiliated(지역화폐 가맹 여부)만 쓴다. 둘 다 선택 필드로 두고 실제 값 해석은
 * DemoDataSeeder에서 한다 — 두 개념이 같은 게 맞는지는 데이터팀 확인 대기 중.
 *
 * region/district는 원본에 없는 필드라 여기 넣지 않는다 — DemoDataSeeder가 address에서 유도한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StoreJson(
        String bizNo,
        String name,
        String address,
        double lat,
        double lng,
        String category,
        String status,
        Boolean easyPayAffiliated,
        String easyPayNote,
        Boolean localCurrencyAffiliated) {
}

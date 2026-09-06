package com.gcc.impossible.seed.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 데이터팀(주희) payments_*.json 원본 스키마(카드-008 규격) 그대로 매핑 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentJson(
        String paymentId,
        String userId,
        String bizNo,
        String merchantName,
        String approvedAt,
        long amount,
        String status,
        String cardType) {
}

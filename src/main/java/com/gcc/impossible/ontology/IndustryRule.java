package com.gcc.impossible.ontology;

/**
 * 업종별 단골 기준 한 행 — 데이터팀(주희) 노션 "온톨로지 기준(판정 엔진용) JSON"과 필드명 그대로.
 *
 * @param type         판정 방식. 지금은 "count"(횟수형)만 있음
 * @param minVisits    단골로 인정하는 최소 방문 횟수
 * @param periodMonths 위 방문 횟수를 세는 관찰 기간(개월) — 이 기간을 벗어난 결제는 판정에서 제외
 */
public record IndustryRule(String industry, String type, int minVisits, int periodMonths) {
}

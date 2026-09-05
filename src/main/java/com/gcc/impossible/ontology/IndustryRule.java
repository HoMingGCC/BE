package com.gcc.impossible.ontology;

/**
 * 업종별 단골 기준 한 행.
 *
 * @param threshold      이 방문 횟수 이상이면 단골
 * @param substitutable  대체 가게 추천 제공 여부 (병원·약국·편의점처럼 대체가 자명하지 않으면 false)
 */
public record IndustryRule(String industry, Category category, int threshold, boolean substitutable) {
}

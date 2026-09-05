package com.gcc.impossible.ranking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 방문객 단골 보증 랭킹 한 행 (합성). 검색·리뷰가 아니라 "재방문 단골 수" 기준 정렬.
 * 다중 고객 데이터가 없어 FE mocks/visitor.ts 값을 그대로 시드한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictRankingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String districtCode;

    private String districtName;

    private String storeRegno;

    private int regularCount;

    private double avgYears;

    private int recent6mVisitors;

    private int walkMinutes;
}

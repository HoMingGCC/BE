package com.gcc.impossible.ranking;

import com.gcc.impossible.ontology.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상권 랭킹", description = "상권(지역)별 단골 랭킹 조회 API")
@RestController
@RequestMapping("/api/districts")
public class RankingController {

    private final DistrictRankingService districtRankingService;

    public RankingController(DistrictRankingService districtRankingService) {
        this.districtRankingService = districtRankingService;
    }

    @Operation(summary = "상권별 단골 랭킹 조회", description = "카테고리로 필터링할 수 있으며, 생략 시 전체 업종을 반환한다.")
    @GetMapping("/{code}/ranking")
    public DistrictRankingDto getRanking(
            @Parameter(example = "dongseongro") @PathVariable String code,
            @Parameter(example = "meal") @RequestParam(required = false) Category category) {
        return districtRankingService.getRanking(code, category);
    }
}

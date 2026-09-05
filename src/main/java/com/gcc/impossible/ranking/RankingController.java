package com.gcc.impossible.ranking;

import com.gcc.impossible.ontology.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/districts")
public class RankingController {

    private final DistrictRankingService districtRankingService;

    public RankingController(DistrictRankingService districtRankingService) {
        this.districtRankingService = districtRankingService;
    }

    @GetMapping("/{code}/ranking")
    public DistrictRankingDto getRanking(
            @PathVariable String code, @RequestParam(required = false) Category category) {
        return districtRankingService.getRanking(code, category);
    }
}

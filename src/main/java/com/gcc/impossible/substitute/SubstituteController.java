package com.gcc.impossible.substitute;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대체 가게", description = "폐업 가게 방문객에게 보여줄 대체 후보 조회 API")
@RestController
@RequestMapping("/api/stores")
public class SubstituteController {

    private final SubstituteCandidateService substituteCandidateService;

    public SubstituteController(SubstituteCandidateService substituteCandidateService) {
        this.substituteCandidateService = substituteCandidateService;
    }

    @Operation(summary = "대체 가게 후보 조회", description = "폐업한 가게(regno)와 같은 업종·인근 지역의 대체 후보를 방문수 순으로 반환한다.")
    @GetMapping("/{regno}/substitutes")
    public List<SubstituteDto> findCandidates(
            @Parameter(example = "514-81-10003") @PathVariable String regno) {
        return substituteCandidateService.findCandidates(regno);
    }
}

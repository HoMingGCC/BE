package com.gcc.impossible.substitute;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대체 가게", description = "폐업 가게 방문객에게 보여줄 대체 후보 조회 API")
@RestController
@RequestMapping("/api/stores")
public class SubstituteController {

    /** 데모는 로그인이 없어 개인화 대상이 항상 이 사용자다 (RegularController와 동일한 데모 주인공) */
    private static final String DEFAULT_USER_ID = "U001";

    private final SubstituteCandidateService substituteCandidateService;

    public SubstituteController(SubstituteCandidateService substituteCandidateService) {
        this.substituteCandidateService = substituteCandidateService;
    }

    @Operation(
            summary = "대체 가게 후보 조회",
            description = "폐업한 가게(regno)와 같은 업종의 대체 후보를 이 사용자의 최근 방문일 우선으로 반환하고, "
                    + "3곳이 안 되면 결제 이력 없는 가게로 채운다(콜드스타트). userId 생략 시 데모 주인공(U001) 기준.")
    @GetMapping("/{regno}/substitutes")
    public List<SubstituteDto> findCandidates(
            @Parameter(example = "999-99-99999") @PathVariable String regno,
            @Parameter(example = "U001") @RequestParam(name = "userId", required = false) String userId) {
        return substituteCandidateService.findCandidates(regno, userId == null ? DEFAULT_USER_ID : userId);
    }
}

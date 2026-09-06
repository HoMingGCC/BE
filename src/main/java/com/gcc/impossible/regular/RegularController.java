package com.gcc.impossible.regular;

import com.gcc.impossible.payment.PaySource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "단골", description = "결제 원장 기반 단골 판정 API")
@RestController
@RequestMapping("/api/regulars")
public class RegularController {

    /** 지역화폐는 자사 원장이라 동의 없이 이미 보인다 — 기본값(v1) */
    private static final List<PaySource> DEFAULT_SOURCES = List.of(PaySource.LOCALPAY);

    /** 데모는 로그인이 없어 "나의 단골 지도"가 항상 이 사용자 기준이다 (FE의 옛 MAIN_USER='u-juyeon'과 같은 역할) */
    private static final String DEFAULT_USER_ID = "U001";

    private final RegularService regularService;

    public RegularController(RegularService regularService) {
        this.regularService = regularService;
    }

    @Operation(
            summary = "단골 판정 전체 목록 조회",
            description = "sources 생략 시 지역화폐(localpay) 원장만으로 판정한다(v1 기본값). "
                    + "userId 생략 시 데모 주인공(U001) 기준으로 판정한다 — 로그인이 없어 세션 사용자 개념이 없기 때문.")
    @GetMapping
    public List<RegularStatusDto> findAll(
            @Parameter(example = "localpay") @RequestParam(name = "sources", required = false) List<PaySource> sources,
            @Parameter(example = "U001") @RequestParam(name = "userId", required = false) String userId) {
        return regularService.findAll(userId == null ? DEFAULT_USER_ID : userId, sources == null ? DEFAULT_SOURCES : sources);
    }

    @Operation(summary = "가게별 단골 판정 단건 조회")
    @GetMapping("/{regno}")
    public RegularStatusDto findOne(
            @Parameter(example = "111-11-11111") @PathVariable String regno,
            @Parameter(example = "localpay") @RequestParam(name = "sources", required = false) List<PaySource> sources,
            @Parameter(example = "U001") @RequestParam(name = "userId", required = false) String userId) {
        return regularService.findOne(
                regno, userId == null ? DEFAULT_USER_ID : userId, sources == null ? DEFAULT_SOURCES : sources);
    }
}

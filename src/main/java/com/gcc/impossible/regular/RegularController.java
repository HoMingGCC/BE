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

    private final RegularService regularService;

    public RegularController(RegularService regularService) {
        this.regularService = regularService;
    }

    @Operation(summary = "단골 판정 전체 목록 조회", description = "sources 생략 시 지역화폐(localpay) 원장만으로 판정한다(v1 기본값).")
    @GetMapping
    public List<RegularStatusDto> findAll(
            @Parameter(example = "localpay") @RequestParam(name = "sources", required = false) List<PaySource> sources) {
        return regularService.findAll(sources == null ? DEFAULT_SOURCES : sources);
    }

    @Operation(summary = "가게별 단골 판정 단건 조회")
    @GetMapping("/{regno}")
    public RegularStatusDto findOne(
            @Parameter(example = "514-81-10001") @PathVariable String regno,
            @Parameter(example = "localpay") @RequestParam(name = "sources", required = false) List<PaySource> sources) {
        return regularService.findOne(regno, sources == null ? DEFAULT_SOURCES : sources);
    }
}

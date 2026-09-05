package com.gcc.impossible.regular;

import com.gcc.impossible.payment.PaySource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regulars")
public class RegularController {

    /** 지역화폐는 자사 원장이라 동의 없이 이미 보인다 — 기본값(v1) */
    private static final List<PaySource> DEFAULT_SOURCES = List.of(PaySource.LOCALPAY);

    private final RegularService regularService;

    public RegularController(RegularService regularService) {
        this.regularService = regularService;
    }

    @GetMapping
    public List<RegularStatusDto> findAll(
            @RequestParam(name = "sources", required = false) List<PaySource> sources) {
        return regularService.findAll(sources == null ? DEFAULT_SOURCES : sources);
    }

    @GetMapping("/{regno}")
    public RegularStatusDto findOne(
            @PathVariable String regno,
            @RequestParam(name = "sources", required = false) List<PaySource> sources) {
        return regularService.findOne(regno, sources == null ? DEFAULT_SOURCES : sources);
    }
}

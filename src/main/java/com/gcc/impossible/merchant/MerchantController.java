package com.gcc.impossible.merchant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사장님", description = "사장님 대시보드 조회 · 단골 소식 발송 API")
@RestController
@RequestMapping("/api/merchant/{regno}")
public class MerchantController {

    private final MerchantDashboardService merchantDashboardService;
    private final NewsSendService newsSendService;

    public MerchantController(MerchantDashboardService merchantDashboardService, NewsSendService newsSendService) {
        this.merchantDashboardService = merchantDashboardService;
        this.newsSendService = newsSendService;
    }

    @Operation(summary = "사장님 대시보드 조회", description = "단골/휴면/신규 고객 수와 이번 주 소식 발송 슬롯 현황을 반환한다.")
    @GetMapping("/dashboard")
    public MerchantDashboardDto getDashboard(
            @Parameter(example = "514-81-10001") @PathVariable String regno) {
        return merchantDashboardService.getDashboard(regno);
    }

    @Operation(summary = "단골 소식 발송", description = "주간 발송 슬롯 한도 내에서 단골 손님에게 소식을 발송한다.")
    @PostMapping("/news")
    public NewsSendResultDto sendNews(
            @Parameter(example = "514-81-10001") @PathVariable String regno,
            @Valid @RequestBody NewsSendRequest request) {
        return newsSendService.send(regno, request.message());
    }
}

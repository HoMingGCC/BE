package com.gcc.impossible.merchant;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchant/{regno}")
public class MerchantController {

    private final MerchantDashboardService merchantDashboardService;
    private final NewsSendService newsSendService;

    public MerchantController(MerchantDashboardService merchantDashboardService, NewsSendService newsSendService) {
        this.merchantDashboardService = merchantDashboardService;
        this.newsSendService = newsSendService;
    }

    @GetMapping("/dashboard")
    public MerchantDashboardDto getDashboard(@PathVariable String regno) {
        return merchantDashboardService.getDashboard(regno);
    }

    @PostMapping("/news")
    public NewsSendResultDto sendNews(@PathVariable String regno, @Valid @RequestBody NewsSendRequest request) {
        return newsSendService.send(regno, request.message());
    }
}

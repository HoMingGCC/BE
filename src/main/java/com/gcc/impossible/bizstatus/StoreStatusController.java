package com.gcc.impossible.bizstatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사업자 상태", description = "국세청 사업자등록상태 실시간 조회 API")
@RestController
@RequestMapping("/api/stores")
public class StoreStatusController {

    private final StoreStatusService storeStatusService;

    public StoreStatusController(StoreStatusService storeStatusService) {
        this.storeStatusService = storeStatusService;
    }

    @Operation(summary = "사업자등록상태 조회", description = "국세청 진위확인 API로 실시간 조회하고, 실패 시 시드값으로 폴백한다.")
    @GetMapping("/{regno}/status")
    public BusinessStatusDto getStatus(
            @Parameter(example = "514-81-10001") @PathVariable String regno) {
        return storeStatusService.resolveStatus(regno);
    }
}

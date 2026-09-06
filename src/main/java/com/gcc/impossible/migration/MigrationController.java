package com.gcc.impossible.migration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이주 감지", description = "결제 지역 변화 기반 이주(자취 시작) 이벤트 감지 API — GPS 아님, 결제 데이터 기반")
@RestController
@RequestMapping("/api/users")
public class MigrationController {

    private final MigrationDetectionService migrationDetectionService;

    public MigrationController(MigrationDetectionService migrationDetectionService) {
        this.migrationDetectionService = migrationDetectionService;
    }

    @Operation(
            summary = "이주 이벤트 감지",
            description = "대구 결제 공백(기본 30일 이상) + 그 공백 기간 내 정착업종(부동산·가구) 결제, "
                    + "두 조건이 모두 있어야 이주로 판정한다. 여행 등 단발성 이동으로 오탐되는 걸 막기 위함.")
    @GetMapping("/{userId}/migration")
    public MigrationStatusDto detect(@Parameter(example = "U001") @PathVariable String userId) {
        return migrationDetectionService.detect(userId);
    }
}

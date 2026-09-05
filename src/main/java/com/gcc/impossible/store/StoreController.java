package com.gcc.impossible.store;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "가게", description = "가게 마스터 조회 API")
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @Operation(summary = "가게 전체 목록 조회")
    @GetMapping
    public List<StoreDto> findAll() {
        return storeService.findAll().stream().map(StoreDto::from).toList();
    }

    @Operation(summary = "사업자등록번호로 가게 단건 조회")
    @GetMapping("/{regno}")
    public StoreDto getByRegno(
            @Parameter(example = "514-81-10001") @PathVariable String regno) {
        return StoreDto.from(storeService.getByRegno(regno));
    }
}

package com.gcc.impossible.bizstatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
public class StoreStatusController {

    private final StoreStatusService storeStatusService;

    public StoreStatusController(StoreStatusService storeStatusService) {
        this.storeStatusService = storeStatusService;
    }

    @GetMapping("/{regno}/status")
    public BusinessStatusDto getStatus(@PathVariable String regno) {
        return storeStatusService.resolveStatus(regno);
    }
}

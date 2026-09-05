package com.gcc.impossible.store;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<StoreDto> findAll() {
        return storeService.findAll().stream().map(StoreDto::from).toList();
    }

    @GetMapping("/{regno}")
    public StoreDto getByRegno(@PathVariable String regno) {
        return StoreDto.from(storeService.getByRegno(regno));
    }
}

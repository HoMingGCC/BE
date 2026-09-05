package com.gcc.impossible.store;

import com.gcc.impossible.common.ApiException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store getByRegno(String regno) {
        return storeRepository
                .findById(regno)
                .orElseThrow(() -> ApiException.notFound("가게를 찾을 수 없습니다: " + regno));
    }
}

package com.gcc.impossible.store;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, String> {

    List<Store> findByCategoryAndStatusAndRegnoNot(String category, StoreStatus status, String regno);

    List<Store> findByDistrict(String district);
}

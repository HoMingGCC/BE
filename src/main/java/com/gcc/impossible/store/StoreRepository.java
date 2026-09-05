package com.gcc.impossible.store;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, String> {

    List<Store> findByIndustryAndStatusAndRegnoNot(String industry, StoreStatus status, String regno);
}

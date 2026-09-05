package com.gcc.impossible.payment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySourceIn(List<PaySource> sources);

    List<Payment> findByMerchantRegnoAndStatus(String merchantRegno, PaymentStatus status);

    long countByMerchantRegnoAndStatus(String merchantRegno, PaymentStatus status);
}

package com.gcc.impossible.regular;

import com.gcc.impossible.common.ApiException;
import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * FE store/useAppStore.ts 의 regularsFor(sources) 포팅.
 * localpay만 넘기면 v1(동의 불필요) 상태, 전체 소스를 넘기면 마이데이터 동의 이후 상태를 재현한다.
 */
@Service
public class RegularService {

    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;
    private final RegularJudgeService regularJudgeService;

    public RegularService(
            StoreRepository storeRepository,
            PaymentRepository paymentRepository,
            RegularJudgeService regularJudgeService) {
        this.storeRepository = storeRepository;
        this.paymentRepository = paymentRepository;
        this.regularJudgeService = regularJudgeService;
    }

    public List<RegularStatusDto> findAll(List<PaySource> sources) {
        List<Payment> payments = paymentRepository.findBySourceIn(sources);
        List<Store> stores = storeRepository.findAll();
        return regularJudgeService.judgeAll(stores, payments);
    }

    public RegularStatusDto findOne(String regno, List<PaySource> sources) {
        Store store = storeRepository
                .findById(regno)
                .orElseThrow(() -> ApiException.notFound("가게를 찾을 수 없습니다: " + regno));
        List<Payment> payments = paymentRepository.findBySourceIn(sources);
        return regularJudgeService
                .judge(store, payments)
                .orElseThrow(() -> ApiException.notFound("이 가게에 대한 결제 이력이 없습니다: " + regno));
    }
}

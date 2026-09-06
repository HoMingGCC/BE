package com.gcc.impossible.regular;

import com.gcc.impossible.common.ApiException;
import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.store.Region;
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

    public List<RegularStatusDto> findAll(String userId, List<PaySource> sources) {
        List<Payment> payments = paymentRepository.findByUserIdAndSourceIn(userId, sources);
        // 서울 이주감지용 가상 가게(부동산·가구)는 "나의 단골 지도"엔 안 나온다 — 대구·경북 결제만 보여주는 화면이라서
        List<Store> stores =
                storeRepository.findAll().stream().filter(s -> s.getRegion() != Region.SEOUL).toList();
        return regularJudgeService.judgeAll(stores, payments);
    }

    public RegularStatusDto findOne(String regno, String userId, List<PaySource> sources) {
        Store store = storeRepository
                .findById(regno)
                .orElseThrow(() -> ApiException.notFound("가게를 찾을 수 없습니다: " + regno));
        List<Payment> payments = paymentRepository.findByUserIdAndSourceIn(userId, sources);
        return regularJudgeService
                .judge(store, payments)
                .orElseThrow(() -> ApiException.notFound("이 가게에 대한 결제 이력이 없습니다: " + regno));
    }
}

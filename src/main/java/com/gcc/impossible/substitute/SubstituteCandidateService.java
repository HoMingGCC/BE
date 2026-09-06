package com.gcc.impossible.substitute;

import com.gcc.impossible.common.ApiException;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 대체 가게 후보 — "지식그래프" 대신 관계형 쿼리로 구현 (데이터팀 노션 "대체 가게 후보 로직" 의사코드 그대로).
 * (Neo4j 등 그래프DB는 이 데이터 규모/일정에는 과함 — 결과는 동일한 "관계 탐색")
 *
 * 1) 이 사용자가 결제한 이력이 있는 같은 업종 가게를 최근 방문일 우선으로 채우고,
 * 2) 3곳이 안 되면(콜드스타트) 결제 이력 없는 같은 업종 가게로 나머지를 채운다.
 */
@Service
public class SubstituteCandidateService {

    private static final int TOP_N = 3;

    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;

    public SubstituteCandidateService(StoreRepository storeRepository, PaymentRepository paymentRepository) {
        this.storeRepository = storeRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<SubstituteDto> findCandidates(String regno, String userId) {
        Store target = storeRepository
                .findById(regno)
                .orElseThrow(() -> ApiException.notFound("가게를 찾을 수 없습니다: " + regno));

        List<Store> candidates =
                storeRepository.findByCategoryAndStatusAndRegnoNot(target.getCategory(), StoreStatus.OPEN, regno);

        List<SubstituteDto> withHistory = new ArrayList<>();
        List<Store> withoutHistory = new ArrayList<>();
        for (Store store : candidates) {
            List<Payment> userPayments = paymentRepository.findByUserIdAndMerchantRegnoAndStatus(
                    userId, store.getRegno(), PaymentStatus.APPROVED);
            if (userPayments.isEmpty()) {
                withoutHistory.add(store);
                continue;
            }
            Instant lastVisit = userPayments.stream().map(Payment::getApprovedAt).max(Instant::compareTo).orElseThrow();
            withHistory.add(new SubstituteDto(StoreDto.from(store), userPayments.size(), lastVisit));
        }

        withHistory.sort(Comparator.comparing(SubstituteDto::lastVisit, Comparator.reverseOrder())
                .thenComparing(Comparator.comparingLong(SubstituteDto::visits).reversed()));

        List<SubstituteDto> result = new ArrayList<>(withHistory);
        for (Store store : withoutHistory) {
            if (result.size() >= TOP_N) {
                break;
            }
            result.add(new SubstituteDto(StoreDto.from(store), 0, null));
        }

        return result.stream().limit(TOP_N).toList();
    }
}

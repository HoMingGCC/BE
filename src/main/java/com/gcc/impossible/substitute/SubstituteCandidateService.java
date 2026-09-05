package com.gcc.impossible.substitute;

import com.gcc.impossible.common.ApiException;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 대체 가게 후보 — "지식그래프" 대신 관계형 쿼리로 구현.
 * 같은 업종 + 영업 중 가게를 뽑아 결제 이력(방문횟수) 기준으로 TOP3 정렬한다.
 * (Neo4j 등 그래프DB는 이 데이터 규모/일정에는 과함 — 결과는 동일한 "관계 탐색")
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

    public List<SubstituteDto> findCandidates(String regno) {
        Store target = storeRepository
                .findById(regno)
                .orElseThrow(() -> ApiException.notFound("가게를 찾을 수 없습니다: " + regno));

        List<Store> candidates =
                storeRepository.findByIndustryAndStatusAndRegnoNot(target.getIndustry(), StoreStatus.OPEN, regno);

        return candidates.stream()
                .map(store -> new SubstituteDto(
                        StoreDto.from(store),
                        paymentRepository.countByMerchantRegnoAndStatus(store.getRegno(), PaymentStatus.APPROVED)))
                .sorted(Comparator.comparingLong(SubstituteDto::visits).reversed())
                .limit(TOP_N)
                .toList();
    }
}

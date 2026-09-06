package com.gcc.impossible.ranking;

import com.gcc.impossible.ontology.Category;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.regular.RegularJudgeService;
import com.gcc.impossible.regular.RegularStatusDto;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 방문객 QR 랭킹 — 정적 시드 테이블이 아니라 결제 원장을 온톨로지 기준으로 실시간 판정해서 계산한다.
 * "재방문 단골 수" 자체가 지표라 검색·리뷰로는 조작할 수 없다는 설계 원칙을 실제로 지키려면
 * 미리 박아둔 숫자가 아니라 매 요청마다 판정을 다시 돌려야 한다.
 */
@Service
public class DistrictRankingService {

    /** 상권 코드 → 실제 district 값. 데모는 동성로 상권 하나뿐 (서문시장보다 업종이 다양해서 변경됨) */
    private static final Map<String, String> DISTRICT_NAMES = Map.of("dongseongro", "중구");
    private static final String DEFAULT_DISTRICT_CODE = "dongseongro";

    /** 도보 환산 — 평균 도보 속도 약 4km/h(분당 67m) 기준 */
    private static final double WALK_METERS_PER_MINUTE = 67.0;
    private static final long SIX_MONTHS_SECONDS = 183L * 86_400;

    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;
    private final RegularJudgeService regularJudgeService;

    public DistrictRankingService(
            StoreRepository storeRepository, PaymentRepository paymentRepository, RegularJudgeService regularJudgeService) {
        this.storeRepository = storeRepository;
        this.paymentRepository = paymentRepository;
        this.regularJudgeService = regularJudgeService;
    }

    public DistrictRankingDto getRanking(String districtCode, Category categoryFilter) {
        String resolvedCode = DISTRICT_NAMES.containsKey(districtCode) ? districtCode : DEFAULT_DISTRICT_CODE;
        String districtName = DISTRICT_NAMES.get(resolvedCode);

        List<Store> districtStores = storeRepository.findByDistrict(districtName);
        if (districtStores.isEmpty()) {
            return new DistrictRankingDto(resolvedCode, districtName, List.of());
        }

        double[] anchor = centroidOf(districtStores);

        List<DistrictRankingItemDto> items = districtStores.stream()
                .filter(store -> store.getStatus() == StoreStatus.OPEN)
                .filter(store -> categoryFilter == null || Category.bucketOf(store.getCategory()) == categoryFilter)
                .map(store -> toItem(store, anchor))
                .sorted(Comparator.comparingInt(DistrictRankingItemDto::regularCount).reversed())
                .toList();

        return new DistrictRankingDto(resolvedCode, districtName, items);
    }

    private DistrictRankingItemDto toItem(Store store, double[] anchor) {
        List<Payment> payments = paymentRepository.findByMerchantRegnoAndStatus(store.getRegno(), PaymentStatus.APPROVED);
        Map<String, List<Payment>> byUser = payments.stream().collect(Collectors.groupingBy(Payment::getUserId));

        Instant sixMonthsAgo = Instant.now().minusSeconds(SIX_MONTHS_SECONDS);
        int regularCount = 0;
        int recent6mVisitors = 0;
        double yearsSum = 0;
        for (List<Payment> userPayments : byUser.values()) {
            RegularStatusDto judged = regularJudgeService.judge(store, userPayments).orElse(null);
            if (judged == null || !judged.isRegular()) {
                continue;
            }
            regularCount++;
            yearsSum += judged.yearsSpan();
            if (judged.lastVisit().isAfter(sixMonthsAgo)) {
                recent6mVisitors++;
            }
        }
        double avgYears = regularCount == 0 ? 0 : Math.round((yearsSum / regularCount) * 10) / 10.0;
        int walkMinutes = Math.max(1, (int) Math.round(haversineMeters(anchor, store) / WALK_METERS_PER_MINUTE));

        return new DistrictRankingItemDto(StoreDto.from(store), regularCount, avgYears, recent6mVisitors, walkMinutes, true);
    }

    private double[] centroidOf(List<Store> stores) {
        double lat = stores.stream().mapToDouble(Store::getLat).average().orElse(0);
        double lng = stores.stream().mapToDouble(Store::getLng).average().orElse(0);
        return new double[] {lat, lng};
    }

    /** 두 좌표 간 대략적인 거리(m) — 도보 환산용이라 정밀한 지구 곡률 보정까진 필요 없음 */
    private double haversineMeters(double[] anchor, Store store) {
        double earthRadiusM = 6_371_000;
        double lat1 = Math.toRadians(anchor[0]);
        double lat2 = Math.toRadians(store.getLat());
        double dLat = Math.toRadians(store.getLat() - anchor[0]);
        double dLng = Math.toRadians(store.getLng() - anchor[1]);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusM * c;
    }
}

package com.gcc.impossible.seed;

import com.gcc.impossible.merchant.MerchantSnapshot;
import com.gcc.impossible.merchant.MerchantSnapshotRepository;
import com.gcc.impossible.ontology.Category;
import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.payment.PayType;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.ranking.DistrictRankingEntry;
import com.gcc.impossible.ranking.DistrictRankingRepository;
import com.gcc.impossible.store.Region;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data 레포가 비어있는 동안 임시로 넣어두는 시드 데이터.
 * FE src/mocks/{stores,payments,merchant,visitor}.ts 의 값을 그대로 포팅한다.
 * Data 팀 결과물(공공데이터 가게 마스터 + 합성 결제 원장)이 나오면 이 클래스를 걷어내면 된다.
 */
@Slf4j
@Component
public class DemoDataSeeder implements ApplicationRunner {

    private static final String MAIN_USER = "u-juyeon";

    /** 방학·휴가철 가중치 — 1·2·7·8월은 방문이 줄어든다 (0-indexed: 1월=index0) */
    private static final double[] SEASON_WEIGHT = {
        0.4, 0.5, 1.2, 1.1, 1.0, 1.0, 0.5, 0.4, 1.2, 1.1, 1.0, 0.9
    };

    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;
    private final MerchantSnapshotRepository merchantSnapshotRepository;
    private final DistrictRankingRepository districtRankingRepository;

    public DemoDataSeeder(
            StoreRepository storeRepository,
            PaymentRepository paymentRepository,
            MerchantSnapshotRepository merchantSnapshotRepository,
            DistrictRankingRepository districtRankingRepository) {
        this.storeRepository = storeRepository;
        this.paymentRepository = paymentRepository;
        this.merchantSnapshotRepository = merchantSnapshotRepository;
        this.districtRankingRepository = districtRankingRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (storeRepository.count() > 0) {
            log.info("시드 데이터가 이미 존재합니다 — 건너뜁니다.");
            return;
        }

        List<Store> stores = seedStores();
        storeRepository.saveAll(stores);
        log.info("가게 마스터 {}곳 시드 완료", stores.size());

        List<Payment> payments = seedPayments();
        paymentRepository.saveAll(payments);
        log.info("결제 원장 {}건 시드 완료", payments.size());

        merchantSnapshotRepository.saveAll(seedMerchantSnapshots());
        districtRankingRepository.saveAll(seedDistrictRanking());
        log.info("사장님 대시보드 · 방문객 랭킹 시드 완료");
    }

    private List<Store> seedStores() {
        return List.of(
                store("514-81-10001", "봉덕 분식", "대구 남구 봉덕로 21", 35.8421, 128.5936,
                        "분식", Category.MEAL, StoreStatus.OPEN, null, true, "10:00–20:00", "일요일", "남구"),
                store("514-81-10002", "가나 미용실", "대구 북구 대학로 88", 35.8895, 128.6103,
                        "미용실", Category.LIFE, StoreStatus.OPEN, null, true, "09:00–19:00", "월요일", "북구"),
                store("514-81-10003", "산격문구", "대구 북구 대학로 12", 35.8912, 128.6088,
                        "문구", Category.LIFE, StoreStatus.CLOSED, LocalDate.of(2024, 12, 11), true, null, null, "북구"),
                store("514-81-10004", "오늘의커피", "대구 북구 산격동 118", 35.8878, 128.6121,
                        "카페", Category.CAFE, StoreStatus.OPEN, null, true, "08:00–22:00", null, "북구"),
                store("514-81-10005", "큐 안경점", "대구 북구 복현로 34", 35.8934, 128.6045,
                        "안경점", Category.LIFE, StoreStatus.OPEN, null, false, "10:00–20:00", null, "북구"),
                store("514-81-10006", "대현문구", "대구 북구 대현로 9", 35.8869, 128.6071,
                        "문구", Category.LIFE, StoreStatus.OPEN, null, true, "09:00–21:00", null, "북구"),
                store("514-81-10007", "알파문구 산격점", "대구 북구 산격로 55", 35.8901, 128.6152,
                        "문구", Category.LIFE, StoreStatus.OPEN, null, false, null, null, "북구"),
                store("514-81-10008", "주연 이비인후과", "대구 북구 침산로 40", 35.8847, 128.5993,
                        "병원", Category.LIFE, StoreStatus.OPEN, null, true, "09:00–18:00", "일요일", "북구"),
                store("514-81-10009", "옛집 손칼국수", "대구 중구 큰장로26길 14", 35.8697, 128.5793,
                        "국수", Category.MEAL, StoreStatus.OPEN, null, true, "09:00–19:00", null, "중구"),
                store("514-81-10010", "서문 옛맛 만두", "대구 중구 큰장로26길 31", 35.8703, 128.5801,
                        "만두", Category.MEAL, StoreStatus.OPEN, null, true, "10:00–20:00", null, "중구"),
                store("514-81-10011", "진골목 팥죽", "대구 중구 큰장로26길 8", 35.8688, 128.5787,
                        "디저트", Category.CAFE, StoreStatus.OPEN, null, true, "09:00–19:00", "둘째·넷째 일요일", "중구"),
                store("514-81-10012", "큰장 막창", "대구 중구 서성로 77", 35.8712, 128.5822,
                        "주점", Category.DRINK, StoreStatus.OPEN, null, true, "17:00–02:00", null, "중구"));
    }

    private Store store(
            String regno,
            String name,
            String address,
            double lat,
            double lng,
            String industry,
            Category category,
            StoreStatus status,
            LocalDate closedAt,
            boolean localPayMerchant,
            String hours,
            String holiday,
            String district) {
        return Store.builder()
                .regno(regno)
                .name(name)
                .address(address)
                .lat(lat)
                .lng(lng)
                .industry(industry)
                .category(category)
                .status(status)
                .closedAt(closedAt)
                .localPayMerchant(localPayMerchant)
                .hours(hours)
                .holiday(holiday)
                .region(Region.DAEGU)
                .district(district)
                .build();
    }

    private record Spec(String regno, String name, int visits, String from, String to, long avgAmount, PaySource source) {
    }

    private List<Payment> seedPayments() {
        List<Spec> specs = List.of(
                // 지역화폐로 잡히는 곳 — 동의 없이 v1에서 이미 보이는 8곳
                new Spec("514-81-10001", "봉덕 분식", 27, "2021-03-15", "2025-11-20", 8_500, PaySource.LOCALPAY),
                new Spec("514-81-10002", "가나 미용실", 19, "2021-04-11", "2025-08-02", 23_000, PaySource.LOCALPAY),
                new Spec("514-81-10003", "산격문구", 14, "2021-03-02", "2024-11-08", 12_400, PaySource.LOCALPAY),
                new Spec("514-81-10004", "오늘의커피", 24, "2022-01-20", "2025-10-14", 5_200, PaySource.LOCALPAY),
                new Spec("514-81-10009", "옛집 손칼국수", 11, "2021-09-04", "2025-05-17", 9_000, PaySource.LOCALPAY),
                new Spec("514-81-10010", "서문 옛맛 만두", 9, "2022-03-19", "2025-06-21", 7_000, PaySource.LOCALPAY),
                new Spec("514-81-10011", "진골목 팥죽", 7, "2021-11-30", "2025-02-09", 8_000, PaySource.LOCALPAY),
                new Spec("514-81-10012", "큰장 막창", 6, "2023-05-12", "2025-09-27", 34_000, PaySource.LOCALPAY),
                // 카드 연결 후 추가되는 4곳 — v2/v3
                new Spec("514-81-10005", "큐 안경점", 7, "2021-06-08", "2025-03-22", 148_000, PaySource.IM_CARD),
                new Spec("514-81-10006", "대현문구", 5, "2024-12-20", "2025-11-02", 11_000, PaySource.IM_CARD),
                new Spec("514-81-10008", "주연 이비인후과", 12, "2021-05-17", "2025-10-30", 42_000, PaySource.OTHER_CARD),
                new Spec("514-81-10007", "알파문구 산격점", 3, "2025-01-14", "2025-08-19", 9_800, PaySource.OTHER_CARD));

        List<Payment> rows = new ArrayList<>();
        int[] n = {0};
        for (Spec spec : specs) {
            for (Instant approvedAt : spreadDates(spec.from(), spec.to(), spec.visits())) {
                n[0] += 1;
                double wobble = 0.8 + ((n[0] * 37) % 40) / 100.0;
                long amount = Math.round(spec.avgAmount() * wobble / 100.0) * 100;
                rows.add(Payment.builder()
                        .userId(MAIN_USER)
                        .merchantRegno(spec.regno())
                        .merchantName(spec.name())
                        .approvedAt(approvedAt)
                        .amount(amount)
                        .payType(spec.source() == PaySource.LOCALPAY ? PayType.PREPAID : PayType.CHECK)
                        .status(PaymentStatus.APPROVED)
                        .source(spec.source())
                        .build());
            }
        }

        // 취소 건 — 규칙 엔진이 제외하는 걸 보여주기 위해
        rows.add(Payment.builder()
                .userId(MAIN_USER)
                .merchantRegno("514-81-10001")
                .merchantName("봉덕 분식")
                .approvedAt(LocalDate.of(2024, 7, 13).atTime(12, 10).toInstant(ZoneOffset.UTC))
                .amount(9_000)
                .payType(PayType.PREPAID)
                .status(PaymentStatus.CANCELED)
                .source(PaySource.LOCALPAY)
                .build());
        rows.add(Payment.builder()
                .userId(MAIN_USER)
                .merchantRegno("514-81-10004")
                .merchantName("오늘의커피")
                .approvedAt(LocalDate.of(2025, 2, 28).atTime(9, 40).toInstant(ZoneOffset.UTC))
                .amount(5_500)
                .payType(PayType.PREPAID)
                .status(PaymentStatus.CANCELED)
                .source(PaySource.LOCALPAY)
                .build());

        return rows;
    }

    /** FE mocks/payments.ts spreadDates() 포팅 — 결정적 의사난수라 재기동해도 같은 데이터가 나온다 */
    private List<Instant> spreadDates(String from, String to, int count) {
        long start = LocalDate.parse(from).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long end = LocalDate.parse(to).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        List<Instant> out = new ArrayList<>();
        double seed = 42;
        for (int i = 0; i < count; i++) {
            seed = (seed * 1103515245 + 12345) % 2147483648.0;
            double jitter = (seed / 2147483648.0 - 0.5) * 0.6;
            double ratio = Math.min(0.999, Math.max(0, (i + 0.5) / count + jitter / count));
            long millis = start + Math.round((end - start) * ratio);
            Instant approx = Instant.ofEpochMilli(millis);

            int monthIndex = approx.atZone(ZoneOffset.UTC).getMonthValue() - 1;
            double weight = SEASON_WEIGHT[monthIndex];
            long dayShiftMillis = Math.round((weight - 1) * 12) * 86_400_000L;

            out.add(approx.plusMillis(dayShiftMillis));
        }
        out.sort(Instant::compareTo);
        return out;
    }

    private List<MerchantSnapshot> seedMerchantSnapshots() {
        return List.of(
                // 봉덕 분식 — 지역화폐 가맹 + 당행 결제, 정상 집계
                MerchantSnapshot.builder()
                        .storeRegno("514-81-10001")
                        .regularCount(12)
                        .dormantCount(3)
                        .newThisMonth(2)
                        .weeklySlotTotal(2)
                        .build(),
                // 큐 안경점 — 미가맹, 단골 0명 (발표 스크린샷용: "우리 가게가 아예 안 보인다")
                MerchantSnapshot.builder()
                        .storeRegno("514-81-10005")
                        .regularCount(0)
                        .dormantCount(0)
                        .newThisMonth(0)
                        .weeklySlotTotal(2)
                        .build());
    }

    private List<DistrictRankingEntry> seedDistrictRanking() {
        return List.of(
                ranking("seomun", "서문시장", "514-81-10009", 84, 4.1, 62, 2),
                ranking("seomun", "서문시장", "514-81-10010", 61, 3.6, 44, 4),
                ranking("seomun", "서문시장", "514-81-10011", 47, 3.2, 38, 6),
                ranking("seomun", "서문시장", "514-81-10012", 22, 2.4, 17, 5));
    }

    private DistrictRankingEntry ranking(
            String code, String name, String regno, int regularCount, double avgYears, int recent6m, int walkMinutes) {
        return DistrictRankingEntry.builder()
                .districtCode(code)
                .districtName(name)
                .storeRegno(regno)
                .regularCount(regularCount)
                .avgYears(avgYears)
                .recent6mVisitors(recent6m)
                .walkMinutes(walkMinutes)
                .build();
    }
}

package com.gcc.impossible.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcc.impossible.merchant.MerchantSnapshot;
import com.gcc.impossible.merchant.MerchantSnapshotRepository;
import com.gcc.impossible.ontology.OntologyConstants;
import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.payment.PayType;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.regular.RegularJudgeService;
import com.gcc.impossible.regular.RegularStatusDto;
import com.gcc.impossible.seed.json.PaymentJson;
import com.gcc.impossible.seed.json.PaymentsFile;
import com.gcc.impossible.seed.json.StoreJson;
import com.gcc.impossible.seed.json.StoresFile;
import com.gcc.impossible.store.Region;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 데이터팀(주희) 노션 "DATA (ONTOLOGY)" 핸드오프 원본을 그대로 시드한다.
 *
 * 가게 마스터·U001(시연 주인공) 결제·태산만두 대시보드용 익명 결제·방문객 랭킹용 익명 결제
 * 전부 {@code src/main/resources/data/*.json}에 데이터팀이 준 원본 그대로 들어있고,
 * 이 클래스는 그걸 읽어서 엔티티로 변환만 한다 (숫자를 코드에 하드코딩하지 않음).
 *
 * payments_ranking_sample.json이 랭킹 대상 14개 가게(다락방 만두·바뷔치·동성로 떡볶이·
 * 미진분식·넌테이블·보바룸·팜테이블·제임스레코드·요바나시·하즈키·트랙·소란·카페루시드·노스폴)
 * 전부를 실제 결제 건으로 커버하므로, 예전에 나머지 가게용으로 익명 유저를 결정적으로
 * 생성해 채우던 로직은 제거했다 — 남겨두면 같은 가게에 실데이터 + 가짜 유저가 겹쳐
 * 랭킹의 "단골 수"가 중복 집계된다.
 */
@Slf4j
@Component
public class DemoDataSeeder implements ApplicationRunner {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StoreRepository storeRepository;
    private final PaymentRepository paymentRepository;
    private final MerchantSnapshotRepository merchantSnapshotRepository;
    private final RegularJudgeService regularJudgeService;

    public DemoDataSeeder(
            StoreRepository storeRepository,
            PaymentRepository paymentRepository,
            MerchantSnapshotRepository merchantSnapshotRepository,
            RegularJudgeService regularJudgeService) {
        this.storeRepository = storeRepository;
        this.paymentRepository = paymentRepository;
        this.merchantSnapshotRepository = merchantSnapshotRepository;
        this.regularJudgeService = regularJudgeService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        if (storeRepository.count() > 0) {
            log.info("시드 데이터가 이미 존재합니다 — 건너뜁니다.");
            return;
        }

        Map<String, Store> storesByRegno = seedStores();
        log.info("가게 마스터 {}곳 시드 완료", storesByRegno.size());

        List<Payment> payments = new ArrayList<>();
        payments.addAll(loadPayments("data/payments_main.json", storesByRegno));
        payments.addAll(loadPayments("data/payments_dashboard_sample.json", storesByRegno));
        payments.addAll(loadPayments("data/payments_ranking_sample.json", storesByRegno));
        paymentRepository.saveAll(payments);
        log.info("결제 원장 {}건 시드 완료", payments.size());

        seedMerchantSnapshot(storesByRegno.get("111-11-11111"), payments);
        log.info("사장님 대시보드(태산만두) 시드 완료");
    }

    private Map<String, Store> seedStores() throws IOException {
        StoresFile file = readJson("data/stores.json", StoresFile.class);
        Map<String, Store> byRegno = new LinkedHashMap<>();
        for (StoreJson s : file.stores()) {
            Store store = Store.builder()
                    .regno(s.bizNo())
                    .name(s.name())
                    .address(s.address())
                    .lat(s.lat())
                    .lng(s.lng())
                    .category(s.category())
                    .status("영업".equals(s.status()) ? StoreStatus.OPEN : StoreStatus.CLOSED)
                    .closedAt(null)
                    .localPayMerchant(localPayMerchantOf(s))
                    .region(regionOf(s.address()))
                    .district(districtOf(s.address()))
                    .build();
            byRegno.put(store.getRegno(), store);
        }
        storeRepository.saveAll(byRegno.values());
        return byRegno;
    }

    /**
     * 원본엔 지역화폐(대구로페이) 가맹 여부가 두 가지 이름으로 나뉘어 있다 — 부동산·가구·랭킹용 가게는
     * localCurrencyAffiliated(지역화폐 가맹 여부)를 그대로 쓰고, 태산만두~사과문구(분식/카페/미용실/문구)는
     * easyPayAffiliated(제로페이 등 간편결제 가맹 여부)만 있다.
     * 간편결제 가맹이 곧 지역화폐 가맹인지는 데이터팀 확인 대기 중 — 확인 전까지는 임시로 같다고 본다.
     */
    private boolean localPayMerchantOf(StoreJson s) {
        if (s.localCurrencyAffiliated() != null) {
            return s.localCurrencyAffiliated();
        }
        return Boolean.TRUE.equals(s.easyPayAffiliated());
    }

    /** region/district는 원본 stores.json에 없는 필드라 주소 앞부분에서 유도한다. */
    private Region regionOf(String address) {
        return address.startsWith("서울") ? Region.SEOUL : Region.DAEGU;
    }

    private static final Set<String> REGION_TOKENS = Set.of("대구", "대구광역시", "서울", "서울특별시");
    private static final Pattern DISTRICT_PATTERN = Pattern.compile("\\S*[가-힣]구$");

    private String districtOf(String address) {
        for (String token : address.split(" ")) {
            if (REGION_TOKENS.contains(token)) {
                continue;
            }
            if (DISTRICT_PATTERN.matcher(token).matches()) {
                return token;
            }
        }
        throw new IllegalStateException("주소에서 구(district)를 찾을 수 없습니다: " + address);
    }

    private List<Payment> loadPayments(String classpathFile, Map<String, Store> storesByRegno) throws IOException {
        PaymentsFile file = readJson(classpathFile, PaymentsFile.class);
        List<Payment> rows = new ArrayList<>();
        for (PaymentJson p : file.payments()) {
            Store store = storesByRegno.get(p.bizNo());
            if (store == null) {
                log.warn("결제 원장에 매칭되는 가게가 없습니다 (bizNo={}) — 건너뜁니다.", p.bizNo());
                continue;
            }
            rows.add(Payment.builder()
                    .userId(p.userId())
                    .merchantRegno(p.bizNo())
                    .merchantName(p.merchantName() != null ? p.merchantName() : store.getName())
                    .approvedAt(LocalDateTime.parse(p.approvedAt()).toInstant(ZoneOffset.UTC))
                    .amount(p.amount())
                    .payType("체크".equals(p.cardType()) ? PayType.CHECK : PayType.CREDIT)
                    .status("승인".equals(p.status()) ? PaymentStatus.APPROVED : PaymentStatus.CANCELED)
                    .source(store.isLocalPayMerchant() ? PaySource.LOCALPAY : PaySource.IM_CARD)
                    .build());
        }
        return rows;
    }

    /** 태산만두(111-11-11111) 사장님 대시보드 집계값 — 실제로 임포트된 결제 원장에서 계산한다 */
    private void seedMerchantSnapshot(Store taesan, List<Payment> allPayments) {
        if (taesan == null) {
            return;
        }
        Map<String, List<Payment>> byUser = allPayments.stream()
                .filter(p -> p.getMerchantRegno().equals(taesan.getRegno()))
                .collect(Collectors.groupingBy(Payment::getUserId));

        Instant now = Instant.now();
        Instant sixMonthsAgo = now.minusSeconds(183L * 86_400);
        YearMonth thisMonth = YearMonth.from(now.atZone(ZoneOffset.UTC));

        int regularCount = 0;
        int dormantCount = 0;
        int newThisMonth = 0;
        for (List<Payment> userPayments : byUser.values()) {
            RegularStatusDto status = regularJudgeService.judge(taesan, userPayments).orElse(null);
            if (status == null || !status.isRegular()) {
                continue;
            }
            regularCount++;
            if (status.lastVisit().isBefore(sixMonthsAgo)) {
                dormantCount++;
            }
            if (YearMonth.from(status.firstVisit().atZone(ZoneOffset.UTC)).equals(thisMonth)) {
                newThisMonth++;
            }
        }

        merchantSnapshotRepository.save(MerchantSnapshot.builder()
                .storeRegno(taesan.getRegno())
                .regularCount(regularCount)
                .dormantCount(dormantCount)
                .newThisMonth(newThisMonth)
                .weeklySlotTotal(OntologyConstants.WEEKLY_SEND_SLOTS)
                .build());
    }

    private <T> T readJson(String classpathFile, Class<T> type) throws IOException {
        try (InputStream in = new ClassPathResource(classpathFile).getInputStream()) {
            return objectMapper.readValue(in, type);
        }
    }
}

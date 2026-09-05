package com.gcc.impossible.regular;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gcc.impossible.ontology.Category;
import com.gcc.impossible.ontology.OntologyProperties;
import com.gcc.impossible.ontology.OntologyService;
import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.payment.PayType;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.store.Region;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegularJudgeServiceTest {

    private RegularJudgeService target;

    @BeforeEach
    void setUp() {
        OntologyProperties properties = new OntologyProperties();
        properties.setIndustries(List.of(
                entry("분식", Category.MEAL, 10, true),
                entry("문구", Category.LIFE, 15, true)));
        target = new RegularJudgeService(new OntologyService(properties));
    }

    @Test
    void 봉덕분식_27회는_단골이다() {
        Store store = store("514-81-10001", "봉덕 분식", "분식", Category.MEAL);
        List<Payment> payments = approvedPayments(store.getRegno(), 27);

        RegularStatusDto result = target.judge(store, payments).orElseThrow();

        assertTrue(result.isRegular());
        assertEquals(27, result.visits());
        assertEquals(10, result.threshold());
    }

    @Test
    void 알파문구_3회는_기준미달이다() {
        Store store = store("514-81-10007", "알파문구 산격점", "문구", Category.LIFE);
        List<Payment> payments = approvedPayments(store.getRegno(), 3);

        RegularStatusDto result = target.judge(store, payments).orElseThrow();

        assertFalse(result.isRegular());
        assertEquals(3, result.visits());
        assertEquals(15, result.threshold());
    }

    @Test
    void 취소건은_방문횟수에서_제외된다() {
        Store store = store("514-81-10001", "봉덕 분식", "분식", Category.MEAL);
        List<Payment> payments = new ArrayList<>(approvedPayments(store.getRegno(), 5));
        payments.add(Payment.builder()
                .merchantRegno(store.getRegno())
                .approvedAt(Instant.now())
                .amount(1_000)
                .status(PaymentStatus.CANCELED)
                .source(PaySource.LOCALPAY)
                .payType(PayType.PREPAID)
                .build());

        RegularStatusDto result = target.judge(store, payments).orElseThrow();

        assertEquals(5, result.visits());
    }

    @Test
    void 결제이력이_없으면_빈값을_반환한다() {
        Store store = store("514-81-10099", "결제없는가게", "분식", Category.MEAL);

        assertTrue(target.judge(store, List.of()).isEmpty());
    }

    private OntologyProperties.Entry entry(String industry, Category category, int threshold, boolean substitutable) {
        OntologyProperties.Entry entry = new OntologyProperties.Entry();
        entry.setIndustry(industry);
        entry.setCategory(category);
        entry.setThreshold(threshold);
        entry.setSubstitutable(substitutable);
        return entry;
    }

    private Store store(String regno, String name, String industry, Category category) {
        return Store.builder()
                .regno(regno)
                .name(name)
                .industry(industry)
                .category(category)
                .status(StoreStatus.OPEN)
                .region(Region.DAEGU)
                .district("북구")
                .build();
    }

    private List<Payment> approvedPayments(String regno, int count) {
        List<Payment> list = new ArrayList<>();
        Instant base = Instant.parse("2022-01-01T00:00:00Z");
        for (int i = 0; i < count; i++) {
            list.add(Payment.builder()
                    .merchantRegno(regno)
                    .approvedAt(base.plus(30L * i, ChronoUnit.DAYS))
                    .amount(1_000)
                    .status(PaymentStatus.APPROVED)
                    .source(PaySource.LOCALPAY)
                    .payType(PayType.PREPAID)
                    .build());
        }
        return list;
    }
}

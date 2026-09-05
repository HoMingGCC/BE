package com.gcc.impossible.regular;

import com.gcc.impossible.ontology.IndustryRule;
import com.gcc.impossible.ontology.OntologyService;
import com.gcc.impossible.payment.PaySource;
import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 단골 판정 — 규칙 기반 (FE src/lib/regular.ts judge()/judgeAll() 포팅).
 *
 * 확률 모델을 안 쓰는 이유: 화면에서 "미용실 기준 10회 · 현재 19회 → 인증"처럼
 * 근거를 그대로 보여줘야 하기 때문. 취소 건은 제외한다.
 */
@Service
public class RegularJudgeService {

    private static final double MS_PER_YEAR = 1000.0 * 60 * 60 * 24 * 365;

    private final OntologyService ontologyService;

    public RegularJudgeService(OntologyService ontologyService) {
        this.ontologyService = ontologyService;
    }

    public Optional<RegularStatusDto> judge(Store store, List<Payment> payments) {
        List<Payment> rows = payments.stream()
                .filter(p -> p.getMerchantRegno().equals(store.getRegno()) && p.getStatus() == PaymentStatus.APPROVED)
                .sorted(Comparator.comparing(Payment::getApprovedAt))
                .toList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Instant first = rows.get(0).getApprovedAt();
        Instant last = rows.get(rows.size() - 1).getApprovedAt();
        IndustryRule rule = ontologyService.ruleOf(store.getIndustry());

        double yearsSpan = Math.round((Duration.between(first, last).toMillis() / MS_PER_YEAR) * 10) / 10.0;

        int[] monthlyVisits = new int[12];
        ZonedDateTime now = last.atZone(ZoneOffset.UTC);
        for (Payment p : rows) {
            ZonedDateTime visited = p.getApprovedAt().atZone(ZoneOffset.UTC);
            int diff = (now.getYear() - visited.getYear()) * 12 + (now.getMonthValue() - visited.getMonthValue());
            if (diff >= 0 && diff < 12) {
                monthlyVisits[11 - diff] += 1;
            }
        }

        Set<PaySource> sources = rows.stream().map(Payment::getSource).collect(Collectors.toSet());
        PaySource source = sources.contains(PaySource.LOCALPAY)
                ? PaySource.LOCALPAY
                : sources.contains(PaySource.IM_CARD) ? PaySource.IM_CARD : PaySource.OTHER_CARD;

        long totalAmount = rows.stream().mapToLong(Payment::getAmount).sum();

        return Optional.of(new RegularStatusDto(
                StoreDto.from(store),
                rows.size(),
                first,
                last,
                totalAmount,
                rule.threshold(),
                rows.size() >= rule.threshold(),
                yearsSpan,
                monthlyVisits,
                source));
    }

    public List<RegularStatusDto> judgeAll(List<Store> stores, List<Payment> payments) {
        return stores.stream()
                .map(store -> judge(store, payments))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingInt(RegularStatusDto::visits).reversed())
                .toList();
    }
}

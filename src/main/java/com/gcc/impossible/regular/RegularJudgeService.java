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
 * 단골 판정 — 데이터팀(주희) 노션 "업종별 단골 판정 로직" 의사코드 그대로.
 *
 * 방문횟수는 "오늘 기준 최근 periodMonths개월 이내" 유효 결제만 센다(취소 제외).
 * 다만 최초 방문일·연차·최근 1년 미니차트는 전체 이력 기준으로 계산한다 —
 * "3년째 다니는 단골인데 최근엔 뜸하다"는 맥락까지 보여줘야 해서다.
 */
@Service
public class RegularJudgeService {

    private static final double MS_PER_YEAR = 1000.0 * 60 * 60 * 24 * 365;

    private final OntologyService ontologyService;

    public RegularJudgeService(OntologyService ontologyService) {
        this.ontologyService = ontologyService;
    }

    public Optional<RegularStatusDto> judge(Store store, List<Payment> payments) {
        List<Payment> allApproved = payments.stream()
                .filter(p -> p.getMerchantRegno().equals(store.getRegno()) && p.getStatus() == PaymentStatus.APPROVED)
                .sorted(Comparator.comparing(Payment::getApprovedAt))
                .toList();

        if (allApproved.isEmpty()) {
            return Optional.empty();
        }

        IndustryRule rule = ontologyService.ruleOf(store.getCategory());

        Instant windowStart =
                Instant.now().atZone(ZoneOffset.UTC).minusMonths(rule.periodMonths()).toInstant();
        List<Payment> recentRows =
                allApproved.stream().filter(p -> !p.getApprovedAt().isBefore(windowStart)).toList();

        Instant first = allApproved.get(0).getApprovedAt();
        Instant last = allApproved.get(allApproved.size() - 1).getApprovedAt();

        double yearsSpan = Math.round((Duration.between(first, Instant.now()).toMillis() / MS_PER_YEAR) * 10) / 10.0;

        int[] monthlyVisits = new int[12];
        ZonedDateTime chartAnchor = last.atZone(ZoneOffset.UTC);
        for (Payment p : allApproved) {
            ZonedDateTime visited = p.getApprovedAt().atZone(ZoneOffset.UTC);
            int diff = (chartAnchor.getYear() - visited.getYear()) * 12 + (chartAnchor.getMonthValue() - visited.getMonthValue());
            if (diff >= 0 && diff < 12) {
                monthlyVisits[11 - diff] += 1;
            }
        }

        Set<PaySource> sources = allApproved.stream().map(Payment::getSource).collect(Collectors.toSet());
        PaySource source = sources.contains(PaySource.LOCALPAY)
                ? PaySource.LOCALPAY
                : sources.contains(PaySource.IM_CARD) ? PaySource.IM_CARD : PaySource.OTHER_CARD;

        long totalAmount = allApproved.stream().mapToLong(Payment::getAmount).sum();

        return Optional.of(new RegularStatusDto(
                StoreDto.from(store),
                recentRows.size(),
                first,
                last,
                totalAmount,
                rule.minVisits(),
                recentRows.size() >= rule.minVisits(),
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

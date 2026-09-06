package com.gcc.impossible.migration;

import com.gcc.impossible.payment.Payment;
import com.gcc.impossible.payment.PaymentRepository;
import com.gcc.impossible.payment.PaymentStatus;
import com.gcc.impossible.store.Region;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import com.gcc.impossible.store.StoreRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * 이주(자취 시작) 감지 — 데이터팀(주희) 노션 "이주·자취 감지 로직" 의사코드 그대로.
 *
 * "결제 지역이 바뀌었다"는 신호 하나만으론 오탐(여행 등)이 나올 수 있어서,
 * ① 대구 결제 공백(gapDays 이상) ② 그 공백 기간 중 정착업종(부동산·가구) 결제
 * 두 조건이 **둘 다** 있어야 이주로 판정한다.
 */
@Service
@EnableConfigurationProperties(MigrationProperties.class)
public class MigrationDetectionService {

    private final PaymentRepository paymentRepository;
    private final StoreRepository storeRepository;
    private final MigrationProperties properties;

    public MigrationDetectionService(
            PaymentRepository paymentRepository, StoreRepository storeRepository, MigrationProperties properties) {
        this.paymentRepository = paymentRepository;
        this.storeRepository = storeRepository;
        this.properties = properties;
    }

    public MigrationStatusDto detect(String userId) {
        List<Payment> payments = paymentRepository.findByUserIdAndStatus(userId, PaymentStatus.APPROVED);
        if (payments.isEmpty()) {
            return MigrationStatusDto.notDetected();
        }

        Map<String, Store> storesByRegno = storeRepository
                .findAllById(payments.stream().map(Payment::getMerchantRegno).distinct().toList())
                .stream()
                .collect(Collectors.toMap(Store::getRegno, Function.identity()));

        Instant now = Instant.now();

        // 1) 최근 대구 결제 공백 확인
        Instant lastDaeguPaymentAt = payments.stream()
                .filter(p -> storesByRegno.get(p.getMerchantRegno()).getRegion() == Region.DAEGU)
                .map(Payment::getApprovedAt)
                .max(Instant::compareTo)
                .orElse(null);
        if (lastDaeguPaymentAt == null) {
            return MigrationStatusDto.notDetected();
        }

        long gapDays = Duration.between(lastDaeguPaymentAt, now).toDays();
        if (gapDays < properties.getGapDays()) {
            return MigrationStatusDto.notDetected();
        }

        // 2) 공백 기간 중 정착업종(부동산·가구) 결제 확인
        List<Payment> settlementPayments = payments.stream()
                .filter(p -> !p.getApprovedAt().isBefore(lastDaeguPaymentAt) && !p.getApprovedAt().isAfter(now))
                .filter(p -> properties.getSettlementIndustries().contains(storesByRegno.get(p.getMerchantRegno()).getCategory()))
                .sorted(Comparator.comparing(Payment::getApprovedAt))
                .toList();
        if (settlementPayments.isEmpty()) {
            return MigrationStatusDto.notDetected();
        }

        // 3) 조건 충족 — 이주 이벤트 확정
        Store firstSettlementStore = storesByRegno.get(settlementPayments.get(0).getMerchantRegno());
        List<StoreDto> evidence = settlementPayments.stream()
                .map(p -> storesByRegno.get(p.getMerchantRegno()))
                .distinct()
                .map(StoreDto::from)
                .toList();

        return new MigrationStatusDto(true, firstSettlementStore.getRegion(), (int) gapDays, lastDaeguPaymentAt, evidence);
    }
}

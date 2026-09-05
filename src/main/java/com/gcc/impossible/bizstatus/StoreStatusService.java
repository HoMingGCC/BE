package com.gcc.impossible.bizstatus;

import com.gcc.impossible.common.ApiException;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 폐업·근황 감지 — 사업자번호로 국세청 상태를 조회하고, 시드 사업자번호처럼 국세청에
 * 등록되지 않은 경우(데모용 가짜 번호)는 가게 마스터에 미리 넣어둔 status로 폴백한다.
 * 배치 조회 결과는 TTL 캐시로 재사용해 매 요청마다 외부 API를 부르지 않는다.
 */
@Service
public class StoreStatusService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final NtsBusinessStatusClient client;
    private final StoreRepository storeRepository;

    private volatile CacheEntry cache;

    public StoreStatusService(NtsBusinessStatusClient client, StoreRepository storeRepository) {
        this.client = client;
        this.storeRepository = storeRepository;
    }

    public BusinessStatusDto resolveStatus(String regno) {
        Store store = storeRepository
                .findById(regno)
                .orElseThrow(() -> ApiException.notFound("가게를 찾을 수 없습니다: " + regno));

        Map<String, NtsStatusItem> results = freshCache();
        NtsStatusItem item = results.get(regno.replace("-", ""));

        if (item != null && item.isRegistered()) {
            return new BusinessStatusDto(regno, mapStatus(item.b_stt_cd()), "nts", item.b_stt(), Instant.now());
        }

        String message = store.getStatus() == StoreStatus.CLOSED
                ? "폐업 (시드 데이터 기준 — 사업자번호가 데모용이라 국세청에 등록되어 있지 않음)"
                : "영업 중 (시드 데이터 기준)";
        return new BusinessStatusDto(regno, store.getStatus(), "seed-fallback", message, Instant.now());
    }

    private Map<String, NtsStatusItem> freshCache() {
        CacheEntry current = cache;
        if (current != null && Duration.between(current.fetchedAt(), Instant.now()).compareTo(CACHE_TTL) < 0) {
            return current.data();
        }
        synchronized (this) {
            current = cache;
            if (current != null && Duration.between(current.fetchedAt(), Instant.now()).compareTo(CACHE_TTL) < 0) {
                return current.data();
            }
            List<String> regnos = storeRepository.findAll().stream().map(Store::getRegno).toList();
            Map<String, NtsStatusItem> data = client.lookupStatuses(regnos);
            cache = new CacheEntry(Instant.now(), data);
            return data;
        }
    }

    /** 01 계속사업자 → 영업 / 02 휴업자·03 폐업자 → 근황 알림 대상이므로 둘 다 CLOSED로 취급 */
    private StoreStatus mapStatus(String code) {
        return "01".equals(code) ? StoreStatus.OPEN : StoreStatus.CLOSED;
    }

    private record CacheEntry(Instant fetchedAt, Map<String, NtsStatusItem> data) {
    }
}

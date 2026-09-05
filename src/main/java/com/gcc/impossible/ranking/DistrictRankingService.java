package com.gcc.impossible.ranking;

import com.gcc.impossible.ontology.Category;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import com.gcc.impossible.store.StoreRepository;
import com.gcc.impossible.store.StoreStatus;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** FE mocks/visitor.ts 의 rankingOf() 포팅 — 코드가 없으면 서문시장으로 폴백한다. */
@Service
public class DistrictRankingService {

    private static final String DEFAULT_DISTRICT_CODE = "seomun";

    private final DistrictRankingRepository districtRankingRepository;
    private final StoreRepository storeRepository;

    public DistrictRankingService(DistrictRankingRepository districtRankingRepository, StoreRepository storeRepository) {
        this.districtRankingRepository = districtRankingRepository;
        this.storeRepository = storeRepository;
    }

    public DistrictRankingDto getRanking(String districtCode, Category categoryFilter) {
        List<DistrictRankingEntry> entries =
                districtRankingRepository.findByDistrictCodeOrderByRegularCountDesc(districtCode);
        if (entries.isEmpty()) {
            entries = districtRankingRepository.findByDistrictCodeOrderByRegularCountDesc(DEFAULT_DISTRICT_CODE);
        }
        if (entries.isEmpty()) {
            return new DistrictRankingDto(districtCode, districtCode, List.of());
        }

        Map<String, Store> stores = storeRepository
                .findAllById(entries.stream().map(DistrictRankingEntry::getStoreRegno).toList())
                .stream()
                .collect(Collectors.toMap(Store::getRegno, Function.identity()));

        List<DistrictRankingItemDto> items = entries.stream()
                .map(entry -> toItem(entry, stores.get(entry.getStoreRegno())))
                .filter(item -> item.store() != null)
                // 지금 영업 중인 가게만 노출 — 시장은 폐점 시간이 제각각이라 갈 수 없는 곳은 의미가 없음
                .filter(item -> item.isOpenNow())
                .filter(item -> categoryFilter == null || item.store().category() == categoryFilter)
                .toList();

        String districtName = entries.get(0).getDistrictName();
        return new DistrictRankingDto(entries.get(0).getDistrictCode(), districtName, items);
    }

    private DistrictRankingItemDto toItem(DistrictRankingEntry entry, Store store) {
        if (store == null) {
            return new DistrictRankingItemDto(null, 0, 0, 0, 0, false);
        }
        return new DistrictRankingItemDto(
                StoreDto.from(store),
                entry.getRegularCount(),
                entry.getAvgYears(),
                entry.getRecent6mVisitors(),
                entry.getWalkMinutes(),
                store.getStatus() == StoreStatus.OPEN);
    }
}

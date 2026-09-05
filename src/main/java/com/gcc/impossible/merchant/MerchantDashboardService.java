package com.gcc.impossible.merchant;

import com.gcc.impossible.ontology.OntologyConstants;
import com.gcc.impossible.store.Store;
import com.gcc.impossible.store.StoreDto;
import com.gcc.impossible.store.StoreService;
import org.springframework.stereotype.Service;

@Service
public class MerchantDashboardService {

    private final StoreService storeService;
    private final MerchantSnapshotRepository merchantSnapshotRepository;
    private final NewsSendLogRepository newsSendLogRepository;

    public MerchantDashboardService(
            StoreService storeService,
            MerchantSnapshotRepository merchantSnapshotRepository,
            NewsSendLogRepository newsSendLogRepository) {
        this.storeService = storeService;
        this.merchantSnapshotRepository = merchantSnapshotRepository;
        this.newsSendLogRepository = newsSendLogRepository;
    }

    public MerchantDashboardDto getDashboard(String regno) {
        Store store = storeService.getByRegno(regno);
        MerchantSnapshot snapshot = merchantSnapshotRepository
                .findById(regno)
                .orElseGet(() -> MerchantSnapshot.builder()
                        .storeRegno(regno)
                        .regularCount(0)
                        .dormantCount(0)
                        .newThisMonth(0)
                        .weeklySlotTotal(OntologyConstants.WEEKLY_SEND_SLOTS)
                        .build());

        long weeklySlotUsed = newsSendLogRepository.countByStoreRegnoAndSentAtAfter(regno, WeekWindow.startOfCurrentWeek());

        return new MerchantDashboardDto(
                StoreDto.from(store),
                snapshot.getRegularCount(),
                snapshot.getDormantCount(),
                snapshot.getNewThisMonth(),
                OntologyConstants.RANK_MIN_REGULARS,
                (int) weeklySlotUsed,
                snapshot.getWeeklySlotTotal());
    }
}

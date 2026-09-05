package com.gcc.impossible.merchant;

import com.gcc.impossible.common.ApiException;
import com.gcc.impossible.ontology.OntologyConstants;
import com.gcc.impossible.store.StoreService;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소식 보내기 — 사용자당(=가게당) 주 2건 총량 제한.
 * 슬롯이 희소해야 값이 매겨진다는 BM 논리를 그대로 구현: 실시간 강제 push가 아니라 슬롯 소모형.
 */
@Service
public class NewsSendService {

    private final StoreService storeService;
    private final MerchantSnapshotRepository merchantSnapshotRepository;
    private final NewsSendLogRepository newsSendLogRepository;

    public NewsSendService(
            StoreService storeService,
            MerchantSnapshotRepository merchantSnapshotRepository,
            NewsSendLogRepository newsSendLogRepository) {
        this.storeService = storeService;
        this.merchantSnapshotRepository = merchantSnapshotRepository;
        this.newsSendLogRepository = newsSendLogRepository;
    }

    @Transactional
    public NewsSendResultDto send(String regno, String message) {
        storeService.getByRegno(regno); // 존재하지 않으면 404

        int weeklySlotTotal = merchantSnapshotRepository
                .findById(regno)
                .map(MerchantSnapshot::getWeeklySlotTotal)
                .orElse(OntologyConstants.WEEKLY_SEND_SLOTS);

        Instant weekStart = WeekWindow.startOfCurrentWeek();
        long used = newsSendLogRepository.countByStoreRegnoAndSentAtAfter(regno, weekStart);

        if (used >= weeklySlotTotal) {
            throw ApiException.conflict("이번 주 발송 슬롯을 모두 사용했습니다 (%d/%d)".formatted(used, weeklySlotTotal));
        }

        newsSendLogRepository.save(NewsSendLog.builder()
                .storeRegno(regno)
                .sentAt(Instant.now())
                .message(message)
                .build());

        return new NewsSendResultDto((int) used + 1, weeklySlotTotal);
    }
}

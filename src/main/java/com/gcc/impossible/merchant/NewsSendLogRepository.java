package com.gcc.impossible.merchant;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsSendLogRepository extends JpaRepository<NewsSendLog, Long> {

    long countByStoreRegnoAndSentAtAfter(String storeRegno, Instant after);
}

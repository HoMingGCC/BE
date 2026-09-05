package com.gcc.impossible.ranking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRankingRepository extends JpaRepository<DistrictRankingEntry, Long> {

    List<DistrictRankingEntry> findByDistrictCodeOrderByRegularCountDesc(String districtCode);
}

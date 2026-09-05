package com.gcc.impossible.store;

import com.gcc.impossible.ontology.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 가게 마스터 — 실서비스: 당행 가맹점 마스터 + 대구 공공데이터 인허가 API. 사업자등록번호가 PK. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @Column(length = 20)
    private String regno;

    private String name;

    private String address;

    private double lat;

    private double lng;

    /** '분식' '미용실' '문구' 등 — 온톨로지 매칭용 */
    private String industry;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    private LocalDate closedAt;

    private boolean localPayMerchant;

    private String hours;

    private String holiday;

    @Enumerated(EnumType.STRING)
    private Region region;

    private String district;
}

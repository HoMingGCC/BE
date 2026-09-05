package com.gcc.impossible.merchant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사장님 대시보드 집계값 (합성). 실서비스에서는 당행 결제 원장을 가게 단위로 집계해서 채운다.
 * 데모는 다중 고객 데이터가 없어 FE mocks/merchant.ts 값을 그대로 시드한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantSnapshot {

    @Id
    @Column(length = 20)
    private String storeRegno;

    private int regularCount;

    private int dormantCount;

    private int newThisMonth;

    private int weeklySlotTotal;
}

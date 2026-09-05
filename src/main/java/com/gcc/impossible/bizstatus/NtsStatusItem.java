package com.gcc.impossible.bizstatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 국세청 사업자등록 상태조회 결과 한 건.
 * b_stt_cd: 01 계속사업자 / 02 휴업자 / 03 폐업자. 등록되지 않은 번호는 비어서 온다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NtsStatusItem(String b_no, String b_stt, String b_stt_cd, String tax_type, String end_dt) {

    public boolean isRegistered() {
        return b_stt_cd != null && !b_stt_cd.isBlank();
    }
}

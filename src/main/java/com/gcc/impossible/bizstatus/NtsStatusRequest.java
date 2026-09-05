package com.gcc.impossible.bizstatus;

import java.util.List;

/** 국세청 사업자등록 상태조회 API 요청 바디 — 숫자 10자리('-' 제거)만 가능, 1회 최대 100건 */
public record NtsStatusRequest(List<String> b_no) {
}

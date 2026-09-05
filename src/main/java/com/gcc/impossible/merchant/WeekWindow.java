package com.gcc.impossible.merchant;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

/** 소식 발송 주간 총량 제한 계산용 — 이번 주 월요일 00:00(시스템 기본 시간대) 기준 */
final class WeekWindow {

    private WeekWindow() {
    }

    static Instant startOfCurrentWeek() {
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}

package org.manage.service.holiday;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.domain.DayInfo;
import org.manage.domain.enums.DayType;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.manage.service.holiday.HolidayUpdater.CONSULTANT_SOURCE_TYPE;

@QuarkusTest
class HolidayServiceTest {

    @Inject
    HolidayService holidayService;

    @BeforeEach
    @Transactional
    void setup() {
        DayInfo.deleteAll();
    }

    @Test
    void testIsWorkingDay_Defaults() {
        // 2023-10-23 is Monday
        assertThat(holidayService.isWorkingDay(LocalDate.of(2023, 10, 23))).isTrue();
        // 2023-10-28 is Saturday
        assertThat(holidayService.isWorkingDay(LocalDate.of(2023, 10, 28))).isFalse();
        // 2023-10-29 is Sunday
        assertThat(holidayService.isWorkingDay(LocalDate.of(2023, 10, 29))).isFalse();
    }

    @Test
    @Transactional
    void testIsWorkingDay_WithHoliday() {
        LocalDate tuesday = LocalDate.of(2023, 10, 24);
        createDayInfo(tuesday, DayType.HOLIDAY);
        
        assertThat(holidayService.isWorkingDay(tuesday)).isFalse();
    }

    @Test
    @Transactional
    void testIsWorkingDay_WithWeekendInBase() {
        LocalDate wednesday = LocalDate.of(2023, 10, 25);
        createDayInfo(wednesday, DayType.WEEKEND);
        
        assertThat(holidayService.isWorkingDay(wednesday)).isFalse();
    }

    @Test
    @Transactional
    void testIsWorkingDay_WithPreHoliday() {
        LocalDate thursday = LocalDate.of(2023, 10, 26);
        createDayInfo(thursday, DayType.PRE_HOLIDAY);
        
        // Pre-holiday is a working day
        assertThat(holidayService.isWorkingDay(thursday)).isTrue();
    }

    @Test
    @Transactional
    void testIsWorkingDay_WorkingSaturday() {
        // 2023-10-28 is Saturday
        LocalDate saturday = LocalDate.of(2023, 10, 28);
        createDayInfo(saturday, DayType.PRE_HOLIDAY); // E.g. shifted working day
        
        assertThat(holidayService.isWorkingDay(saturday)).isTrue();
    }

    private void createDayInfo(LocalDate date, DayType type) {
        DayInfo dayInfo = new DayInfo();
        dayInfo.day = date;
        dayInfo.dayType = type;
        dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
        dayInfo.persist();
    }
}

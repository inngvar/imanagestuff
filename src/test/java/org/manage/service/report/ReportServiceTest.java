package org.manage.service.report;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.domain.DayInfo;
import org.manage.domain.Member;
import org.manage.domain.enums.DayType;
import org.manage.service.dto.DayRegisteredTimeDTO;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.manage.service.holiday.HolidayUpdater.CONSULTANT_SOURCE_TYPE;

@QuarkusTest
class ReportServiceTest {

    @Inject
    ReportService reportService;

    @BeforeEach
    @Transactional
    void setup() {
        org.manage.domain.TimeEntry.deleteAll();
        org.manage.domain.TimeLog.deleteAll();
        org.manage.domain.PendingLink.deleteAll();
        DayInfo.deleteAll();
        Member.deleteAll();
        
        Member member = new Member();
        member.login = "test_user";
        member.firstName = "Test";
        member.lastName = "User";
        member.persist();
    }

    @Test
    @Transactional
    void testIsHoliday_NormalDays() {
        // 2023-10-23 is Monday
        LocalDate monday = LocalDate.of(2023, 10, 23);
        // 2023-10-28 is Saturday
        LocalDate saturday = LocalDate.of(2023, 10, 28);
        // 2023-10-29 is Sunday
        LocalDate sunday = LocalDate.of(2023, 10, 29);

        List<DayRegisteredTimeDTO> report = reportService.getRegisteredTimeReport("test_user", monday, 7);

        // Monday should NOT be a holiday
        assertThat(getDay(report, monday).holiday).isFalse();
        // Saturday should be a holiday
        assertThat(getDay(report, saturday).holiday).isTrue();
        // Sunday should be a holiday
        assertThat(getDay(report, sunday).holiday).isTrue();
    }

    @Test
    @Transactional
    void testIsHoliday_WithDayInfo() {
        // 2023-10-24 is Tuesday (normally working day)
        LocalDate tuesday = LocalDate.of(2023, 10, 24);
        
        DayInfo dayInfo = new DayInfo();
        dayInfo.day = tuesday;
        dayInfo.dayType = DayType.HOLIDAY;
        dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
        dayInfo.persist();

        List<DayRegisteredTimeDTO> report = reportService.getRegisteredTimeReport("test_user", tuesday, 1);

        assertThat(getDay(report, tuesday).holiday).isTrue();
    }

    @Test
    @Transactional
    void testIsHoliday_PreHolidayIsWorkingDay() {
        // 2023-10-25 is Wednesday (normally working day)
        LocalDate wednesday = LocalDate.of(2023, 10, 25);
        
        DayInfo dayInfo = new DayInfo();
        dayInfo.day = wednesday;
        dayInfo.dayType = DayType.PRE_HOLIDAY;
        dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
        dayInfo.persist();

        List<DayRegisteredTimeDTO> report = reportService.getRegisteredTimeReport("test_user", wednesday, 1);

        // PRE_HOLIDAY should be a working day (holiday = false)
        assertThat(getDay(report, wednesday).holiday).isFalse(); 
    }

    private DayRegisteredTimeDTO getDay(List<DayRegisteredTimeDTO> report, LocalDate date) {
        return report.stream()
            .filter(d -> d.date.equals(date))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Date " + date + " not found in report"));
    }
}

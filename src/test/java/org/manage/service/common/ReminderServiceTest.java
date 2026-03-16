package org.manage.service.common;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.domain.DayInfo;
import org.manage.domain.Member;
import org.manage.domain.TimeEntry;
import org.manage.domain.TimeLog;
import org.manage.domain.enums.DayType;
import org.manage.service.TelegramBotServiceMock;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.manage.service.holiday.HolidayUpdater.CONSULTANT_SOURCE_TYPE;

@QuarkusTest
class ReminderServiceTest {

    @Inject
    ReminderService reminderService;

    @BeforeEach
    @Transactional
    void setup() {
        TimeEntry.deleteAll();
        TimeLog.deleteAll();
        org.manage.domain.PendingLink.deleteAll();
        DayInfo.deleteAll();
        Member.deleteAll();
        TelegramBotServiceMock.sentMessages.clear();
    }

    @Test
    @Transactional
    void testSendReminders_WorkingDay() {
        // Create a member who needs a reminder
        Member member = new Member();
        member.login = "remind_me";
        member.firstName = "Remind";
        member.lastName = "Me";
        member.telegramId = 12345L;
        member.persist();

        // 2023-10-23 is Monday (working day)
        LocalDate monday = LocalDate.of(2023, 10, 23);
        
        reminderService.sendReminders(monday);

        assertThat(TelegramBotServiceMock.sentMessages).hasSize(1);
        assertThat(TelegramBotServiceMock.sentMessages.get(0).chatId).isEqualTo(12345L);
    }

    @Test
    @Transactional
    void testSendReminders_NonWorkingDay_Weekend() {
        // Create a member who needs a reminder
        Member member = new Member();
        member.login = "remind_me";
        member.firstName = "Remind";
        member.lastName = "Me";
        member.telegramId = 12345L;
        member.persist();

        // 2023-10-28 is Saturday (weekend)
        LocalDate saturday = LocalDate.of(2023, 10, 28);
        
        reminderService.sendReminders(saturday);

        assertThat(TelegramBotServiceMock.sentMessages).isEmpty();
    }

    @Test
    @Transactional
    void testSendReminders_NonWorkingDay_Holiday() {
        // Create a member who needs a reminder
        Member member = new Member();
        member.login = "remind_me";
        member.firstName = "Remind";
        member.lastName = "Me";
        member.telegramId = 12345L;
        member.persist();

        // 2023-10-24 is Tuesday
        LocalDate tuesday = LocalDate.of(2023, 10, 24);
        
        // Mark as holiday
        DayInfo dayInfo = new DayInfo();
        dayInfo.day = tuesday;
        dayInfo.dayType = DayType.HOLIDAY;
        dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
        dayInfo.persist();
        
        reminderService.sendReminders(tuesday);

        assertThat(TelegramBotServiceMock.sentMessages).isEmpty();
    }

    @Test
    @Transactional
    void testSendReminders_WorkingDay_PreHoliday() {
        // Create a member who needs a reminder
        Member member = new Member();
        member.login = "remind_me";
        member.firstName = "Remind";
        member.lastName = "Me";
        member.telegramId = 12345L;
        member.persist();

        // 2023-10-25 is Wednesday
        LocalDate wednesday = LocalDate.of(2023, 10, 25);
        
        // Mark as pre-holiday
        DayInfo dayInfo = new DayInfo();
        dayInfo.day = wednesday;
        dayInfo.dayType = DayType.PRE_HOLIDAY;
        dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
        dayInfo.persist();
        
        reminderService.sendReminders(wednesday);

        assertThat(TelegramBotServiceMock.sentMessages).hasSize(1);
    }
}

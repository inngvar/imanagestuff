package org.manage.service.common;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.manage.domain.Member;
import org.manage.domain.TimeEntry;
import org.manage.service.telegram.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    @Inject
    TelegramBotService telegramBotService;

    @Inject
    org.manage.service.holiday.HolidayService holidayService;

    @ConfigProperty(name = "reminder.message")
    String reminderMessage;

    @Scheduled(cron = "{reminder.cron}")
    public void sendReminders() {
        sendReminders(LocalDate.now());
    }

    public void sendReminders(LocalDate date) {
        log.info("Running daily reminders task for {}", date);

        if (!holidayService.isWorkingDay(date)) {
            log.info("Today ({}) is a non-working day. Skipping reminders.", date);
            return;
        }

        List<Member> membersToRemind = getMembersToRemind(date);
        log.info("Found {} members to remind", membersToRemind.size());

        for (Member member : membersToRemind) {
            log.info("Sending reminder to member {} (telegramId: {})", member.login, member.telegramId);
            telegramBotService.sendMsg(member.telegramId, reminderMessage);
        }
    }

    @Transactional
    public List<Member> getMembersToRemind(LocalDate date) {
        return Member.findAllWhoNeedReminder(date);
    }
}

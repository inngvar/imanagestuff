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

    @ConfigProperty(name = "reminder.message")
    String reminderMessage;

    @Scheduled(cron = "{reminder.cron}")
    public void sendReminders() {
        log.info("Running daily reminders task");
        LocalDate today = LocalDate.now();

        List<Member> membersToRemind = getMembersToRemind(today);
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

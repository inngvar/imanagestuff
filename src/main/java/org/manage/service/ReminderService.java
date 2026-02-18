package org.manage.service;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.manage.client.TelegramBotClient;
import org.manage.client.dto.SendMessageRequest;
import org.manage.domain.Member;
import org.manage.domain.TimeEntry;
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
    @RestClient
    TelegramBotClient telegramBotClient;

    @ConfigProperty(name = "telegram.bot.token")
    String token;

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
            try {
                telegramBotClient.sendMessage(token, new SendMessageRequest(member.telegramId, reminderMessage));
            } catch (Exception e) {
                log.error("Failed to send reminder to telegramId {}: {}", member.telegramId, e.getMessage());
            }
        }
    }

    @Transactional
    public List<Member> getMembersToRemind(LocalDate date) {
        return Member.findAllWhoNeedReminder(date);
    }
}

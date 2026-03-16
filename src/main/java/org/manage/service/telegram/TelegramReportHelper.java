package org.manage.service.telegram;

import org.manage.domain.Member;
import org.manage.service.dto.TimeEntryDTO;
import org.manage.service.time.TimeEntryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class TelegramReportHelper {

    @Inject
    TimeEntryService timeEntryService;

    @Inject
    TelegramBotService telegramBotService;

    public void handlePeriodEntries(Long chatId, Long telegramId, LocalDate startDate, LocalDate endDate, String periodName) {
        Member member = Member.findByTelegramId(telegramId)
                .orElseThrow();

        var entries = timeEntryService.findByMemberAndDateAndProject(member.id, startDate, endDate, member.defaultProject != null ? member.defaultProject.id : null);

        if (entries.isEmpty()) {
            telegramBotService.sendMsg(chatId, "За " + periodName + " записей нет.");
            return;
        }

        if (startDate.equals(endDate)) {
             StringBuilder sb = new StringBuilder("Записи за " + periodName + ":\n");
             for (TimeEntryDTO entry : entries) {
                 sb.append("- ").append(formatDuration(entry.duration)).append(": ").append(entry.shortDescription).append("\n");
             }
             telegramBotService.sendMsg(chatId, sb.toString());
        } else {
             java.time.Duration total = java.time.Duration.ZERO;
             for (TimeEntryDTO entry : entries) {
                 total = total.plus(entry.duration);
             }
             StringBuilder sb = new StringBuilder("Сводка за " + periodName + ":\n");
             sb.append("Всего записей: ").append(entries.size()).append("\n");
             sb.append("Затрачено времени: ").append(formatDuration(total)).append("\n");
             telegramBotService.sendMsg(chatId, sb.toString());
        }
    }

    public String formatDuration(java.time.Duration duration) {
        long hours = duration.toHours();
        int minutes = duration.toMinutesPart();
        return String.format("%d:%02d", hours, minutes);
    }
}

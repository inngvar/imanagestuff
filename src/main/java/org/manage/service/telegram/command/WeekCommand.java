package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramReportHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class WeekCommand implements TelegramCommand {

    @Inject
    TelegramReportHelper reportHelper;

    @Override
    public String commandName() {
        return "/week";
    }

    @Override
    public boolean existenceLinkedAccount() {
        return true;
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        reportHelper.handlePeriodEntries(chatId, telegramId, startOfWeek, endOfWeek, "текущую неделю");
    }
}

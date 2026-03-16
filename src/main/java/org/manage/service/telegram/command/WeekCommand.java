package org.manage.service.telegram.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class WeekCommand implements TelegramCommand {

    @Inject
    TelegramReportHelper reportHelper;

    @Override
    public boolean canHandle(String command) {
        return "/week".equals(command);
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        reportHelper.handlePeriodEntries(chatId, telegramId, startOfWeek, endOfWeek, "текущую неделю");
    }
}

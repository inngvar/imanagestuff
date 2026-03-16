package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramBotService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class DateCommand implements TelegramCommand {

    @Inject
    TelegramReportHelper reportHelper;

    @Inject
    TelegramBotService telegramBotService;

    @Override
    public boolean canHandle(String command) {
        return "/date".equals(command);
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        String[] parts = text.split("\\s+");
        if (parts.length < 2) {
            telegramBotService.sendMsg(chatId, "Укажите дату в формате ДД.ММ или ДД.ММ.ГГГГ, например: /date 15.03");
            return;
        }

        LocalDate targetDate;
        try {
            String dateText = parts[1];
            String[] dp = dateText.split("\\.");
            int day = Integer.parseInt(dp[0]);
            int month = Integer.parseInt(dp[1]);
            int year = dp.length == 3 ? Integer.parseInt(dp[2]) : LocalDate.now().getYear();
            targetDate = LocalDate.of(year, month, day);
        } catch (Exception e) {
            telegramBotService.sendMsg(chatId, "Неверный формат даты. Используйте ДД.ММ или ДД.ММ.ГГГГ, например: /date 15.03");
            return;
        }

        reportHelper.handlePeriodEntries(chatId, telegramId, targetDate, targetDate, targetDate.toString());
    }
}

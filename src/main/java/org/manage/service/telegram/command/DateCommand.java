package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramBotService;
import org.manage.service.telegram.TelegramReportHelper;
import org.manage.service.util.TelegramMessageParser;

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
    public String commandName() {
        return "/date";
    }

    @Override
    public boolean existenceLinkedAccount() {
        return true;
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        String[] parts = text.split("\\s+");
        if (parts.length < 2) {
            telegramBotService.sendMsg(chatId, "Укажите дату в формате ДД.ММ или ДД.ММ.ГГГГ, например: /date 15.03");
            return;
        }

        LocalDate targetDate = TelegramMessageParser.parseDate(parts[1]);
        if (targetDate == null) {
            telegramBotService.sendMsg(chatId, "Неверный формат даты. Используйте ДД.ММ или ДД.ММ.ГГГГ, например: /date 15.03");
            return;
        }

        reportHelper.handlePeriodEntries(chatId, telegramId, targetDate, targetDate, targetDate.toString());
    }
}

package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramReportHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class TodayCommand implements TelegramCommand {

    @Inject
    TelegramReportHelper reportHelper;

    @Override
    public String commandName() {
        return "/today";
    }

    @Override
    public boolean existenceLinkedAccount() {
        return true;
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        reportHelper.handlePeriodEntries(chatId, telegramId, LocalDate.now(), LocalDate.now(), "сегодня");
    }
}

package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramReportHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class YesterdayCommand implements TelegramCommand {

    @Inject
    TelegramReportHelper reportHelper;

    @Override
    public String commandName() {
        return "/yesterday";
    }

    @Override
    public boolean existenceLinkedAccount() {
        return true;
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        reportHelper.handlePeriodEntries(chatId, telegramId, LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), "вчера");
    }
}

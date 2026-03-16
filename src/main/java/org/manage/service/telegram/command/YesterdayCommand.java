package org.manage.service.telegram.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class YesterdayCommand implements TelegramCommand {

    @Inject
    TelegramReportHelper reportHelper;

    @Override
    public boolean canHandle(String command) {
        return "/yesterday".equals(command);
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        reportHelper.handlePeriodEntries(chatId, telegramId, LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), "вчера");
    }
}

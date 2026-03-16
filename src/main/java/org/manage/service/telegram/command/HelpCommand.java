package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramBotService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HelpCommand implements TelegramCommand {

    @Inject
    TelegramBotService telegramBotService;

    @Override
    public boolean canHandle(String command) {
        return "/help".equals(command);
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        telegramBotService.sendMsg(chatId, "Доступные команды:\n/today - записи за сегодня\n/yesterday - записи за вчера\n/week - сводка за неделю\n/date ДД.ММ - записи за дату\n/help - справка\n\nДля записи времени отправьте сообщение в формате: `длительность описание` (например, `2:00 разработка API` или `вчера 1h баги`) ");
    }
}

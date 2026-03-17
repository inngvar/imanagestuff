package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramBotService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HelpCommand implements TelegramCommand {

    @Inject
    TelegramBotService telegramBotService;

    @Override
    public String commandName() {
        return "/help";
    }

    @Override
    public boolean existenceLinkedAccount() {
        return false;
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        String helpMsg = "Доступные команды:\n" +
                "/today - записи за сегодня\n" +
                "/yesterday - записи за вчера\n" +
                "/week - сводка за неделю\n" +
                "/date ДД.ММ - записи за дату\n" +
                "/help - справка\n\n" +
                "Для записи времени отправьте сообщение. Возможные форматы:\n" +
                "- `длительность описание` (запишет на сегодня)\n" +
                "- `дата длительность описание` (запишет на указанную дату)\n\n" +
                "Длительность можно указывать как:\n" +
                "- `1:30` (1 час 30 минут)\n" +
                "- `1.5h` или `1.5ч` (1.5 часа)\n" +
                "- `90m` или `90м` или просто `90` (90 минут)\n\n" +
                "Дату можно указывать как:\n" +
                "- `вчера` или `позавчера`\n" +
                "- `15.03` (15 марта текущего года)\n" +
                "- `15.03.2023` (с указанием года)\n\n" +
                "Примеры:\n" +
                "`1:30 созвон с командой`\n" +
                "`вчера 1.5h фиксил баги`\n" +
                "`15.03 45м написание тестов`";
        telegramBotService.sendMsg(chatId, helpMsg);
    }
}


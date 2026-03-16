package org.manage.service.telegram;

import org.manage.service.telegram.command.TelegramCommand;
import org.manage.service.telegram.command.TimeEntryCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class TelegramMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageHandler.class);

    @Inject
    Instance<TelegramCommand> commands;

    @Inject
    TimeEntryCommand timeEntryCommand;

    @Transactional
    public void handleMessage(Message message) {
        log.debug("Handling telegram message: {}", message.getText());
        Long chatId = message.getChatId();
        String text = message.getText();
        Long telegramId = message.getFrom().getId();

        if (text == null || text.isBlank()) {
            return;
        }

        String commandName = text.split("\\s+")[0].toLowerCase();

        for (TelegramCommand cmd : commands) {
            // Skip TimeEntryCommand in the loop to use it as an explicit fallback
            if (!(cmd instanceof TimeEntryCommand) && cmd.canHandle(commandName)) {
                cmd.handle(chatId, telegramId, text);
                return;
            }
        }

        // Fallback
        timeEntryCommand.handle(chatId, telegramId, text);
    }
}

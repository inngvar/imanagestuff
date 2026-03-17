package org.manage.service.telegram;

import org.manage.domain.Member;
import org.manage.service.telegram.command.TelegramCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class TelegramMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageHandler.class);

    @Inject
    Map<String, TelegramCommand> telegramCommands;

    @Inject
    TimeEntryHandler timeEntryHandler;

    @Inject
    TelegramBotService telegramBotService;

    @Inject
    TelegramReportHelper reportHelper;

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

        var existLinkedAccount = Member.existsByTelegramId(telegramId);

        var telegramCommand = telegramCommands.get(commandName);
        if (telegramCommand != null) {
            if (telegramCommand.existenceLinkedAccount() && !existLinkedAccount) {
                telegramBotService.sendMsg(chatId, "Ваш Telegram не привязан к аккаунту. Используйте `/start CODE` для привязки.");
            } else {
                telegramCommand.handle(chatId, telegramId, text);
            }
        } else {
            // Если ни одна команда не подошла, расцениваем как попытку создать Time Entry
            if (existLinkedAccount) {
                timeEntryHandler.handle(chatId, telegramId, text);
            } else {
                telegramBotService.sendMsg(chatId, "Ваш Telegram не привязан к аккаунту. Используйте `/start CODE` для привязки.");
            }
        }


    }
}

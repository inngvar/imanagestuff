package org.manage.service.telegram.command;

import org.manage.service.telegram.TelegramBotService;
import org.manage.service.telegram.TelegramLinkService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StartCommand implements TelegramCommand {

    @Inject
    TelegramLinkService telegramLinkService;

    @Inject
    TelegramBotService telegramBotService;

    @Override
    public String commandName() {
        return "/start";
    }

    @Override
    public boolean existenceLinkedAccount() {
        return false;
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
        String[] parts = text.split("\\s+");
        if (parts.length > 1) {
            String code = parts[1];
            boolean linked = telegramLinkService.linkMember(telegramId, code);
            if (linked) {
                telegramBotService.sendMsg(chatId, "Ваш Telegram успешно привязан!");
            } else {
                telegramBotService.sendMsg(chatId, "Неверный или просроченный код привязки.");
            }
        } else {
            telegramBotService.sendMsg(chatId, "Добро пожаловать! Чтобы привязать аккаунт, используйте ссылку из личного кабинета.");
        }
    }
}

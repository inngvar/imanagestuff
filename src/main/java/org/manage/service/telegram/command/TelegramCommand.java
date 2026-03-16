package org.manage.service.telegram.command;

public interface TelegramCommand {
    boolean canHandle(String command);
    void handle(Long chatId, Long telegramId, String text);
}

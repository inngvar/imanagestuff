package org.manage.service.telegram.command;

/**
 * Интерфейс для обработки команд Telegram-бота.
 */
public interface TelegramCommand {

    /**
     * Возвращает название команды, которую обрабатывает данный класс.
     *
     * @return строка с названием команды (например, "/start" или "/help")
     */
    String commandName();

    /**
     * Указывает, требует ли данная команда наличия привязанного аккаунта.
     *
     * @return true, если для выполнения команды требуется авторизация, иначе false
     */
    boolean existenceLinkedAccount();

    /**
     * Выполняет логику команды.
     *
     * @param chatId     идентификатор чата в Telegram
     * @param telegramId идентификатор пользователя в Telegram
     * @param text       полный текст полученного сообщения
     */
    void handle(Long chatId, Long telegramId, String text);
}

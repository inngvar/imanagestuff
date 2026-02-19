package org.manage.service;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class TelegramBotInitializer {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotInitializer.class);

    @Inject
    TelegramBotService telegramBotService;

    void onStart(@Observes StartupEvent ev) {
        if (LaunchMode.current() == LaunchMode.TEST) {
            log.info("Test mode detected. Telegram Bot registration skipped.");
            return;
        }

        log.info("Initializing Telegram Bot...");
        if ("MISSING_TOKEN".equals(telegramBotService.getBotToken())) {
            log.warn("Telegram bot token is not set. Bot registration skipped.");
            return;
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBotService);
            log.info("Telegram Bot registered successfully.");
        } catch (TelegramApiException e) {
            log.error("Failed to register Telegram Bot", e);
        }
    }
}

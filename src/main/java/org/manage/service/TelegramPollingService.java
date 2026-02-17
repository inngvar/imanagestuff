package org.manage.service;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.manage.client.TelegramBotClient;
import org.manage.client.dto.Update;
import org.manage.domain.PollingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TelegramPollingService {

    private static final Logger log = LoggerFactory.getLogger(TelegramPollingService.class);

    @Inject
    @RestClient
    TelegramBotClient telegramBotClient;

    @Inject
    TelegramMessageHandler telegramMessageHandler;

    @ConfigProperty(name = "telegram.bot.token")
    String token;

    @Scheduled(every = "{telegram.polling.interval}")
    @Transactional
    public void poll() {
        log.trace("Polling telegram updates");
        PollingState state = PollingState.findById(1L);
        if (state == null) {
            state = new PollingState();
            state.id = 1L;
            state.lastUpdateId = 0;
            state.persist();
        }

        Long offset = state.lastUpdateId == 0 ? null : (long) state.lastUpdateId + 1;

        try {
            List<Update> updates = telegramBotClient.getUpdates(token, offset, 0);
            for (Update update : updates) {
                if (update.message != null) {
                    telegramMessageHandler.handleMessage(update.message);
                }
                state.lastUpdateId = update.update_id.intValue();
            }
        } catch (Exception e) {
            log.error("Failed to fetch updates from Telegram", e);
        }
    }
}

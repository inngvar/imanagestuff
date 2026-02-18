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

    @ConfigProperty(name = "telegram.polling.timeout", defaultValue = "30")
    Integer timeout;

    @Scheduled(every = "{telegram.polling.interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void poll() {
        log.trace("Polling telegram updates");

        Long offset = getOffset();

        try {
            List<Update> updates = telegramBotClient.getUpdates(token, offset, timeout);
            if (!updates.isEmpty()) {
                processUpdates(updates);
            }
        } catch (Exception e) {
            log.error("Failed to fetch updates from Telegram", e);
        }
    }

    @Transactional
    public Long getOffset() {
        PollingState state = PollingState.findById(1L);
        if (state == null) {
            state = new PollingState();
            state.id = 1L;
            state.persist();
        }
        return state.lastUpdateId == null ? null : state.lastUpdateId + 1;
    }

    @Transactional
    public void processUpdates(List<Update> updates) {
        PollingState state = PollingState.findById(1L);
        for (Update update : updates) {
            if (update.message != null) {
                try {
                    telegramMessageHandler.handleMessage(update.message);
                } catch (Exception e) {
                    log.error("Error handling message update_id={}", update.update_id, e);
                }
            }
            state.lastUpdateId = update.update_id;
        }
    }
}

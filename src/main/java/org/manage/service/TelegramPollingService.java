package org.manage.service;

import com.rometools.utils.Strings;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.manage.client.TelegramBotClient;
import org.manage.client.dto.Update;
import org.manage.domain.PollingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Optional;

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

    @PostConstruct
    void init() {
        if ("MISSING_TOKEN".equals(token)) {
            log.warn("Telegram bot token is not set. Polling will be skipped.");
        }
    }

    @Scheduled(every = "{telegram.polling.interval}", delay = 2,
        concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void poll() {
        // Если токен отсутствует – ничего не делаем
        if ("MISSING_TOKEN".equals(token)) {
            return;
        }

        log.trace("Polling telegram updates");

        try {
            List<Update> updates = telegramBotClient.getUpdates(token, getOffset().orElse(null), timeout);
            if (!updates.isEmpty()) {
                processUpdates(updates);
            }
        } catch (Exception e) {
            log.error("Failed to fetch updates from Telegram", e);
        }
    }

    @Transactional
    public Optional<Long> getOffset() {
        PollingState state = PollingState.findById(1L);
        if (state == null) {
            state = new PollingState();
            state.id = 1L;
            state.persist();
        }
        return Optional.ofNullable(state.lastUpdateId)
            .map(id -> id + 1);
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

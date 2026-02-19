package org.manage.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.client.TelegramBotClient;
import org.manage.client.dto.Chat;
import org.manage.client.dto.Message;
import org.manage.client.dto.Update;
import org.manage.client.dto.User;
import org.manage.domain.PollingState;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TelegramPollingServiceTest {

    @Inject
    TelegramPollingService telegramPollingService;

    @InjectMock
    @RestClient
    TelegramBotClient telegramBotClient;

    @InjectMock
    TelegramMessageHandler telegramMessageHandler;

    @BeforeEach
    @Transactional
    public void setup() {
        PollingState.deleteAll();
    }

    @Test
    @Transactional
    public void testPollInitial() {
        Update update = new Update();
        update.update_id = 100L;
        update.message = new Message();
        update.message.text = "test";
        update.message.chat = new Chat();
        update.message.chat.id = 1L;
        update.message.from = new User();
        update.message.from.id = 1L;

        when(telegramBotClient.getUpdates(anyString(), isNull(), anyInt()))
            .thenReturn(List.of(update));

        telegramPollingService.poll();

        verify(telegramMessageHandler).handleMessage(any());

        PollingState state = PollingState.findById(1L);
        assertThat(state).isNotNull();
        assertThat(state.lastUpdateId).isEqualTo(100);
    }

    @Test
    @Transactional
    public void testPollWithOffset() {
        PollingState state = new PollingState();
        state.id = 1L;
        state.lastUpdateId = 100L;
        state.persist();
        PollingState.flush();

        Update update = new Update();
        update.update_id = 101L;
        update.message = new Message();
        update.message.text = "test";
        update.message.chat = new Chat();
        update.message.chat.id = 1L;
        update.message.from = new User();
        update.message.from.id = 1L;

        when(telegramBotClient.getUpdates(anyString(), eq(101L), anyInt()))
            .thenReturn(List.of(update));

        telegramPollingService.poll();

        verify(telegramMessageHandler).handleMessage(any());

        PollingState updatedState = PollingState.findById(1L);
        assertThat(updatedState.lastUpdateId).isEqualTo(101);
    }
}

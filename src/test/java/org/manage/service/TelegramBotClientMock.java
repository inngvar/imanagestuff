package org.manage.service;

import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.manage.client.TelegramBotClient;
import org.manage.client.dto.SendMessageRequest;
import org.manage.client.dto.Update;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mock
@RestClient
@ApplicationScoped
public class TelegramBotClientMock implements TelegramBotClient {
    public static final List<SendMessageRequest> sentMessages = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<Update> getUpdates(String token, Long offset, Integer timeout) {
        return new ArrayList<>();
    }

    @Override
    public void sendMessage(String token, SendMessageRequest request) {
        sentMessages.add(request);
    }
}

package org.manage.service;

import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@Mock
@ApplicationScoped
public class TelegramBotServiceMock extends TelegramBotService {

    public static List<SentMessage> sentMessages = new ArrayList<>();

    @Override
    public void sendMsg(Long chatId, String text) {
        sentMessages.add(new SentMessage(chatId, text));
    }

    public static class SentMessage {
        public Long chatId;
        public String text;

        public SentMessage(Long chatId, String text) {
            this.chatId = chatId;
            this.text = text;
        }
    }
}

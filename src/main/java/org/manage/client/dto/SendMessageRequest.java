package org.manage.client.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SendMessageRequest {
    public Long chat_id;
    public String text;
    public String parse_mode;

    public SendMessageRequest() {
    }

    public SendMessageRequest(Long chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }
}

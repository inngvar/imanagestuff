package org.manage.client.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Message {
    public Long message_id;
    public User from;
    public Long date;
    public Chat chat;
    public String text;
}

package org.manage.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

@RegisterForReflection
public class TelegramLinkDTO {
    public String link;
    public Instant expiresAt;

    public TelegramLinkDTO() {
    }

    public TelegramLinkDTO(String link, Instant expiresAt) {
        this.link = link;
        this.expiresAt = expiresAt;
    }
}

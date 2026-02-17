package org.manage.service;

import org.manage.domain.Member;
import org.manage.domain.PendingLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class TelegramLinkService {

    private final Logger log = LoggerFactory.getLogger(TelegramLinkService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final Duration CODE_EXPIRATION = Duration.ofMinutes(10);

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public String generateLinkCode(Member member) {
        log.debug("Generating link code for member: {}", member.login);

        // Invalidate previous unused codes for this member
        PendingLink.find("from PendingLink pl where pl.member = ?1 and pl.used = false", member)
            .stream().forEach(pl -> {
                ((PendingLink)pl).used = true;
            });

        String code = generateRandomCode();
        PendingLink pendingLink = new PendingLink();
        pendingLink.member = member;
        pendingLink.code = code;
        pendingLink.expiresAt = Instant.now().plus(CODE_EXPIRATION);
        pendingLink.persist();

        return code;
    }

    @Transactional
    public Optional<Member> confirmLink(String code, Long telegramId) {
        log.debug("Confirming link for code: {} and telegramId: {}", code, telegramId);

        Optional<PendingLink> pendingLinkOpt = PendingLink.find("code = ?1 and used = false and expiresAt > ?2",
                code, Instant.now()).firstResultOptional();

        if (pendingLinkOpt.isPresent()) {
            PendingLink pendingLink = pendingLinkOpt.get();
            Member member = pendingLink.member;
            member.telegramId = telegramId;
            member.persist();

            pendingLink.used = true;
            pendingLink.persist();

            log.info("Member {} successfully linked to telegramId {}", member.login, telegramId);
            return Optional.of(member);
        }

        log.warn("Invalid or expired link code: {}", code);
        return Optional.empty();
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}

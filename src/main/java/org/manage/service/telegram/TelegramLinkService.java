package org.manage.service.telegram;

import org.manage.domain.Member;
import org.manage.domain.PendingLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.manage.security.RandomUtil;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ApplicationScoped
public class TelegramLinkService {

    private final Logger log = LoggerFactory.getLogger(TelegramLinkService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final Duration CODE_EXPIRATION = Duration.ofMinutes(10);

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public PendingLink generateLinkCode(Member member) {
        log.debug("Generating link code for member: {}", member.login);

        // Invalidate previous unused codes for this member
        PendingLink.find("from PendingLink pl where pl.member = ?1 and pl.used = false", member)
            .stream().forEach(pl -> {
                ((PendingLink) pl).used = true;
            });

        String code = generateRandomCode();
        PendingLink pendingLink = new PendingLink();
        pendingLink.member = member;
        pendingLink.code = code;
        pendingLink.expiresAt = Instant.now().plus(CODE_EXPIRATION);
        pendingLink.persist();

        return pendingLink;
    }

    @Transactional
    public boolean linkMember(Long telegramId, String code) {
        Optional<PendingLink> pendingLinkOpt = PendingLink.find("code = ?1 and used = false", code).firstResultOptional();
        if (pendingLinkOpt.isPresent()) {
            PendingLink pendingLink = pendingLinkOpt.get();
            if (pendingLink.expiresAt.isAfter(Instant.now())) {
                Member member = pendingLink.member;
                member.telegramId = telegramId;
                member.persist();

                pendingLink.used = true;
                pendingLink.persist();
                return true;
            }
        }
        return false;
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

    @Transactional
    @Scheduled(every = "{telegram.link.cleanup.interval}", delay = 2)
    public void cleanup() {
        log.debug("Cleaning up expired or used pending links");
        long deletedCount = PendingLink.delete("used = true or expiresAt < ?1", Instant.now());
        if (deletedCount > 0) {
            log.info("Deleted {} expired or used pending links", deletedCount);
        }
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}

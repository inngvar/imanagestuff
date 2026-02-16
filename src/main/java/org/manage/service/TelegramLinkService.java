package org.manage.service;

import org.manage.domain.Member;
import org.manage.domain.PendingLink;
import org.manage.security.RandomUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ApplicationScoped
public class TelegramLinkService {

    @Transactional
    public String generateLinkCode(Member member) {
        // Clear old links
        PendingLink.delete("from PendingLink p where p.member = ?1", member);

        PendingLink pendingLink = new PendingLink();
        pendingLink.member = member;
        pendingLink.code = RandomUtil.generateRandomAlphanumericString();
        pendingLink.createdAt = Instant.now();
        pendingLink.expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);
        pendingLink.used = false;
        pendingLink.persist();

        return pendingLink.code;
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
}

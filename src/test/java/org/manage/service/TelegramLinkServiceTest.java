package org.manage.service;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.manage.domain.Member;
import org.manage.domain.PendingLink;
import org.manage.service.telegram.TelegramLinkService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class TelegramLinkServiceTest {

    @Inject
    TelegramLinkService telegramLinkService;

    @Test
    @Transactional
    void generateAndConfirmLink() {
        // Prepare member
        Member member = new Member();
        member.login = "testuser";
        member.firstName = "Test";
        member.lastName = "User";
        member.persist();

        // Generate code
        String code = telegramLinkService.generateLinkCode(member).code;
        assertThat(code).isNotNull().hasSize(8);

        // Check PendingLink created
        PendingLink pendingLink = PendingLink.find("code", code).firstResult();
        assertThat(pendingLink).isNotNull();
        assertThat(pendingLink.member.id).isEqualTo(member.id);
        assertThat(pendingLink.used).isFalse();
        assertThat(pendingLink.expiresAt).isAfter(Instant.now());

        // Confirm link
        Long telegramId = 123456789L;
        Optional<Member> confirmedMemberOpt = telegramLinkService.confirmLink(code, telegramId);

        assertThat(confirmedMemberOpt).isPresent();
        Member confirmedMember = confirmedMemberOpt.get();
        assertThat(confirmedMember.id).isEqualTo(member.id);
        assertThat(confirmedMember.telegramId).isEqualTo(telegramId);

        // Check PendingLink marked as used
        pendingLink = PendingLink.findById(pendingLink.id);
        assertThat(pendingLink.used).isTrue();

        // Try to use same code again
        Optional<Member> secondConfirmation = telegramLinkService.confirmLink(code, 987654321L);
        assertThat(secondConfirmation).isEmpty();
    }

    @Test
    @Transactional
    void confirmLink_Expired() {
        Member member = new Member();
        member.login = "expireduser";
        member.firstName = "Expired";
        member.lastName = "User";
        member.persist();

        String code = "EXPIRED1";
        PendingLink pendingLink = new PendingLink();
        pendingLink.member = member;
        pendingLink.code = code;
        pendingLink.createdAt = Instant.now().minusSeconds(3600);
        pendingLink.expiresAt = Instant.now().minusSeconds(1800);
        pendingLink.used = false;
        pendingLink.persist();

        Optional<Member> confirmedMemberOpt = telegramLinkService.confirmLink(code, 123L);
        assertThat(confirmedMemberOpt).isEmpty();
    }

    @Test
    @Transactional
    void generateLinkCode_InvalidatesPrevious() {
        Member member = new Member();
        member.login = "multiuser";
        member.firstName = "Multi";
        member.lastName = "User";
        member.persist();

        String code1 = telegramLinkService.generateLinkCode(member).code;
        PendingLink pl1 = PendingLink.find("code", code1).firstResult();
        assertThat(pl1.used).isFalse();

        String code2 = telegramLinkService.generateLinkCode(member).code;
        pl1 = PendingLink.findById(pl1.id);
        assertThat(pl1.used).isTrue();

        PendingLink pl2 = PendingLink.find("code", code2).firstResult();
        assertThat(pl2.used).isFalse();
    }
}

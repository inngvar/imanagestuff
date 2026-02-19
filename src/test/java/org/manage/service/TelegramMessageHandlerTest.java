package org.manage.service;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manage.domain.Member;
import org.manage.domain.PendingLink;
import org.manage.domain.Project;
import org.manage.domain.TimeEntry;
import org.manage.service.dto.TimeEntryDTO;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@QuarkusTest
public class TelegramMessageHandlerTest {

    @Inject
    TelegramMessageHandler telegramMessageHandler;

    @Inject
    TimeEntryService timeEntryService;

    @Inject
    TelegramLinkService telegramLinkService;

    @BeforeEach
    @Transactional
    public void setup() {
        TimeEntry.deleteAll();
        PendingLink.deleteAll();
        Member.deleteAll();
        Project.deleteAll();
        TelegramBotServiceMock.sentMessages.clear();
    }

    @Test
    public void testHandleStartWithoutCode() {
        Message message = createMessage("/start", 123L);
        telegramMessageHandler.handleMessage(message);

        assertThat(TelegramBotServiceMock.sentMessages).hasSize(1);
        assertThat(TelegramBotServiceMock.sentMessages.get(0).text).contains("привязать аккаунт");
    }

    @Test
    @Transactional
    public void testHandleStartWithCode() {
        Project project = new Project();
        project.name = "Test Project";
        project.persist();

        Member member = new Member();
        member.login = "testuser";
        member.firstName = "Test";
        member.lastName = "User";
        member.defaultProject = project;
        member.persist();

        String code = telegramLinkService.generateLinkCode(member);

        Message message = createMessage("/start " + code, 123L);
        telegramMessageHandler.handleMessage(message);

        assertThat(TelegramBotServiceMock.sentMessages).hasSize(1);
        assertThat(TelegramBotServiceMock.sentMessages.get(0).text).contains("успешно привязан");

        Member updatedMember = Member.findById(member.id);
        assertThat(updatedMember.telegramId).isEqualTo(123L);
    }

    @Test
    @Transactional
    public void testHandleTimeEntry() {
        Project project = new Project();
        project.name = "Test Project";
        project.persist();

        Member member = new Member();
        member.login = "testuser";
        member.firstName = "Test";
        member.lastName = "User";
        member.defaultProject = project;
        member.telegramId = 123L;
        member.persist();

        Message message = createMessage("2:30 coding", 123L);
        telegramMessageHandler.handleMessage(message);

        assertThat(TelegramBotServiceMock.sentMessages).hasSize(1);
        assertThat(TelegramBotServiceMock.sentMessages.get(0).text).contains("Запись сохранена");

        List<TimeEntryDTO> entries = timeEntryService.findAll(io.quarkus.panache.common.Page.of(0, 10)).content;
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).duration).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        assertThat(entries.get(0).description).isEqualTo("coding");
    }

    private Message createMessage(String text, Long telegramId) {
        Message message = Mockito.mock(Message.class);
        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(456L);
        
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(telegramId);
        when(message.getFrom()).thenReturn(user);
        
        return message;
    }
}

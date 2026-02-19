package org.manage.service;

import org.manage.domain.Member;
import org.manage.service.dto.TimeEntryDTO;
import org.manage.service.util.TelegramMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class TelegramMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageHandler.class);

    @Inject
    TimeEntryService timeEntryService;

    @Inject
    TelegramLinkService telegramLinkService;

    @Inject
    TelegramBotService telegramBotService;

    @Transactional
    public void handleMessage(Message message) {
        log.debug("Handling telegram message: {}", message.getText());
        Long chatId = message.getChatId();
        String text = message.getText();
        Long telegramId = message.getFrom().getId();

        if (text == null) {
            return;
        }

        if (text.startsWith("/start")) {
            handleStart(chatId, telegramId, text);
        } else if (text.startsWith("/today")) {
            handleToday(chatId, telegramId);
        } else if (text.startsWith("/help")) {
            handleHelp(chatId);
        } else {
            handleTimeEntry(chatId, telegramId, text);
        }
    }

    private void handleStart(Long chatId, Long telegramId, String text) {
        String[] parts = text.split("\\s+");
        if (parts.length > 1) {
            String code = parts[1];
            boolean linked = telegramLinkService.linkMember(telegramId, code);
            if (linked) {
                telegramBotService.sendMsg(chatId, "Ваш Telegram успешно привязан!");
            } else {
                telegramBotService.sendMsg(chatId, "Неверный или просроченный код привязки.");
            }
        } else {
            telegramBotService.sendMsg(chatId, "Добро пожаловать! Чтобы привязать аккаунт, используйте ссылку из личного кабинета.");
        }
    }

    private void handleToday(Long chatId, Long telegramId) {
        Optional<Member> memberOpt = Member.findByTelegramId(telegramId);
        if (memberOpt.isEmpty()) {
            telegramBotService.sendMsg(chatId, "Ваш Telegram не привязан к аккаунту.");
            return;
        }

        Member member = memberOpt.get();
        LocalDate today = LocalDate.now();
        var entries = timeEntryService.findByMemberAndDateAndProject(member.id, today, today, member.defaultProject != null ? member.defaultProject.id : null);

        if (entries.isEmpty()) {
            telegramBotService.sendMsg(chatId, "За сегодня записей нет.");
        } else {
            StringBuilder sb = new StringBuilder("Записи за сегодня:\n");
            for (TimeEntryDTO entry : entries) {
                sb.append("- ").append(formatDuration(entry.duration)).append(": ").append(entry.description).append("\n");
            }
            telegramBotService.sendMsg(chatId, sb.toString());
        }
    }

    private String formatDuration(java.time.Duration duration) {
        long hours = duration.toHours();
        int minutes = duration.toMinutesPart();
        return String.format("%d:%02d", hours, minutes);
    }

    private void handleHelp(Long chatId) {
        telegramBotService.sendMsg(chatId, "Доступные команды:\n/today - записи за сегодня\n/help - справка\n\nДля записи времени отправьте сообщение в формате: `длительность описание` (например, `2:00 разработка API`) ");
    }

    private void handleTimeEntry(Long chatId, Long telegramId, String text) {
        Optional<Member> memberOpt = Member.findByTelegramId(telegramId);
        if (memberOpt.isEmpty()) {
            telegramBotService.sendMsg(chatId, "Ваш Telegram не привязан к аккаунту. Используйте `/start CODE` для привязки.");
            return;
        }

        Member member = memberOpt.get();
        if (member.defaultProject == null) {
            telegramBotService.sendMsg(chatId, "У вас не выбран проект по умолчанию. Пожалуйста, установите его в личном кабинете.");
            return;
        }

        TelegramMessageParser.ParserResult result = TelegramMessageParser.parseMessage(text);
        if (result == null) {
            telegramBotService.sendMsg(chatId, "Не удалось распознать формат. Используйте: `длительность описание` (например, `2:00 разработка API`)");
            return;
        }

        TimeEntryDTO timeEntryDTO = new TimeEntryDTO();
        timeEntryDTO.duration = result.getDuration();
        timeEntryDTO.description = result.getDescription();
        timeEntryDTO.date = LocalDate.now();
        timeEntryDTO.memberId = member.id;
        timeEntryDTO.projectId = member.defaultProject.id;

        timeEntryService.persist(timeEntryDTO);
        telegramBotService.sendMsg(chatId, "Запись сохранена: " + formatDuration(result.getDuration()) + " - " + result.getDescription());
    }
}

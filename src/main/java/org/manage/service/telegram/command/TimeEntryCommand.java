package org.manage.service.telegram.command;

import org.manage.domain.Member;
import org.manage.service.dto.TimeEntryDTO;
import org.manage.service.time.TimeEntryService;
import org.manage.service.util.TelegramMessageParser;
import org.manage.service.telegram.TelegramBotService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class TimeEntryCommand implements TelegramCommand {

    @Inject
    TimeEntryService timeEntryService;

    @Inject
    TelegramBotService telegramBotService;

    @Inject
    TelegramReportHelper reportHelper;

    @Override
    public boolean canHandle(String command) {
        return true; 
    }

    @Override
    public void handle(Long chatId, Long telegramId, String text) {
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
        String description = result.getDescription();
        timeEntryDTO.shortDescription = description.length() > 256
            ? description.substring(0, 253) + "..."
            : description;
        timeEntryDTO.date = result.getDate() != null ? result.getDate() : LocalDate.now();
        timeEntryDTO.memberId = member.id;
        timeEntryDTO.projectId = member.defaultProject.id;

        timeEntryService.persist(timeEntryDTO);
        telegramBotService.sendMsg(chatId, "Запись сохранена (" + timeEntryDTO.date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "): " + reportHelper.formatDuration(result.getDuration()) + " - " + result.getDescription());
    }
}

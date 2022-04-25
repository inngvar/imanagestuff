package org.manage.service;

import org.manage.config.LocalDateProvider;
import org.manage.domain.Member;
import org.manage.domain.TimeEntry;
import org.manage.service.dto.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportService {

    public static final String SUBJECT_DAY_REPORT_TEMPLATE = "Отчёт по проекту %s за %s";

    public static final String SUBJECT_PERIOD_REPORT_TEMPLATE = "Отчёт по проекту %s за период с %s по %s";

    public static final String SUBJECT_DAY_TIME_LOG_REPORT_TEMPLATE = "Отчёт по отметке времени за %s";

    public static final String SUBJECT_PERIOD_TIME_LOG_REPORT_TEMPLATE = "Отчёт по отметке времени за период с %s по %s";

    @Inject
    public ProjectService projectService;

    @Inject
    public MemberService memberService;

    @Inject
    public TimeEntryService timeEntryService;

    @Inject
    public TimeLogService timeLogService;

    public DayReportDTO generateReport(final Long projectId, final LocalDate fromDate, final LocalDate toDate) {
        final ProjectDTO projectDto = projectService.findOne(projectId).orElseThrow();
        final List<MemberReportInfoDTO> membersReports = memberService
            .findAllByProject(projectDto)
            .map(m -> toMemberReportInfoDTO(m, fromDate, toDate, projectId))
            .collect(Collectors.toList());
        DayReportDTO dayReportDTO = new DayReportDTO();
        dayReportDTO.fromDate = fromDate;
        dayReportDTO.toDate = toDate;
        dayReportDTO.membersReports = membersReports;
        dayReportDTO.project = projectDto;
        dayReportDTO.totalHours = dayReportDTO.membersReports.stream().mapToDouble(r -> r.totalHours).sum();
        dayReportDTO.subject = generateSubject(projectDto, fromDate, toDate);
        return dayReportDTO;
    }

    public TimeLogReportDTO generateTimeLogReport(final LocalDate fromDate, final LocalDate toDate) {
        List<TimeLogDTO> logDTOS = timeLogService.findByDateBetween(fromDate, toDate);

        Map<Long, List<TimeLogDTO>> groupByMembers = logDTOS.stream()
            .filter(dto -> dto.checkIn != null && dto.checkOut != null)
            .collect(Collectors.groupingBy(d -> d.memberId));

        TimeLogReportDTO reportDTO = new TimeLogReportDTO();

        for (Map.Entry<Long, List<TimeLogDTO>> entry : groupByMembers.entrySet()) {

            MemberTimeLogReportInfoDTO memDTO = new MemberTimeLogReportInfoDTO();
            memDTO.member = memberService.findOne(entry.getKey()).orElse(null);
            memDTO.entries = entry.getValue();

            double memTotalSeconds = 0;
            for (TimeLogDTO dto : entry.getValue()) {
                memTotalSeconds += Duration.between(dto.checkIn, dto.checkOut).getSeconds();
            }
            memDTO.totalHours = memTotalSeconds / 60.0 / 60.0;

            reportDTO.membersReports.add(memDTO);
        }

        reportDTO.totalHours = reportDTO.membersReports.stream().mapToDouble(r -> r.totalHours).sum();
        reportDTO.subject = generateTimeLogSubject(fromDate, toDate);

        return reportDTO;
    }

    private String generateTimeLogSubject(LocalDate fromDate, LocalDate toDate) {
        return fromDate.equals(toDate) ?
            String.format(SUBJECT_DAY_TIME_LOG_REPORT_TEMPLATE, LocalDateProvider.formatDate(fromDate)) :
            String.format(SUBJECT_PERIOD_TIME_LOG_REPORT_TEMPLATE, fromDate, toDate);
    }

    private String generateSubject(ProjectDTO projectDTO, LocalDate fromDate, LocalDate toDate) {
        return fromDate.equals(toDate) ?
            String.format(SUBJECT_DAY_REPORT_TEMPLATE, projectDTO.name, LocalDateProvider.formatDate(fromDate)) :
            String.format(SUBJECT_PERIOD_REPORT_TEMPLATE, projectDTO.name, fromDate, toDate);
    }

    private MemberReportInfoDTO toMemberReportInfoDTO(MemberDTO m, LocalDate fromDate, LocalDate toDate, Long projectId) {
        MemberReportInfoDTO dto = new MemberReportInfoDTO();
        dto.member = m;
        dto.entries = timeEntryService.findByMemberAndDateAndProject(m.id, fromDate, toDate, projectId);
        double totalSeconds = ((Long) dto.entries.stream().mapToLong(e -> e.duration.toSeconds()).sum()).doubleValue();
        dto.totalHours = totalSeconds / 60.0 / 60.0;
        return dto;
    }

    /**
     * returns a report of registered time and corresponding projects for period fromDate to toDate for member.
     * Reports for days in which daily goal of 8h was reached are excluded.
     * @param fromDate - inclusive
     * @param toDate - exclusive
     */
    public Optional<List<DayRegisteredTimeDTO>> getRegisteredTimeReport(final String login, final LocalDate fromDate, final LocalDate toDate) {
        Member member = Member.findByLogin(login).orElseThrow();
        List<TimeEntry> timeEntries = TimeEntry.getAllByDateBetweenAndMember(fromDate, toDate.minusDays(1), member);

        List<DayRegisteredTimeDTO> timeReport = new ArrayList<>();
        for (LocalDate date = LocalDate.from(fromDate);
             date.isBefore(toDate);
             date = date.plusDays(1)
        ){
            final LocalDate finalDate = LocalDate.from(date);
            DayRegisteredTimeDTO dayRegisteredTimeDTO = new DayRegisteredTimeDTO();
            dayRegisteredTimeDTO.date = date;
            timeEntries.stream().filter(entry -> entry.date.isEqual(finalDate))
                .forEach(entry -> dayRegisteredTimeDTO.addProjectDuration(projectService.toDTO(entry.project), entry.duration));

            if (dayRegisteredTimeDTO.unregisteredDuration().toMinutes() > 0) {
                timeReport.add(dayRegisteredTimeDTO);
            }
        }
        return Optional.of(timeReport);
    }

}

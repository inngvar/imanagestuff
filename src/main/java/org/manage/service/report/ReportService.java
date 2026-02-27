package org.manage.service.report;

import org.manage.config.LocalDateProvider;
import org.manage.domain.DayInfo;
import org.manage.domain.Member;
import org.manage.domain.Project;
import org.manage.domain.TimeEntry;
import org.manage.service.dto.*;
import org.manage.service.mapper.ProjectMapper;
import org.manage.service.member.MemberService;
import org.manage.service.project.ProjectService;
import org.manage.service.time.TimeEntryService;
import org.manage.service.time.TimeLogService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotAuthorizedException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.manage.service.holiday.HolidayUpdater.CONSULTANT_SOURCE_TYPE;

@ApplicationScoped
public class ReportService {

    public static final Duration WORKDAY_MINUTES_TOTAL = Duration.ofHours(8);

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

    @Inject
    ProjectMapper projectMapper;

    @Transactional
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
     *
     * @param fromDate  inclusive
     * @param daysCount count of days in report
     */
    @Transactional
    public List<DayRegisteredTimeDTO> getRegisteredTimeReport(final String login, final LocalDate fromDate, Integer daysCount) {
        Member member = Member.findByLogin(login).orElseThrow(() -> new NotAuthorizedException("You are not logged in"));
        List<TimeEntry> timeEntries = TimeEntry.getAllByDateBetweenAndMember(fromDate, fromDate.plusDays(daysCount), member);
        return new ReportBuilder(14, fromDate).addTimeEntries(timeEntries).build();
    }

    public class ReportBuilder {

        private final int daysCount;
        private final LocalDate startDate;
        private Map<LocalDate, List<TimeEntry>> entries;

        public ReportBuilder(int daysCount, LocalDate startDate) {
            this.daysCount = daysCount;
            this.startDate = startDate;
        }

        ReportBuilder addTimeEntries(List<TimeEntry> entries) {
            this.entries = entries.stream().collect(Collectors.groupingBy(f -> f.date));
            return this;
        }

        public List<DayRegisteredTimeDTO> build() {
            LocalDate currentDate = startDate;
            List<DayRegisteredTimeDTO> report = new ArrayList<>(daysCount);
            for (int i = 0; i < daysCount; i++) {
                DayRegisteredTimeDTO dayReport = new DayRegisteredTimeDTO();
                dayReport.date = currentDate;
                dayReport.holiday = isHoliday(currentDate);
                if (dayReport.holiday) {
                    dayReport.unregisteredDuration = 0L;
                } else {
                    dayReport.unregisteredDuration = WORKDAY_MINUTES_TOTAL.toMinutes();
                }
                currentDate = currentDate.plusDays(1);
                report.add(dayReport);
                if (!entries.containsKey(dayReport.date)) {
                    continue;
                }
                dayReport.totalDuration = entries.get(dayReport.date).stream().collect(Collectors.summarizingLong(this::getMinutes)).getSum();
                final Map<Project, List<TimeEntry>> entriesByProject = entries.get(dayReport.date).stream().collect(Collectors.groupingBy(f -> f.project));
                for (var pr : entriesByProject.keySet()) {
                    ProjectDuration projectDuration = new ProjectDuration();
                    projectDuration.project = projectMapper.toDto(pr);
                    projectDuration.duration = entriesByProject.get(pr).stream().collect(Collectors.summarizingLong(this::getMinutes)).getSum();
                    dayReport.projectDurations.add(projectDuration);
                    dayReport.unregisteredDuration = dayReport.unregisteredDuration - projectDuration.duration;
                }
            }
            Collections.reverse(report);
            return report;
        }

        private boolean isHoliday(LocalDate currentDate) {
            return DayInfo.getByDate(currentDate, CONSULTANT_SOURCE_TYPE)
                .map(f -> true)
                .orElse(currentDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }

        private long getMinutes(TimeEntry f) {
            return f.duration.get(ChronoUnit.SECONDS) / 60;
        }
    }
}

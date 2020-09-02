package org.manage.service;

import org.manage.config.LocalDateProvider;
import org.manage.service.dto.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportService {

    @Inject
    public ProjectService projectService;

    @Inject
    public MemberService memberService;

    @Inject
    public TimeEntryService timeEntryService;

    public DayReportDTO generateDayReport(final Long projectId, final LocalDate reportDate) {
        final ProjectDTO projectDto = projectService.findOne(projectId).orElseThrow();
        final List<MemberReportInfoDTO> membersReports = memberService
            .findAllByProject(projectDto)
            .map(m -> toMemberReportInfoDTO(m, reportDate, projectId))
            .collect(Collectors.toList());
        DayReportDTO dayReportDTO = new DayReportDTO();
        dayReportDTO.date = reportDate;
        dayReportDTO.membersReports = membersReports;
        dayReportDTO.project = projectDto;
        dayReportDTO.totalHours = dayReportDTO.membersReports.stream().collect(Collectors.summingDouble(r -> r.totalHours));
        dayReportDTO.subject = "Отчёт по работе над проектом " + dayReportDTO.project.name + " за " + LocalDateProvider.dateFormatter.format(reportDate);
        return dayReportDTO;
    }

    private MemberReportInfoDTO toMemberReportInfoDTO(MemberDTO m, LocalDate reportDate, Long projectId) {
        MemberReportInfoDTO dto = new MemberReportInfoDTO();
        dto.member = m;
        dto.entries = timeEntryService.findByMemberAndDateAndProject(m.id, reportDate, projectId);
        Double totalSeconds = dto.entries.stream().collect(Collectors.summingLong(e -> e.duration.toSeconds())).doubleValue();
        dto.totalHours = totalSeconds / 60.0 / 60.0;
        return dto;
    }

}

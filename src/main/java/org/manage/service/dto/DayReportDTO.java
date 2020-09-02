package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
public class DayReportDTO {

    public ProjectDTO project;

    public LocalDate date;

    public List<MemberReportInfoDTO> membersReports = Lists.newArrayList();

    public Long totalHours;

    public String subject;
}


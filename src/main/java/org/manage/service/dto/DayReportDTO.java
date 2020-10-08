package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
public class DayReportDTO {

    public ProjectDTO project;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate date;

    public List<MemberReportInfoDTO> membersReports = Lists.newArrayList();

    public Double totalHours;

    public String subject;

}


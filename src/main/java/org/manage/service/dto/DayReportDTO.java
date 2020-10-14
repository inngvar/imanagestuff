package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNumberFormat;
import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
public class DayReportDTO {

    public ProjectDTO project;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate fromDate;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate toDate;

    public List<MemberReportInfoDTO> membersReports = Lists.newArrayList();

    public Double totalHours;

    public String subject;

}


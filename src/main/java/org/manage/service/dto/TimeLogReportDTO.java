package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDate;
import java.util.List;

@RegisterForReflection
public class TimeLogReportDTO {

    public String subject;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate fromDate;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate toDate;

    public List<MemberTimeLogReportInfoDTO> membersReports = Lists.newArrayList();

    public Double totalHours;
}

package org.manage.service.dto;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReportSingleDayRequestModel {
    @NotNull
    public Long projectId;
    @NotNull
    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate dateFrom;
}

package org.manage.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RegisterForReflection
public class ReportRequestModel {

    @NotNull
    public Long projectId;

    @NotNull
    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate fromtDate;

    @NotNull
    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate toDate;

}

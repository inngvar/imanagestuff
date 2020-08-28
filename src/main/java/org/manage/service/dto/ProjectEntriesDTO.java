package org.manage.service.dto;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ProjectEntriesDTO {

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate  reportDate;

    public ProjectDTO project;

    public List<TimeEntryDTO> timeEntries;

}

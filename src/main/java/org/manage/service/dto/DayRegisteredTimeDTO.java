package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.manage.config.Constants;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RegisterForReflection
public class DayRegisteredTimeDTO implements Serializable {

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate date;

    public Long totalDuration = 0L;

    public final List<ProjectDuration> projectDurations = Lists.newArrayList();

    public Long unregisteredDuration = 0L;

    public boolean holiday;

}

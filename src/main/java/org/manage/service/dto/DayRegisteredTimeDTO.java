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
    public static final Duration WORKDAY_MINUTES_TOTAL = Duration.ofHours(8);

    @NotNull
    @JsonbDateFormat(value = Constants.LOCAL_DATE_FORMAT)
    public LocalDate date;

    public Duration totalDuration = Duration.ZERO;

    public final List<ProjectDuration> projectDurations = Lists.newArrayList();

    public void addProjectDuration(ProjectDTO project, Duration duration) {
        this.totalDuration = duration.plus(totalDuration);
        ProjectDuration pd = projectDurations.stream()
            .filter(el -> Objects.equals(el.project.id, project.id))
            .findAny().orElse(null);
        if(pd != null) {
            pd.duration = pd.duration.plus(duration);
        } else {
            this.projectDurations.add(buildProjectDuration(project, duration));
        }
    }

    public Duration unregisteredDuration() {
        return WORKDAY_MINUTES_TOTAL.minus(totalDuration);
    }

    private ProjectDuration buildProjectDuration(ProjectDTO project, Duration duration) {
        ProjectDuration pd = new ProjectDuration();
        pd.project = project;
        pd.duration = duration;
        return pd;
    }

}

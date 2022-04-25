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

@RegisterForReflection
public class DayRegisteredTimeDTO implements Serializable {
    public static Duration WORKDAY_MINUTES_TOTAL = Duration.ofHours(8);

    @NotNull
    @JsonbDateFormat(value = Constants.LOCAL_DATE_FORMAT)
    public LocalDate date;
    public Duration totalDuration = Duration.ZERO;
    public List<ProjectDuration> projectDurations = Lists.newArrayList();

    public void addProjectDuration(ProjectDTO project, Duration duration) {
        this.projectDurations.add(buildProjectDuration(project,duration));
        this.totalDuration = duration.plus(totalDuration);
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

    public static class ProjectDuration {
        public ProjectDTO project;
        public Duration duration;
    }
}

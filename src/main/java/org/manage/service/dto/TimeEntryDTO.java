package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import java.time.Duration;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.manage.domain.TimeEntry} entity.
 */
@RegisterForReflection
public class TimeEntryDTO implements Serializable {

    public Long id;

    @NotNull
    public Duration duration;

    @NotNull
    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate date;

    @Size(max = 256)
    public String shotDescription;

    @Size(max = 4000)
    public String description;

    public Long memberId;
    public String memberLogin;
    public Long projectId;
    public String projectName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeEntryDTO)) {
            return false;
        }

        return id != null && id.equals(((TimeEntryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeEntryDTO{" +
            "id=" + id +
            ", duration='" + duration + "'" +
            ", date='" + date + "'" +
            ", shotDescription='" + shotDescription + "'" +
            ", description='" + description + "'" +
            ", memberId=" + memberId +
            ", memberLogin='" + memberLogin + "'" +
            ", projectId=" + projectId +
            ", projectName='" + projectName + "'" +
            "}";
    }
}

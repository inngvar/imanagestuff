package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import org.manage.config.Constants;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.manage.domain.TimeCheckTask} entity.
 */
@RegisterForReflection
public class TimeCheckTaskDTO implements Serializable {
    
    public Long id;

    @NotNull
    @JsonbDateFormat(value = Constants.LOCAL_DATE_FORMAT)
    public LocalDate date;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeCheckTaskDTO)) {
            return false;
        }

        return id != null && id.equals(((TimeCheckTaskDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeCheckTaskDTO{" +
            "id=" + id +
            ", date='" + date + "'" +
            "}";
    }
}

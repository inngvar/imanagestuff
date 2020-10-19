package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import org.manage.config.Constants;
import javax.json.bind.annotation.JsonbDateFormat;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.manage.domain.TimeLog} entity.
 */
@RegisterForReflection
public class TimeLogDTO implements Serializable {
    
    public Long id;

    @NotNull
    @JsonbDateFormat(value = Constants.LOCAL_DATE_FORMAT)
    public LocalDate date;

    @NotNull
    public ZonedDateTime checkIn;

    @NotNull
    public ZonedDateTime checkOut;

    public Long memberId;
    public String memberLastName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeLogDTO)) {
            return false;
        }

        return id != null && id.equals(((TimeLogDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeLogDTO{" +
            "id=" + id +
            ", date='" + date + "'" +
            ", checkIn='" + checkIn + "'" +
            ", checkOut='" + checkOut + "'" +
            ", memberId=" + memberId +
            ", memberLastName='" + memberLastName + "'" +
            "}";
    }
}

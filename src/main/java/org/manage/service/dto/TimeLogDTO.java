package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
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
    public ZonedDateTime timestamp;

    public Long memberId;
    public String memberLogin;
    public Long timeCheckTaskId;

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
            ", timestamp='" + timestamp + "'" +
            ", memberId=" + memberId +
            ", memberLogin='" + memberLogin + "'" +
            ", timeCheckTaskId=" + timeCheckTaskId +
            "}";
    }
}

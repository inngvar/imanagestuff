package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.manage.domain.TaskConfig} entity.
 */
@RegisterForReflection
public class TaskConfigDTO implements Serializable {
    
    public Long id;

    @NotNull
    public String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskConfigDTO)) {
            return false;
        }

        return id != null && id.equals(((TaskConfigDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TaskConfigDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            "}";
    }
}

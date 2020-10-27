package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the {@link org.manage.domain.Project} entity.
 */
@RegisterForReflection
public class ProjectDTO implements Serializable {
    
    public Long id;

    @NotNull
    public String name;

    @Size(max = 4000)
    public String description;

    @Size(max = 4000)
    public String sendReports;

    public Long projectManagerId;
    public String projectManagerLogin;
    public Set<MemberDTO> members = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectDTO)) {
            return false;
        }

        return id != null && id.equals(((ProjectDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", sendReports='" + sendReports + "'" +
            ", projectManagerId=" + projectManagerId +
            ", projectManagerLogin='" + projectManagerLogin + "'" +
            ", members='" + members + "'" +
            "}";
    }
}

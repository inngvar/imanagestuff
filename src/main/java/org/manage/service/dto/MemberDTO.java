package org.manage.service.dto;


import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link org.manage.domain.Member} entity.
 */
@RegisterForReflection
public class MemberDTO implements Serializable {

    public Long id;

    @NotNull
    public String login;

    @NotNull
    public String firstName;

    public String middleName;

    @NotNull
    public String lastName;

    public String fio;

    public Long defaultProjectId;

    public String defaultProjectName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberDTO)) {
            return false;
        }

        return id != null && id.equals(((MemberDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
            "id=" + id +
            ", login='" + login + "'" +
            ", firstName='" + firstName + "'" +
            ", middleName='" + middleName + "'" +
            ", lastName='" + lastName + "'" +
            ", defaultProjectId=" + defaultProjectId +
            ", defaultProjectName='" + defaultProjectName + "'" +
            "}";
    }
}

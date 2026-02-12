package org.manage.service.dto;

import org.manage.config.Constants;
import org.manage.domain.User;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RegisterForReflection
public class AccountDTO {
    public Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    public String login;

    @Email
    @Size(min = 5, max = 254)
    public String email;

    @Size(max = 256)
    public String imageUrl;

    public boolean activated = false;

    @Size(min = 2, max = 10)
    public String langKey;

    public String firstName;

    public String middleName;

    public String lastName;

    public String createdBy;

    public Instant createdDate;

    public String lastModifiedBy;

    public Instant lastModifiedDate;

    public Set<String> authorities;

    public Long defaultProjectId;

    public String defaultProjectName;

    public AccountDTO() {
    }

    public AccountDTO(User user) {
        this.id = user.id;
        this.login = user.login;
        this.email = user.email;
        this.activated = user.activated;
        this.imageUrl = user.imageUrl;
        this.langKey = user.langKey;
        this.createdBy = user.createdBy;
        this.createdDate = user.createdDate;
        this.lastModifiedBy = user.lastModifiedBy;
        this.lastModifiedDate = user.lastModifiedDate;
        this.authorities = user.authorities.stream().map(authority -> authority.name).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return (
            "AccountDTO{" +
            "login='" +
            login +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", imageUrl='" +
            imageUrl +
            '\'' +
            ", activated=" +
            activated +
            ", langKey='" +
            langKey +
            '\'' +
            ", firstName='" +
            firstName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", createdBy=" +
            createdBy +
            ", createdDate=" +
            createdDate +
            ", lastModifiedBy='" +
            lastModifiedBy +
            '\'' +
            ", lastModifiedDate=" +
            lastModifiedDate +
            ", authorities=" +
            authorities +
            "}"
        );
    }
}

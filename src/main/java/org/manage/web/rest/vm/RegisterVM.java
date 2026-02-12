package org.manage.web.rest.vm;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * View Model object for storing a user's credentials and full name during registration.
 */
@RegisterForReflection
public class RegisterVM {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$|^[_.@A-Za-z0-9-]+$")
    @SuppressWarnings("RegExpRedundantEscape")
    @Size(min = 1, max = 50)
    public String login;

    @NotBlank
    @Size(max = 50)
    public String firstName;

    @Size(max = 50)
    public String middleName;

    @NotBlank
    @Size(max = 50)
    public String lastName;

    @NotBlank
    @Email
    @Size(min = 5, max = 254)
    public String email;

    @NotBlank
    @Size(min = 4, max = 100)
    public String password;

    @Override
    public String toString() {
        return "RegisterVM{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", middleName='" + middleName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}

package org.manage.web.rest.vm;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.NotNull;

@RegisterForReflection
public class MemberCreateVM {

    public Long id;

    @NotNull
    public String login;

    @NotNull
    public String firstName;

    public String middleName;

    @NotNull
    public String lastName;

    public String fio;

    public Long telegramId;

    @Override
    public String toString() {
        return "MemberCreateVM{" +
            "id=" + id +
            ", login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", middleName='" + middleName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", fio='" + fio + '\'' +
            ", telegramId=" + telegramId +
            '}';
    }
}

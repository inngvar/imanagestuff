package org.manage.client.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class User {
    public Long id;
    public boolean is_bot;
    public String first_name;
    public String last_name;
    public String username;
}

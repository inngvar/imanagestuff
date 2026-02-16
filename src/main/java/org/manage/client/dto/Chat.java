package org.manage.client.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Chat {
    public Long id;
    public String type;
    public String title;
    public String username;
    public String first_name;
    public String last_name;
}

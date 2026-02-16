package org.manage.client.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Update {
    public Long update_id;
    public Message message;
}

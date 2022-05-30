package org.manage.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Duration;

@RegisterForReflection
public class ProjectDuration {

    public ProjectDTO project = new ProjectDTO();

    public Duration duration;

}

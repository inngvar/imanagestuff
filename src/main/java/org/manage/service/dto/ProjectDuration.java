package org.manage.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.manage.domain.Project;
import org.manage.domain.TimeEntry;

import java.time.Duration;
import java.util.List;

@RegisterForReflection
public class ProjectDuration {

    public ProjectDTO project = new ProjectDTO();

    public Long duration;

}

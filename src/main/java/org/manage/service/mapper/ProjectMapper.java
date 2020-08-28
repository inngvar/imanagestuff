package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.ProjectDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "cdi", uses = {MemberMapper.class})
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {



    default Project fromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.id = id;
        return project;
    }
}

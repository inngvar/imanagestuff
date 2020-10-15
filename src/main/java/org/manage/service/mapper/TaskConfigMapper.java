package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.TaskConfigDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskConfig} and its DTO {@link TaskConfigDTO}.
 */
@Mapper(componentModel = "cdi", uses = {})
public interface TaskConfigMapper extends EntityMapper<TaskConfigDTO, TaskConfig> {


    @Mapping(target = "members", ignore = true)
    TaskConfig toEntity(TaskConfigDTO taskConfigDTO);

    default TaskConfig fromId(Long id) {
        if (id == null) {
            return null;
        }
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.id = id;
        return taskConfig;
    }
}

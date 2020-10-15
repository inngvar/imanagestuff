package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.TimeCheckTaskDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeCheckTask} and its DTO {@link TimeCheckTaskDTO}.
 */
@Mapper(componentModel = "cdi", uses = {})
public interface TimeCheckTaskMapper extends EntityMapper<TimeCheckTaskDTO, TimeCheckTask> {


    @Mapping(target = "checks", ignore = true)
    TimeCheckTask toEntity(TimeCheckTaskDTO timeCheckTaskDTO);

    default TimeCheckTask fromId(Long id) {
        if (id == null) {
            return null;
        }
        TimeCheckTask timeCheckTask = new TimeCheckTask();
        timeCheckTask.id = id;
        return timeCheckTask;
    }
}

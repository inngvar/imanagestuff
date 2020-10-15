package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.TimeLogDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeLog} and its DTO {@link TimeLogDTO}.
 */
@Mapper(componentModel = "cdi", uses = {MemberMapper.class, TimeCheckTaskMapper.class})
public interface TimeLogMapper extends EntityMapper<TimeLogDTO, TimeLog> {

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.login", target = "memberLogin")
    @Mapping(source = "timeCheckTask.id", target = "timeCheckTaskId")
    TimeLogDTO toDto(TimeLog timeLog);

    @Mapping(source = "memberId", target = "member")
    @Mapping(source = "timeCheckTaskId", target = "timeCheckTask")
    TimeLog toEntity(TimeLogDTO timeLogDTO);

    default TimeLog fromId(Long id) {
        if (id == null) {
            return null;
        }
        TimeLog timeLog = new TimeLog();
        timeLog.id = id;
        return timeLog;
    }
}

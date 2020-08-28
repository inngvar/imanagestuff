package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.TimeEntryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeEntry} and its DTO {@link TimeEntryDTO}.
 */
@Mapper(componentModel = "cdi", uses = {MemberMapper.class, ProjectMapper.class})
public interface TimeEntryMapper extends EntityMapper<TimeEntryDTO, TimeEntry> {

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.login", target = "memberLogin")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    TimeEntryDTO toDto(TimeEntry timeEntry);

    @Mapping(source = "memberId", target = "member")
    @Mapping(source = "projectId", target = "project")
    TimeEntry toEntity(TimeEntryDTO timeEntryDTO);

    default TimeEntry fromId(Long id) {
        if (id == null) {
            return null;
        }
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.id = id;
        return timeEntry;
    }
}

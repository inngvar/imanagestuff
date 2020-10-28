package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.MemberDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Member} and its DTO {@link MemberDTO}.
 */
@Mapper(componentModel = "cdi", uses = {ProjectMapper.class})
public interface MemberMapper extends EntityMapper<MemberDTO, Member> {

    @Mapping(source = "defaultProject.id", target = "defaultProjectId")
    @Mapping(source = "defaultProject.name", target = "defaultProjectName")
    @Mapping(target = "fio", expression = "java(member.lastName +\" \"+ member.firstName)")
    MemberDTO toDto(Member member);

    @Mapping(source = "defaultProjectId", target = "defaultProject")
    @Mapping(target = "timeLogs", ignore = true)
    @Mapping(target = "projects", ignore = true)
    Member toEntity(MemberDTO memberDTO);

    default Member fromId(Long id) {
        if (id == null) {
            return null;
        }
        Member member = new Member();
        member.id = id;
        return member;
    }
}

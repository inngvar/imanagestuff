package org.manage.service.mapper;


import org.manage.domain.*;
import org.manage.service.dto.MemberDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Member} and its DTO {@link MemberDTO}.
 */
@Mapper(componentModel = "cdi", uses = {TaskConfigMapper.class})
public interface MemberMapper extends EntityMapper<MemberDTO, Member> {

    @Mapping(source = "taskConfig.id", target = "taskConfigId")
    MemberDTO toDto(Member member);

    @Mapping(target = "timeLogs", ignore = true)
    @Mapping(source = "taskConfigId", target = "taskConfig")
    @Mapping(target = "projects", ignore = true)
    Member toEntity(MemberDTO memberDTO);

    @Mapping(target = "fio", expression = "java(member.lastName +\" \"+ member.firstName)")
    MemberDTO toDto(Member member);

    default Member fromId(Long id) {
        if (id == null) {
            return null;
        }
        Member member = new Member();
        member.id = id;
        return member;
    }
}

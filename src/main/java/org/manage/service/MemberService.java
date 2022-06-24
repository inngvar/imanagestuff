package org.manage.service;

import io.quarkus.panache.common.Page;
import io.quarkus.security.identity.SecurityIdentity;
import org.manage.domain.Member;
import org.manage.domain.Project;
import org.manage.service.dto.MemberDTO;
import org.manage.service.dto.ProjectDTO;
import org.manage.service.mapper.MemberMapper;
import org.manage.web.rest.vm.MemberCreateVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
@Transactional
public class MemberService {

    private final Logger log = LoggerFactory.getLogger(MemberService.class);

    @Inject
    MemberMapper memberMapper;

    @Inject
    SecurityIdentity securityIdentity;

    @Transactional
    public MemberDTO createMember(MemberCreateVM memberCreateVM){
        log.debug("Request to createMember Member : {}", memberCreateVM);
        var member = memberMapper.toEntity(memberCreateVM);
        member = Member.persistOrUpdate(member);
        return memberMapper.toDto(member);
    }

    @Transactional
    public MemberDTO persistOrUpdate(MemberDTO memberDTO) {
        log.debug("Request to save Member : {}", memberDTO);
        var member = memberMapper.toEntity(memberDTO);
        member = Member.persistOrUpdate(member);
        return memberMapper.toDto(member);
    }

    /**
     * Delete the Member by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Member : {}", id);
        Member.findByIdOptional(id).ifPresent(member -> {
            member.delete();
        });
    }

    /**
     * Get one member by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<MemberDTO> findOne(Long id) {
        log.debug("Request to get Member : {}", id);
        return Member.findByIdOptional(id)
            .map(member -> memberMapper.toDto((Member) member));
    }

    /**
     * Get all the members.
     *
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<MemberDTO> findAll(Page page) {
        log.debug("Request to get all Members");
        return new Paged<>(Member.findAll().page(page))
            .map(member -> memberMapper.toDto((Member) member));
    }


    public Stream<MemberDTO> findAllByProject(ProjectDTO projectDto) {
        Project pr = Project.findById(projectDto.id);
        return pr.members.stream().map(m -> memberMapper.toDto(m));
    }

    public Optional<MemberDTO> findByLogin(String login) {
        log.debug("Request to get Member : {}", login);
        return Member.findByLogin(login)
            .map(member -> memberMapper.toDto(member));
    }

    public boolean matchesLoggedInMember(Long id, String currentUserLogin) {
        MemberDTO currentMember = findByLogin(currentUserLogin).orElse(null);
        return currentMember != null && currentMember.id.equals(id);
    }
}

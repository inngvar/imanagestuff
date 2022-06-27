package org.manage.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Page;
import io.quarkus.security.identity.SecurityIdentity;
import org.manage.domain.Member;
import org.manage.domain.Project;
import org.manage.domain.TimeEntry;
import org.manage.service.dto.TimeEntryDTO;
import org.manage.service.mapper.ProjectMapper;
import org.manage.service.mapper.TimeEntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class TimeEntryService {

    private final Logger log = LoggerFactory.getLogger(TimeEntryService.class);

    @Inject
    TimeEntryMapper timeEntryMapper;

    @Inject
    ProjectMapper projectMapper;

    @Inject
    ProjectService projectService;

    @Inject
    MemberService memberService;

    @Transactional
    public TimeEntryDTO persistOrUpdate(TimeEntryDTO timeEntryDTO, SecurityIdentity securityIdentity) {
        log.debug("Request to save TimeEntry : {}", timeEntryDTO);
        if (!memberService.matchesLoggedInMember(timeEntryDTO.memberId, securityIdentity.getPrincipal().getName())) {
            throw new WebApplicationException(Response.status(403).entity("Specified user doesn't match current user").build());
        }
        var timeEntry = timeEntryMapper.toEntity(timeEntryDTO);
        timeEntry = TimeEntry.persistOrUpdate(timeEntry);
        return timeEntryMapper.toDto(timeEntry);
    }

    /**
     * Delete the TimeEntry by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id, SecurityIdentity securityIdentity) {
        log.debug("Request to delete TimeEntry : {}", id);
        TimeEntry entry = TimeEntry.findById(id);
        if (entry == null) {
            throw new WebApplicationException(Response.status(400).entity("Time entry with id=" + id + " not found").build());
        }
        if (!memberService.matchesLoggedInMember(entry.member.id, securityIdentity.getPrincipal().getName())) {
            throw new WebApplicationException(Response.status(403).entity("Specified user doesn't match current user").build());
        }
        entry.delete();
    }

    /**
     * Get one timeEntry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TimeEntryDTO> findOne(Long id) {
        log.debug("Request to get TimeEntry : {}", id);
        return TimeEntry.findByIdOptional(id)
            .map(timeEntry -> timeEntryMapper.toDto((TimeEntry) timeEntry));
    }

    /**
     * Get all the timeEntries.
     *
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<TimeEntryDTO> findAll(Page page) {
        log.debug("Request to get all TimeEntries");
        return new Paged<>(TimeEntry.findAll().page(page))
            .map(timeEntry -> timeEntryMapper.toDto((TimeEntry) timeEntry));
    }


    public List<TimeEntryDTO> findByMemberAndDateAndProject(Long memberId, LocalDate dateFrom, LocalDate dateTo, Long projectId) {
        log.debug("Request to find all TimeEntries by member{} and dateFrom{} dateTo{}", memberId, dateFrom, dateTo);
        return TimeEntry.getAllByDateBetweenAndMemberAndProject(dateFrom, dateTo, Member.findById(memberId), Project.findById(projectId))
            .map(t -> timeEntryMapper.toDto(t))
            .collect(Collectors.toList());
    }

    public Paged<TimeEntryDTO> findByParticipatingProjects(String login, Page page) {
        log.debug("Request to find all TimeEntries by member{}", login);
        return new Paged<>(TimeEntry.find("From TimeEntry e WHERE e.project in ?1"
            ,projectService.findByLogin(login).stream().map(p ->projectMapper.toEntity(p)).collect(Collectors.toList())).page(page))
            .map(timeEntry -> timeEntryMapper.toDto((TimeEntry) timeEntry));
    }
}

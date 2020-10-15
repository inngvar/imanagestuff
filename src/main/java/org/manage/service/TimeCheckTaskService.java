package org.manage.service;

import io.quarkus.panache.common.Page;
import org.manage.domain.TimeCheckTask;
import org.manage.service.dto.TimeCheckTaskDTO;
import org.manage.service.mapper.TimeCheckTaskMapper;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class TimeCheckTaskService {

    private final Logger log = LoggerFactory.getLogger(TimeCheckTaskService.class);

    @Inject
    TimeCheckTaskMapper timeCheckTaskMapper;

    @Transactional
    public TimeCheckTaskDTO persistOrUpdate(TimeCheckTaskDTO timeCheckTaskDTO) {
        log.debug("Request to save TimeCheckTask : {}", timeCheckTaskDTO);
        var timeCheckTask = timeCheckTaskMapper.toEntity(timeCheckTaskDTO);
        timeCheckTask = TimeCheckTask.persistOrUpdate(timeCheckTask);
        return timeCheckTaskMapper.toDto(timeCheckTask);
    }

    /**
     * Delete the TimeCheckTask by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete TimeCheckTask : {}", id);
        TimeCheckTask.findByIdOptional(id).ifPresent(timeCheckTask -> {
            timeCheckTask.delete();
        });
    }

    /**
     * Get one timeCheckTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TimeCheckTaskDTO> findOne(Long id) {
        log.debug("Request to get TimeCheckTask : {}", id);
        return TimeCheckTask.findByIdOptional(id)
            .map(timeCheckTask -> timeCheckTaskMapper.toDto((TimeCheckTask) timeCheckTask)); 
    }

    /**
     * Get all the timeCheckTasks.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<TimeCheckTaskDTO> findAll(Page page) {
        log.debug("Request to get all TimeCheckTasks");
        return new Paged<>(TimeCheckTask.findAll().page(page))
            .map(timeCheckTask -> timeCheckTaskMapper.toDto((TimeCheckTask) timeCheckTask));
    }



}

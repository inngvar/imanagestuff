package org.manage.service;

import io.quarkus.panache.common.Page;
import org.manage.domain.TimeLog;
import org.manage.service.dto.TimeLogDTO;
import org.manage.service.mapper.TimeLogMapper;
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
public class TimeLogService {

    private final Logger log = LoggerFactory.getLogger(TimeLogService.class);

    @Inject
    TimeLogMapper timeLogMapper;

    @Transactional
    public TimeLogDTO persistOrUpdate(TimeLogDTO timeLogDTO) {
        log.debug("Request to save TimeLog : {}", timeLogDTO);
        var timeLog = timeLogMapper.toEntity(timeLogDTO);
        timeLog = TimeLog.persistOrUpdate(timeLog);
        return timeLogMapper.toDto(timeLog);
    }

    /**
     * Delete the TimeLog by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete TimeLog : {}", id);
        TimeLog.findByIdOptional(id).ifPresent(timeLog -> {
            timeLog.delete();
        });
    }

    /**
     * Get one timeLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TimeLogDTO> findOne(Long id) {
        log.debug("Request to get TimeLog : {}", id);
        return TimeLog.findByIdOptional(id)
            .map(timeLog -> timeLogMapper.toDto((TimeLog) timeLog)); 
    }

    /**
     * Get all the timeLogs.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<TimeLogDTO> findAll(Page page) {
        log.debug("Request to get all TimeLogs");
        return new Paged<>(TimeLog.findAll().page(page))
            .map(timeLog -> timeLogMapper.toDto((TimeLog) timeLog));
    }



}

package org.manage.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Page;
import org.manage.domain.Member;
import org.manage.domain.Project;
import org.manage.domain.TimeEntry;
import org.manage.domain.TimeLog;
import org.manage.service.dto.TimeEntryDTO;
import org.manage.service.dto.TimeLogDTO;
import org.manage.service.mapper.TimeLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
        TimeLog.findByIdOptional(id).ifPresent(PanacheEntityBase::delete);
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

    public List<TimeLogDTO> findByDateBetween(LocalDate dateFrom, LocalDate dateTo) {
        log.debug("Request to find all TimeLogs by dateFrom{} and dateTo{}", dateFrom, dateTo);
        return TimeLog.getAllByDateBetween(dateFrom, dateTo)
            .map(t -> timeLogMapper.toDto(t))
            .collect(Collectors.toList());
    }

    @Transactional
    public TimeLogDTO updateCheckIn(TimeLogDTO timeLogDTO) {
        return updateCheckInCheckOut(timeLogDTO, (exist, income) -> exist.checkIn = income.checkIn);
    }

    @Transactional
    public TimeLogDTO updateCheckOut(TimeLogDTO timeLogDTO) {
        return updateCheckInCheckOut(timeLogDTO, (exist, income) -> exist.checkOut = income.checkOut);
    }

    private TimeLogDTO updateCheckInCheckOut(TimeLogDTO timeLogDTO, BiConsumer<TimeLog, TimeLog> updateFunc) {
        log.debug("Request to update current TimeLog : {}", timeLogDTO);
        var timeLog = timeLogMapper.toEntity(timeLogDTO);

        TimeLog log = TimeLog.find("From TimeLog e WHERE e.member = ?1 and e.date = ?2",
            timeLog.member,
            timeLogDTO.date
        )
            .firstResult();

        if (log != null) {
            updateFunc.accept(log, timeLog);
            timeLog = TimeLog.update(log);
        } else {
            timeLog = TimeLog.persistOrUpdate(timeLog);
        }

        return timeLogMapper.toDto(timeLog);
    }

}

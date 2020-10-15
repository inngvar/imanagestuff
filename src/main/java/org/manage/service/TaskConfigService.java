package org.manage.service;

import io.quarkus.panache.common.Page;
import org.manage.domain.TaskConfig;
import org.manage.service.dto.TaskConfigDTO;
import org.manage.service.mapper.TaskConfigMapper;
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
public class TaskConfigService {

    private final Logger log = LoggerFactory.getLogger(TaskConfigService.class);

    @Inject
    TaskConfigMapper taskConfigMapper;

    @Transactional
    public TaskConfigDTO persistOrUpdate(TaskConfigDTO taskConfigDTO) {
        log.debug("Request to save TaskConfig : {}", taskConfigDTO);
        var taskConfig = taskConfigMapper.toEntity(taskConfigDTO);
        taskConfig = TaskConfig.persistOrUpdate(taskConfig);
        return taskConfigMapper.toDto(taskConfig);
    }

    /**
     * Delete the TaskConfig by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete TaskConfig : {}", id);
        TaskConfig.findByIdOptional(id).ifPresent(taskConfig -> {
            taskConfig.delete();
        });
    }

    /**
     * Get one taskConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TaskConfigDTO> findOne(Long id) {
        log.debug("Request to get TaskConfig : {}", id);
        return TaskConfig.findByIdOptional(id)
            .map(taskConfig -> taskConfigMapper.toDto((TaskConfig) taskConfig)); 
    }

    /**
     * Get all the taskConfigs.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<TaskConfigDTO> findAll(Page page) {
        log.debug("Request to get all TaskConfigs");
        return new Paged<>(TaskConfig.findAll().page(page))
            .map(taskConfig -> taskConfigMapper.toDto((TaskConfig) taskConfig));
    }



}

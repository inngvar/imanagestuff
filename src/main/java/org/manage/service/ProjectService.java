package org.manage.service;

import io.quarkus.panache.common.Page;
import org.manage.domain.Project;
import org.manage.service.dto.ProjectDTO;
import org.manage.service.mapper.ProjectMapper;
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
public class ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    ProjectMapper projectMapper;

    @Transactional
    public ProjectDTO persistOrUpdate(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        var project = projectMapper.toEntity(projectDTO);
        project = Project.persistOrUpdate(project);
        return projectMapper.toDto(project);
    }

    /**
     * Delete the Project by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);
        Project.findByIdOptional(id).ifPresent(project -> {
            project.delete();
        });
    }

    /**
     * Get one project by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ProjectDTO> findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        return Project.findOneWithEagerRelationships(id)
            .map(project -> projectMapper.toDto((Project) project)); 
    }

    /**
     * Get all the projects.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<ProjectDTO> findAll(Page page) {
        log.debug("Request to get all Projects");
        return new Paged<>(Project.findAll().page(page))
            .map(project -> projectMapper.toDto((Project) project));
    }


    /**
     * Get all the projects with eager load of many-to-many relationships.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<ProjectDTO> findAllWithEagerRelationships(Page page) {
        var projects = Project.findAllWithEagerRelationships().page(page).list();
        var totalCount = Project.findAll().count();
        var pageCount = Project.findAll().page(page).pageCount();
        return new Paged<>(page.index, page.size, totalCount, pageCount, projects)
            .map(project -> projectMapper.toDto((Project) project));
    }


}

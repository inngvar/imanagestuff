package org.manage.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import org.manage.domain.Project;
import org.manage.web.rest.errors.BadRequestAlertException;
import org.manage.web.util.HeaderUtil;
import org.manage.web.util.ResponseUtil;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.manage.service.Paged;
import org.manage.web.rest.vm.PageRequestVM;
import org.manage.web.util.PaginationUtil;

import javax.enterprise.context.ApplicationScoped;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.manage.domain.Project}.
 */
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "project";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /projects} : Create a new project.
     *
     * @param project the project to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has already an ID.
     */
    @POST
    @Transactional
    public Response createProject(@Valid Project project, @Context UriInfo uriInfo) {
        log.debug("REST request to save Project : {}", project);
        if (project.id != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = Project.persistOrUpdate(project);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /projects} : Updates an existing project.
     *
     * @param project the project to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated project,
     * or with status {@code 400 (Bad Request)} if the project is not valid,
     * or with status {@code 500 (Internal Server Error)} if the project couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateProject(@Valid Project project) {
        log.debug("REST request to update Project : {}", project);
        if (project.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = Project.persistOrUpdate(project);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, project.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /projects/:id} : delete the "id" project.
     *
     * @param id the id of the project to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteProject(@PathParam("id") Long id) {
        log.debug("REST request to delete Project : {}", id);
        Project.findByIdOptional(id).ifPresent(project -> {
            project.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param pageRequest the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link Response} with status {@code 200 (OK)} and the list of projects in body.
     */
    @GET
    @Transactional
    public Response getAllProjects(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo, @QueryParam(value = "eagerload") boolean eagerload) {
        log.debug("REST request to get a page of Projects");
        var page = pageRequest.toPage();
        Paged<Project> result;
        if (eagerload) {
            var projects = Project.findAllWithEagerRelationships().page(page).list();
            var totalCount = Project.findAll().count();
            var pageCount = Project.findAll().page(page).pageCount();
            result = new Paged<>(page.index, page.size, totalCount, pageCount, projects);
        } else {
            result = new Paged<>(Project.findAll().page(page));
        }
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }


    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the project to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the project, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getProject(@PathParam("id") Long id) {
        log.debug("REST request to get Project : {}", id);
        Optional<Project> project = Project.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(project);
    }
}

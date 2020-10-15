package org.manage.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import org.manage.service.TaskConfigService;
import org.manage.web.rest.errors.BadRequestAlertException;
import org.manage.web.util.HeaderUtil;
import org.manage.web.util.ResponseUtil;
import org.manage.service.dto.TaskConfigDTO;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.manage.service.Paged;
import org.manage.web.rest.vm.PageRequestVM;
import org.manage.web.util.PaginationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.manage.domain.TaskConfig}.
 */
@Path("/api/task-configs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TaskConfigResource {

    private final Logger log = LoggerFactory.getLogger(TaskConfigResource.class);

    private static final String ENTITY_NAME = "taskConfig";

    @ConfigProperty(name = "application.name")
    String applicationName;


    @Inject
    TaskConfigService taskConfigService;
    /**
     * {@code POST  /task-configs} : Create a new taskConfig.
     *
     * @param taskConfigDTO the taskConfigDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new taskConfigDTO, or with status {@code 400 (Bad Request)} if the taskConfig has already an ID.
     */
    @POST
    public Response createTaskConfig(@Valid TaskConfigDTO taskConfigDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save TaskConfig : {}", taskConfigDTO);
        if (taskConfigDTO.id != null) {
            throw new BadRequestAlertException("A new taskConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = taskConfigService.persistOrUpdate(taskConfigDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /task-configs} : Updates an existing taskConfig.
     *
     * @param taskConfigDTO the taskConfigDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated taskConfigDTO,
     * or with status {@code 400 (Bad Request)} if the taskConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskConfigDTO couldn't be updated.
     */
    @PUT
    public Response updateTaskConfig(@Valid TaskConfigDTO taskConfigDTO) {
        log.debug("REST request to update TaskConfig : {}", taskConfigDTO);
        if (taskConfigDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = taskConfigService.persistOrUpdate(taskConfigDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskConfigDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /task-configs/:id} : delete the "id" taskConfig.
     *
     * @param id the id of the taskConfigDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTaskConfig(@PathParam("id") Long id) {
        log.debug("REST request to delete TaskConfig : {}", id);
        taskConfigService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /task-configs} : get all the taskConfigs.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of taskConfigs in body.
     */
    @GET
    public Response getAllTaskConfigs(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of TaskConfigs");
        var page = pageRequest.toPage();
        Paged<TaskConfigDTO> result = taskConfigService.findAll(page);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }


    /**
     * {@code GET  /task-configs/:id} : get the "id" taskConfig.
     *
     * @param id the id of the taskConfigDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the taskConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getTaskConfig(@PathParam("id") Long id) {
        log.debug("REST request to get TaskConfig : {}", id);
        Optional<TaskConfigDTO> taskConfigDTO = taskConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskConfigDTO);
    }
}

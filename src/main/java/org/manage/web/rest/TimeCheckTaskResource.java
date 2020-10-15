package org.manage.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import org.manage.service.TimeCheckTaskService;
import org.manage.web.rest.errors.BadRequestAlertException;
import org.manage.web.util.HeaderUtil;
import org.manage.web.util.ResponseUtil;
import org.manage.service.dto.TimeCheckTaskDTO;

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
 * REST controller for managing {@link org.manage.domain.TimeCheckTask}.
 */
@Path("/api/time-check-tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TimeCheckTaskResource {

    private final Logger log = LoggerFactory.getLogger(TimeCheckTaskResource.class);

    private static final String ENTITY_NAME = "timeCheckTask";

    @ConfigProperty(name = "application.name")
    String applicationName;


    @Inject
    TimeCheckTaskService timeCheckTaskService;
    /**
     * {@code POST  /time-check-tasks} : Create a new timeCheckTask.
     *
     * @param timeCheckTaskDTO the timeCheckTaskDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new timeCheckTaskDTO, or with status {@code 400 (Bad Request)} if the timeCheckTask has already an ID.
     */
    @POST
    public Response createTimeCheckTask(@Valid TimeCheckTaskDTO timeCheckTaskDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save TimeCheckTask : {}", timeCheckTaskDTO);
        if (timeCheckTaskDTO.id != null) {
            throw new BadRequestAlertException("A new timeCheckTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = timeCheckTaskService.persistOrUpdate(timeCheckTaskDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /time-check-tasks} : Updates an existing timeCheckTask.
     *
     * @param timeCheckTaskDTO the timeCheckTaskDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated timeCheckTaskDTO,
     * or with status {@code 400 (Bad Request)} if the timeCheckTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeCheckTaskDTO couldn't be updated.
     */
    @PUT
    public Response updateTimeCheckTask(@Valid TimeCheckTaskDTO timeCheckTaskDTO) {
        log.debug("REST request to update TimeCheckTask : {}", timeCheckTaskDTO);
        if (timeCheckTaskDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = timeCheckTaskService.persistOrUpdate(timeCheckTaskDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeCheckTaskDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /time-check-tasks/:id} : delete the "id" timeCheckTask.
     *
     * @param id the id of the timeCheckTaskDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTimeCheckTask(@PathParam("id") Long id) {
        log.debug("REST request to delete TimeCheckTask : {}", id);
        timeCheckTaskService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /time-check-tasks} : get all the timeCheckTasks.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of timeCheckTasks in body.
     */
    @GET
    public Response getAllTimeCheckTasks(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of TimeCheckTasks");
        var page = pageRequest.toPage();
        Paged<TimeCheckTaskDTO> result = timeCheckTaskService.findAll(page);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }


    /**
     * {@code GET  /time-check-tasks/:id} : get the "id" timeCheckTask.
     *
     * @param id the id of the timeCheckTaskDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the timeCheckTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getTimeCheckTask(@PathParam("id") Long id) {
        log.debug("REST request to get TimeCheckTask : {}", id);
        Optional<TimeCheckTaskDTO> timeCheckTaskDTO = timeCheckTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timeCheckTaskDTO);
    }
}

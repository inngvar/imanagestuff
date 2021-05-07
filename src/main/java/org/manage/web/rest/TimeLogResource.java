package org.manage.web.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.manage.service.Paged;
import org.manage.service.TimeLogService;
import org.manage.service.dto.TimeLogDTO;
import org.manage.web.rest.errors.BadRequestAlertException;
import org.manage.web.rest.vm.PageRequestVM;
import org.manage.web.util.HeaderUtil;
import org.manage.web.util.PaginationUtil;
import org.manage.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static javax.ws.rs.core.UriBuilder.fromPath;

/**
 * REST controller for managing {@link org.manage.domain.TimeLog}.
 */
@Path("/api/time-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TimeLogResource {

    private final Logger log = LoggerFactory.getLogger(TimeLogResource.class);

    private static final String ENTITY_NAME = "timeLog";

    @ConfigProperty(name = "application.name")
    String applicationName;


    @Inject
    TimeLogService timeLogService;
    /**
     * {@code POST  /time-logs} : Create a new timeLog.
     *
     * @param timeLogDTO the timeLogDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new timeLogDTO, or with status {@code 400 (Bad Request)} if the timeLog has already an ID.
     */
    @POST
    public Response createTimeLog(@Valid TimeLogDTO timeLogDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save TimeLog : {}", timeLogDTO);
        if (timeLogDTO.id != null) {
            throw new BadRequestAlertException("A new timeLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = timeLogService.persistOrUpdate(timeLogDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /time-logs} : Updates an existing timeLog.
     *
     * @param timeLogDTO the timeLogDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated timeLogDTO,
     * or with status {@code 400 (Bad Request)} if the timeLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeLogDTO couldn't be updated.
     */
    @PUT
    public Response updateTimeLog(@Valid TimeLogDTO timeLogDTO) {
        log.debug("REST request to update TimeLog : {}", timeLogDTO);
        if (timeLogDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = timeLogService.persistOrUpdate(timeLogDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeLogDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /time-logs/:id} : delete the "id" timeLog.
     *
     * @param id the id of the timeLogDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTimeLog(@PathParam("id") Long id) {
        log.debug("REST request to delete TimeLog : {}", id);
        timeLogService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /time-logs} : get all the timeLogs.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of timeLogs in body.
     */
    @GET
    public Response getAllTimeLogs(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of TimeLogs");
        var page = pageRequest.toPage();
        Paged<TimeLogDTO> result = timeLogService.findAll(page);
        var response = Response.ok().entity(result.content);
        PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }


    /**
     * {@code GET  /time-logs/:id} : get the "id" timeLog.
     *
     * @param id the id of the timeLogDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the timeLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getTimeLog(@PathParam("id") Long id) {
        log.debug("REST request to get TimeLog : {}", id);
        Optional<TimeLogDTO> timeLogDTO = timeLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timeLogDTO);
    }

    @POST
    @Path("/{memberId}/checkin")
    public Response checkin(@PathParam("memberId") Long memberId, @Context UriInfo uriInfo) {
        TimeLogDTO timeLogDTO = makeCheckInDTO(memberId);

        var result = timeLogService.updateCheckIn(timeLogDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    @POST
    @Path("/{memberId}/checkout")
    public Response checkout(@PathParam("memberId") Long memberId, @Context UriInfo uriInfo) {
        TimeLogDTO timeLogDTO = mackCheckOutDTO(memberId);

        var result = timeLogService.updateCheckOut(timeLogDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    private TimeLogDTO makeCheckInDTO(Long memberId) {
        TimeLogDTO timeLogDTO = new TimeLogDTO();
        timeLogDTO.date = LocalDate.now(ZoneId.systemDefault());
        timeLogDTO.checkIn = ZonedDateTime.now(ZoneId.systemDefault());
        timeLogDTO.memberId = memberId;
        return timeLogDTO;
    }

    private TimeLogDTO mackCheckOutDTO(Long memberId) {
        TimeLogDTO timeLogDTO = new TimeLogDTO();
        timeLogDTO.date = LocalDate.now(ZoneId.systemDefault());
        timeLogDTO.checkOut = ZonedDateTime.now(ZoneId.systemDefault());
        timeLogDTO.memberId = memberId;
        return timeLogDTO;
    }
}

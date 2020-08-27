package org.manage.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import org.manage.domain.TimeEntry;
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
 * REST controller for managing {@link org.manage.domain.TimeEntry}.
 */
@Path("/api/time-entries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TimeEntryResource {

    private final Logger log = LoggerFactory.getLogger(TimeEntryResource.class);

    private static final String ENTITY_NAME = "timeEntry";

    @ConfigProperty(name = "application.name")
    String applicationName;


    
    /**
     * {@code POST  /time-entries} : Create a new timeEntry.
     *
     * @param timeEntry the timeEntry to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new timeEntry, or with status {@code 400 (Bad Request)} if the timeEntry has already an ID.
     */
    @POST
    @Transactional
    public Response createTimeEntry(@Valid TimeEntry timeEntry, @Context UriInfo uriInfo) {
        log.debug("REST request to save TimeEntry : {}", timeEntry);
        if (timeEntry.id != null) {
            throw new BadRequestAlertException("A new timeEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = TimeEntry.persistOrUpdate(timeEntry);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /time-entries} : Updates an existing timeEntry.
     *
     * @param timeEntry the timeEntry to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated timeEntry,
     * or with status {@code 400 (Bad Request)} if the timeEntry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeEntry couldn't be updated.
     */
    @PUT
    @Transactional
    public Response updateTimeEntry(@Valid TimeEntry timeEntry) {
        log.debug("REST request to update TimeEntry : {}", timeEntry);
        if (timeEntry.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = TimeEntry.persistOrUpdate(timeEntry);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeEntry.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /time-entries/:id} : delete the "id" timeEntry.
     *
     * @param id the id of the timeEntry to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTimeEntry(@PathParam("id") Long id) {
        log.debug("REST request to delete TimeEntry : {}", id);
        TimeEntry.findByIdOptional(id).ifPresent(timeEntry -> {
            timeEntry.delete();
        });
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /time-entries} : get all the timeEntries.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of timeEntries in body.
     */
    @GET
    public Response getAllTimeEntries(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of TimeEntries");
        var page = pageRequest.toPage();
        var result = new Paged<>(TimeEntry.findAll().page(page));
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }


    /**
     * {@code GET  /time-entries/:id} : get the "id" timeEntry.
     *
     * @param id the id of the timeEntry to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the timeEntry, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getTimeEntry(@PathParam("id") Long id) {
        log.debug("REST request to get TimeEntry : {}", id);
        Optional<TimeEntry> timeEntry = TimeEntry.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(timeEntry);
    }
}

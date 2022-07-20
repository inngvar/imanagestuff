package org.manage.web.rest;

import org.manage.service.HolidayUpdater;
import org.manage.service.dto.YearInfo;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/calendar/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CalendarResource {

    @Inject
    HolidayUpdater holidayUpdater;

    @GET()
    @Path("{year}")
    @Transactional
    public Response calendar(@PathParam("year") int year) {
        YearInfo yearInfo = holidayUpdater.updateForYear(year);
        return Response.ok(yearInfo).build();
    }

}

package org.manage.web.rest;

import org.manage.domain.DayInfo;
import org.manage.domain.enums.DayType;
import org.manage.service.ConsultantHolidayService;
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

    public static final String CONSULTANT_SOURCE_TYPE = "consultant";

    @Inject
    ConsultantHolidayService holidayService;

    @GET()
    @Path("{year}")
    @Transactional
    public Response calendar(@PathParam("year") int year) {
        YearInfo yearInfo = holidayService.getByYear(year);
        for(var info : yearInfo.holidays){
            DayInfo dayInfo = DayInfo.getByDate(info, CONSULTANT_SOURCE_TYPE).orElse(new DayInfo());
            dayInfo.day = info;
            dayInfo.dayType = DayType.HOLIDAY;
            dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
            DayInfo.persist(dayInfo);
        }

        for( var info :yearInfo.weekends){
            DayInfo dayInfo = DayInfo.getByDate(info, CONSULTANT_SOURCE_TYPE).orElse(new DayInfo());
            dayInfo.day = info;
            dayInfo.dayType = DayType.WEEKEND;
            dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
            DayInfo.persist(dayInfo);
        }

        for( var info :yearInfo.preHolidays){
            DayInfo dayInfo = DayInfo.getByDate(info, CONSULTANT_SOURCE_TYPE).orElse(new DayInfo());
            dayInfo.day = info;
            dayInfo.dayType = DayType.PRE_HOLIDAY;
            dayInfo.sourceType = CONSULTANT_SOURCE_TYPE;
            DayInfo.persist(dayInfo);
        }

        return Response.ok(yearInfo).build();
    }
}

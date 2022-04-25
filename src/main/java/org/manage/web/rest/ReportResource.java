package org.manage.web.rest;

import org.manage.security.AuthoritiesConstants;
import org.manage.service.MailService;
import org.manage.service.ReportService;
import org.manage.service.dto.DayReportDTO;
import org.manage.service.dto.TimeLogReportDTO;
import org.manage.web.util.ResponseUtil;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Path("/api/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ReportResource {

    private final ReportService reportService;
    private final MailService mailService;

    @Inject
    public ReportResource(ReportService reportService, MailService mailService) {
        this.reportService = reportService;
        this.mailService = mailService;
    }

    @POST
    @Path("day-report/{id}")
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public CompletionStage<Response> sendDayReport(@PathParam("id") Long projectId, @QueryParam("dateFrom") LocalDate dateFrom, @QueryParam("dateTo") LocalDate dateTo) {
        final DayReportDTO dayReportDTO = reportService.generateReport(projectId, dateFrom, dateTo);
        return mailService.sendDayReport(dayReportDTO)
            .thenApply(x -> Response.accepted().build());
    }

    @GET
    @Path("project/{id}")
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public Response projectReport(@PathParam("id") Long projectId, @QueryParam("dateFrom") LocalDate dateFrom, @QueryParam("dateTo") LocalDate dateTo) {
        return Response.ok(reportService.generateReport(projectId, dateFrom, dateTo))
            .build();
    }

    @GET
    @Path("time-logs")
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public Response timeLogReport(@QueryParam("dateFrom") LocalDate dateFrom, @QueryParam("dateTo") LocalDate dateTo) {
        return Response.ok(reportService.generateTimeLogReport(dateFrom, dateTo))
            .build();
    }

    @POST
    @Path("time-report")
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public CompletionStage<Response> sendTimeReport(@QueryParam("to") List<String> to, @QueryParam("dateFrom") LocalDate dateFrom, @QueryParam("dateTo") LocalDate dateTo) {
        final TimeLogReportDTO reportDTO = reportService.generateTimeLogReport(dateFrom, dateTo);
        return mailService.sendTimeReport(reportDTO, to)
            .thenApply(x -> Response.accepted().build());
    }

    @GET
    @Path("two-week-registered-time-report/{user}")
    public Response twoWeekRegisteredTimeReport(@PathParam("user") String login) {
        final LocalDate from = LocalDate.now().minusDays(14);
        final LocalDate to = LocalDate.now();
        return ResponseUtil.wrapOrNotFound(reportService.getRegisteredTimeReport(login, from, to));
    }
}

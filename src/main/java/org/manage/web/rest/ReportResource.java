package org.manage.web.rest;

import org.manage.service.MailService;
import org.manage.service.ReportService;
import org.manage.service.dto.DayReportDTO;
import org.manage.service.dto.DayReportRequestModel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("day-report")
    public CompletionStage<Response> sendDayReport(@Valid DayReportRequestModel reportRequest) {
        final DayReportDTO dayReportDTO = reportService.generateDayReport(reportRequest.projectId, reportRequest.reportDate);
        return mailService.sendDayReport(dayReportDTO)
            .thenApply(x -> Response.accepted().build());
    }

}

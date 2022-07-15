package org.manage.web.rest;

import io.quarkus.tika.TikaParser;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/calendar/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CalendarResource {

    @Inject
    TikaParser parser;

    @GET
    public Response calendar(){

        return Response.ok("").build();
    }

}

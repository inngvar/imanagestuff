package org.manage.service.restclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("law/ref/calendar/proizvodstvennye")
@RegisterRestClient(configKey = "consultant")
public interface ConsultantClient {

    @GET
    @Path("/{year}")
    String calendar(@PathParam("year") String year);
}

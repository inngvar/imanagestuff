package org.manage.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.manage.client.dto.SendMessageRequest;
import org.manage.client.dto.Update;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/bot{token}")
@RegisterRestClient(configKey = "telegram-bot-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TelegramBotClient {

    @GET
    @Path("/getUpdates")
    List<Update> getUpdates(
        @PathParam("token") String token,
        @QueryParam("offset") Long offset,
        @QueryParam("timeout") Integer timeout);

    @POST
    @Path("/sendMessage")
    void sendMessage(
        @PathParam("token") String token,
        SendMessageRequest request);
}

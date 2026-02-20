package org.manage.web.rest;

import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.manage.domain.Member;
import org.manage.domain.PendingLink;
import org.manage.service.TelegramLinkService;
import org.manage.service.dto.TelegramLinkDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Optional;

@Path("/api/telegram")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class TelegramResource {

    private final Logger log = LoggerFactory.getLogger(TelegramResource.class);

    @ConfigProperty(name = "telegram.bot.username")
    String botUsername;

    @Inject
    TelegramLinkService telegramLinkService;

    @POST
    @Path("/generate-link")
    @Authenticated
    public Response generateLink(@Context SecurityContext ctx) {
        log.debug("REST request to generate telegram link");
        String login = ctx.getUserPrincipal().getName();
        return Member.findByLogin(login)
            .map(member -> {
                PendingLink pendingLink = telegramLinkService.generateLinkCode(member);
                String link = String.format("https://t.me/%s?start=%s", botUsername, pendingLink.code);
                return Response.ok(new TelegramLinkDTO(link, pendingLink.expiresAt)).build();
            })
            .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).build());
    }
}

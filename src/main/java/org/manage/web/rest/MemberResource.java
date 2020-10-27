package org.manage.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import io.quarkus.security.identity.SecurityIdentity;
import org.manage.domain.Member;
import org.manage.security.AuthoritiesConstants;
import org.manage.service.MemberService;
import org.manage.web.rest.errors.BadRequestAlertException;
import org.manage.web.util.HeaderUtil;
import org.manage.web.util.ResponseUtil;
import org.manage.service.dto.MemberDTO;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.manage.service.Paged;
import org.manage.web.rest.vm.PageRequestVM;
import org.manage.web.util.PaginationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.manage.domain.Member}.
 */
@Path("/api/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MemberResource {

    private final Logger log = LoggerFactory.getLogger(MemberResource.class);

    private static final String ENTITY_NAME = "member";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    MemberService memberService;
    /**
     * {@code POST  /members} : Create a new member.
     *
     * @param memberDTO the memberDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new memberDTO, or with status {@code 400 (Bad Request)} if the member has already an ID.
     */
    @POST
    public Response createMember(@Valid MemberDTO memberDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save Member : {}", memberDTO);
        if (memberDTO.id != null) {
            throw new BadRequestAlertException("A new member cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = memberService.persistOrUpdate(memberDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /members} : Updates an existing member.
     *
     * @param memberDTO the memberDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated memberDTO,
     * or with status {@code 400 (Bad Request)} if the memberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the memberDTO couldn't be updated.
     */
    @PUT
    public Response updateMember(@Valid MemberDTO memberDTO) {
        log.debug("REST request to update Member : {}", memberDTO);
        if (memberDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = memberService.persistOrUpdate(memberDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, memberDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /members/:id} : delete the "id" member.
     *
     * @param id the id of the memberDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteMember(@PathParam("id") Long id) {
        log.debug("REST request to delete Member : {}", id);
        memberService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /members} : get all the members.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of members in body.
     */
    @GET
    public Response getAllMembers(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of Members");
        var page = pageRequest.toPage();
        Paged<MemberDTO> result = memberService.findAll(page);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }


    /**
     * {@code GET  /members/:id} : get the "id" member.
     *
     * @param id the id of the memberDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the memberDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")

    public Response getMember(@PathParam("id") Long id) {
        log.debug("REST request to get Member : {}", id);
        Optional<MemberDTO> memberDTO = memberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(memberDTO);
    }

    /**
     * Return current user as a member
     *
     * @return
     */
    @GET
    @Path("current")
    public Response getCurrentMember() {
        final String login = securityIdentity.getPrincipal().getName();
        Optional<Member> currentMember = Member.findByLogin(login);
        Optional<MemberDTO> memberDTO = memberService.findOne(currentMember.get().id);
        return Response.ok(memberDTO).build();
    }

    /**
     * Return current user as a member
     *
     * @return
     */
    @GET
    @Path("isAdmin")
    public Response isAdmin() {
        return Response.ok(securityIdentity.getRoles().contains(AuthoritiesConstants.ADMIN)).build();
    }

}

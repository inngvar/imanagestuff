package org.manage.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import io.quarkus.security.identity.SecurityIdentity;
import org.manage.domain.Member;
import org.manage.security.AuthoritiesConstants;
import org.manage.service.member.MemberService;
import org.manage.service.project.ProjectService;
import org.manage.web.rest.errors.BadRequestAlertException;
import org.manage.web.util.HeaderUtil;
import org.manage.web.util.ResponseUtil;
import org.manage.service.dto.ProjectDTO;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.manage.service.util.Paged;
import org.manage.web.rest.vm.PageRequestVM;
import org.manage.web.util.PaginationUtil;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.manage.domain.Project}.
 */
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "project";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    ProjectService projectService;

    @Inject
    MemberService memberService;

    /**
     * {@code POST  /projects} : Create a new project.
     *
     * @param projectDTO the projectDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new projectDTO, or with status {@code 400 (Bad Request)} if the project has already an ID.
     */
    @POST
    @RolesAllowed({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
    public Response createProject(@Valid ProjectDTO projectDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.id != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = projectService.persistOrUpdate(projectDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /projects} : Updates an existing project.
     *
     * @param projectDTO the projectDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     */
    @PUT
    @RolesAllowed({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
    public Response updateProject(@Valid ProjectDTO projectDTO) {
        log.debug("REST request to update Project : {}", projectDTO);
        if (projectDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = projectService.persistOrUpdate(projectDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /projects/:id} : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
    public Response deleteProject(@PathParam("id") Long id) {
        log.debug("REST request to delete Project : {}", id);
        projectService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param pageRequest the pagination information.
     * @param eagerload   flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link Response} with status {@code 200 (OK)} and the list of projects in body.
     */
    @GET
    @RolesAllowed({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
    public Response getAllProjects(@BeanParam PageRequestVM pageRequest, @Context UriInfo uriInfo, @QueryParam(value = "eagerload") boolean eagerload) {
        log.debug("REST request to get a page of Projects");
        var page = pageRequest.toPage();
        Paged<ProjectDTO> result;
        if (eagerload) {
            result = projectService.findAllWithEagerRelationships(page);
        } else {
            result = projectService.findAll(page);
        }
        var response = Response.ok().entity(result.content);
        var response1 = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response1.build();
    }


    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the projectDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
    public Response getProject(@PathParam("id") Long id) {
        log.debug("REST request to get Project : {}", id);
        Optional<ProjectDTO> projectDTO = projectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * Return list of projects where current user is a member
     *
     * @return
     */
    @GET
    @Path("current")
    @RolesAllowed({AuthoritiesConstants.ADMIN,AuthoritiesConstants.USER})
    public Response getUserProjects() {
        final String login = securityIdentity.getPrincipal().getName();
        List<ProjectDTO> result = projectService.findByLogin(login);
        return Response.ok(result).build();
    }

    /**
     * Return list of projects by member
     *
     * @return
     */
    @GET
    @Path("memberid/{id}")
    public Response getProjectsForUserId(@PathParam("id") Long id) {
        final String login = memberService.findOne(id).get().login;
        List<ProjectDTO> result = projectService.findByLogin(login);
        return Response.ok(result).build();
    }
}

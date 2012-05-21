package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 *
 * Provides read access to SecurityEvents through the /securityEvent path.
 *
 * @author ldalgado
 */
@Component
@Scope("request")
@Path("securityEvent")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class SecurityEventResource extends DefaultCrudEndpoint {

    public static final String UUID = "uuid";
    public static final String RESOURCE_NAME = "securityEvent";

    /* Access to entity definitions */
    private final EntityDefinitionStore entityDefs;

    @Autowired
    public SecurityEventResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        this.entityDefs = entityDefs;
    }

    @POST
    public Response createSecurityEvent(EntityBody newSecurityEvent, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
//            if (principal != null) {
//                System.out.println("tenant: " + principal.getTenantId());
//                System.out.println("realm: " + principal.getRealm());
//                System.out.println("AdminRealm: " + principal.getAdminRealm());
//                for (String role : principal.getRoles()) {
//                    System.out.println("role: " + role);
//                }
//
//                Set<String> sliRoles = new HashSet<String>(principal.getSliRoles());
//
//                for (String role : sliRoles) {
//                    System.out.println("sliRole: " + role);
//                }
//            }
//        }

        return super.create(newSecurityEvent, headers, uriInfo);
    }

    @SuppressWarnings("rawtypes")
    @GET
    public Response getSecurityEvents(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
            if (principal != null) {

                Set<String> sliRoles = new HashSet<String>(principal.getSliRoles());

                if (sliRoles.contains(RoleInitializer.SLC_OPERATOR)) {
                    return getEntities(offset, limit, uriInfo, principal, true);
                } else if (sliRoles.contains(RoleInitializer.SEA_ADMINISTRATOR)
                        || sliRoles.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
                    return getEntities(offset, limit, uriInfo, principal, false);
                }
            }
        }

        Status errorStatus = Status.FORBIDDEN;
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), Status.FORBIDDEN.getReasonPhrase(),
                        "You have no permission to see it!")).build();
    }

    private Response getEntities(final int offset, final int limit, final UriInfo uriInfo, SLIPrincipal principal,
            boolean isSLCOperator) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(RESOURCE_NAME);
        if (entityDef == null) {
            return Response
                    .status(Status.NOT_FOUND)
                    .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                            "Invalid resource path: " + RESOURCE_NAME)).build();
        }

        NeutralQuery neutralQuery = new ApiQuery(uriInfo);
        neutralQuery = addTypeCriteria(entityDef, neutralQuery);
        neutralQuery.setLimit(limit);
        neutralQuery.setOffset(offset);
        if (!isSLCOperator) {
            List<String> targetEdOrgs = Arrays.asList(principal.getEdOrg().split(","));
            neutralQuery.addCriteria(new NeutralCriteria("targetEdOrg", "in", targetEdOrgs));
        }

        // a new list to store results
        List<EntityBody> results = new ArrayList<EntityBody>();

        // list all entities matching query parameters and iterate over results
        for (EntityBody entityBody : entityDef.getService().list(neutralQuery)) {
            entityBody.put(ResourceConstants.LINKS,
                    ResourceUtil.getLinks(entityDefs, entityDef, entityBody, uriInfo));

            // add entity to resulting response
            results.add(entityBody);
        }

        if (results.isEmpty()) {
            Status errorStatus = Status.NOT_FOUND;
            return Response
                    .status(errorStatus)
                    .entity(new ErrorResponse(errorStatus.getStatusCode(), Status.NOT_FOUND
                            .getReasonPhrase(), "Entity not found")).build();
        } else {
            return Response.ok(new EntityResponse(entityDef.getType(), results)).build();
        }
    }

    @SuppressWarnings("rawtypes")
    @GET
    @Path("{" + UUID + "}")
    public Response getSecurityEvent(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.OK).build();
    }

    @DELETE
    @Path("{" + UUID + "}")
    public Response deleteSecurityEvent(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.FORBIDDEN).build();
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{" + UUID + "}")
    public Response updateSecurityEventn(@PathParam(UUID) String uuid, EntityBody app,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.FORBIDDEN).build();
    }
}

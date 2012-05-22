package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import org.slc.sli.domain.NeutralQuery.SortOrder;

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
    public static final List<String> WATCHED_APP = Arrays.asList("SimpleIDP");

    /* Access to entity definitions */
    private final EntityDefinitionStore entityDefs;

    @Autowired
    public SecurityEventResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        this.entityDefs = entityDefs;
    }

    /**
     * Create a $$securityEvent$$ entity
     * @param newSecurityEvent
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of the create operation
     */
    @POST
    public Response createSecurityEvent(EntityBody newSecurityEvent, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.create(newSecurityEvent, headers, uriInfo);
    }

    /**
     * retrieve a list of security events
     * /api/rest/securityEvent
     *
     * @param offset
     *           the starting position in the results to return to user, the default value is 0
     * @param limit
     *           the maximum number of security events to return to user (starting from offset), the default value is ParameterConstants.DEFAULT_LIMIT (50)
     * @param headers
     *           HTTP Request Headers
     * @param uriInfo
     *           URI information including path and query parameters
     * @return a list of security events that are sorted by timestamp in descending order
     */
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
                    return retrieveEntities(offset, limit, uriInfo, principal, RoleInitializer.SLC_OPERATOR);
                } else if (sliRoles.contains(RoleInitializer.SEA_ADMINISTRATOR)) {
                    return retrieveEntities(offset, limit, uriInfo, principal, RoleInitializer.SEA_ADMINISTRATOR);
                } else if (sliRoles.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
                    return retrieveEntities(offset, limit, uriInfo, principal, RoleInitializer.LEA_ADMINISTRATOR);
                }
            }
        }

        Status errorStatus = Status.FORBIDDEN;
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), Status.FORBIDDEN.getReasonPhrase(),
                        "You have no permission to see it!")).build();
    }

//    @GET
//    @Path("{" + UUID + "}")
//    public Response getSecurityEvent(@PathParam(UUID) String uuid, @Context HttpHeaders headers,
//            @Context final UriInfo uriInfo) {
//        return Response.status(Status.OK).build();
//    }
//
//    @DELETE
//    @Path("{" + UUID + "}")
//    public Response deleteSecurityEvent(@PathParam(UUID) String uuid, @Context HttpHeaders headers,
//            @Context final UriInfo uriInfo) {
//        return Response.status(Status.FORBIDDEN).build();
//    }
//
//    @PUT
//    @Path("{" + UUID + "}")
//    public Response updateSecurityEventn(@PathParam(UUID) String uuid, EntityBody app, @Context HttpHeaders headers,
//            @Context final UriInfo uriInfo) {
//        return Response.status(Status.FORBIDDEN).build();
//    }


    private Response retrieveEntities(final int offset, final int limit, final UriInfo uriInfo, SLIPrincipal principal,
            String role) {
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

        // by default the result is sorted by time stamp, the newer event comes earlier
        neutralQuery.setSortBy("timeStamp");
        neutralQuery.setSortOrder(SortOrder.descending);

        // only retrieve watched apps
        neutralQuery.addCriteria(new NeutralCriteria("appId", NeutralCriteria.CRITERIA_IN, WATCHED_APP));

        if (role.equals(RoleInitializer.SEA_ADMINISTRATOR) || role.equals(RoleInitializer.LEA_ADMINISTRATOR)) {

            // set EdOrg filter
            List<String> targetEdOrgs = Arrays.asList(principal.getEdOrg().split(","));
            neutralQuery.addCriteria(new NeutralCriteria("targetEdOrg", NeutralCriteria.CRITERIA_IN, targetEdOrgs));

            // set role filter
            List<String> roles = null;
            if (role.equals(RoleInitializer.SEA_ADMINISTRATOR)) {
                roles = Arrays.asList(RoleInitializer.SEA_ADMINISTRATOR, RoleInitializer.REALM_ADMINISTRATOR);
            } else if (role.equals(RoleInitializer.LEA_ADMINISTRATOR)) {
                roles = Arrays.asList(RoleInitializer.LEA_ADMINISTRATOR);
            }

            if (roles != null) {
                neutralQuery.addCriteria(new NeutralCriteria("roles", NeutralCriteria.CRITERIA_IN, roles));
            }
        }

        // a new list to store results
        List<EntityBody> results = new ArrayList<EntityBody>();

        // list all entities matching query parameters and iterate over results
        for (EntityBody entityBody : entityDef.getService().list(neutralQuery)) {
            entityBody.put(ResourceConstants.LINKS, ResourceUtil.getLinks(entityDefs, entityDef, entityBody, uriInfo));

            // add entity to resulting response
            results.add(entityBody);
        }

        if (results.isEmpty()) {
            Status errorStatus = Status.NOT_FOUND;
            return Response
                    .status(errorStatus)
                    .entity(new ErrorResponse(errorStatus.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                            "Entity not found")).build();
        } else {
            return Response.ok(new EntityResponse(entityDef.getType(), results)).build();
        }
    }

}

package org.slc.sli.api.resources.security;

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
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.common.constants.v1.ParameterConstants;

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


    @Autowired
    public SecurityEventResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
    }

    @POST
    public Response createSecurityEvent(EntityBody newSecurityEvent, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.create(newSecurityEvent, headers, uriInfo);
    }

    @SuppressWarnings("rawtypes")
    @GET
    public Response getSecurityEvents(@QueryParam(ParameterConstants.OFFSET)
            @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context
            HttpHeaders headers, @Context final UriInfo uriInfo) {
        Response resp = super.readAll(offset, limit, headers, uriInfo);
        return resp;
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

package org.slc.sli.api.resources.tenant;

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
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.enums.Right;

/**
 *
 * Provides CRUD operations on registered application through the /tenants path.
 *
 * @author
 */
@Component
@Scope("request")
@Path("tenants")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class TenantResource extends DefaultCrudEndpoint {

    @Autowired
    private EntityDefinitionStore store;

    public static final String UUID = "uuid";
    public static final String RESOURCE_NAME = "tenant";
    public static final String TENANT_ID = "tenantId";
    public static final String LZ_EDUCATION_ORGANIZATION = "educationOrganization";
    public static final String LZ_INGESTION_SERVER = "ingestionServer";
    public static final String LZ_PATH = "path";
    public static final String LZ_USER_NAMES = "userNames";
    public static final String LZ_DESC = "desc";
    //TODO: validate LandingZone data lengths, etc?

    @Autowired
    public TenantResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, RESOURCE_NAME);
        store = entityDefs;
        store.lookupByResourceName(RESOURCE_NAME).getService();
    }

    @POST
    public Response createTenant(EntityBody newTenant, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to provision tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        //TODO: look up the tenantId; if it exists, add new LZs here to the existing list; otherwise, create

        return super.create(newTenant, headers, uriInfo);
    }

    @GET
    public Response getTenants(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context
            HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Looks up a specific application based on client ID, ie.
     * /api/rest/tenants/<tenantId>
     *
     * @param tenantId
     *            the client ID, not the "id"
     * @return the JSON data of the application, otherwise 404 if not found
     */
    @GET
    @Path("{" + UUID + "}")
    public Response getTenant(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.read(uuid, headers, uriInfo);
    }

    @DELETE
    @Path("{" + UUID + "}")
    public Response deleteTenant(@PathParam(UUID) String uuid,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to delete tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.delete(uuid, headers, uriInfo);
    }

    @PUT
    @Path("{" + UUID + "}")
    public Response updateTenant(@PathParam(UUID) String uuid, EntityBody tenant,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to provision tenants or landing zones.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        return super.update(uuid, tenant, headers, uriInfo);
    }

}

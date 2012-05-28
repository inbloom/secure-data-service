package org.slc.sli.api.resources.v1;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Subresource for custom entities
 *
 */
public class CustomEntityResource {

    String entityId;
    EntityDefinition entityDef;

    public CustomEntityResource(String entityId, EntityDefinition entityDef) {
        this.entityId = entityId;
        this.entityDef = entityDef;
    }

    /**
     * Read the contents of the custom resource for the given entity.
     *
     * @return the response to the GET request
     */
    @GET
    @Path("/")
    public Response read() {
        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        EntityBody entityBody = entityDef.getService().getCustom(entityId);
        if (entityBody == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.OK).entity(entityBody).build();
    }

    /**
     * Set the contents of the custom resource for the given entity.
     *
     * @param customEntity
     *            the new entity to set
     * @return the response to the PUT request
     */
    @PUT
    @Path("/")
    public Response createOrUpdatePut(EntityBody customEntity) {
        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDef.getService().createOrUpdateCustom(entityId, customEntity);
        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Set the contents of the custom resource for the given entity.
     *
     * @param customEntity
     *            the new entity to set
     * @return the response to the POST request
     */
    @POST
    @Path("/")
    public Response createOrUpdatePost(EntityBody customEntity, @Context UriInfo uriInfo) {
        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDef.getService().createOrUpdateCustom(entityId, customEntity);
        String uri = ResourceUtil.getURI(uriInfo, PathConstants.V1,
                PathConstants.TEMP_MAP.get(entityDef.getResourceName()), entityId, PathConstants.CUSTOM_ENTITIES)
                .toString();
        return Response.status(Status.CREATED).header("Location", uri).build();
    }

    /**
     * Remove the custom resource for the given entity.
     *
     * @return the response tot he DELETE request
     */
    @DELETE
    @Path("/")
    public Response delete() {
        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        entityDef.getService().deleteCustom(entityId);
        return Response.status(Status.NO_CONTENT).build();
    }
}

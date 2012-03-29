package org.slc.sli.api.resources.v1;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;

/**
 * Subresource for custom entities
 * 
 */
@Resource
public class CustomEntityResource {
    
    String entityId;
    EntityDefinition entityDef;
    
    public CustomEntityResource(String entityId, EntityDefinition entityDef) {
        this.entityId = entityId;
        this.entityDef = entityDef;
    }
    
    @GET
    @Path("/")
    public Response read() {
        EntityBody entityBody = entityDef.getService().getCustom(entityId);
        return Response.status(Status.OK).entity(entityBody).build();
    }
    
    @PUT
    @POST
    @Path("/")
    public Response createOrUpdate(EntityBody customEntity) {
        entityDef.getService().createOrUpdateCustom(entityId, customEntity);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    @DELETE
    @Path("/")
    public Response delete() {
        entityDef.getService().deleteCustom(entityId);
        return Response.status(Status.NO_CONTENT).build();
    }
}

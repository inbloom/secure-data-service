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
        return Response.status(Status.NOT_FOUND).build();
    }
    
    @PUT
    @POST
    @Path("/")
    public Response createOrUpdate() {
        return Response.status(Status.NOT_FOUND).build();
    }
    
    @DELETE
    @Path("/")
    public Response delete() {
        return Response.status(Status.NOT_FOUND).build();
    }
}

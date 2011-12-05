package org.slc.sli.api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.CollectionResponse;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.service.EntityNotFoundException;

@Path("new-api/{type}")
@Component
@Scope("request")
@Produces({ ResourceUtilities.XML_MEDIA_TYPE, ResourceUtilities.JSON_MEDIA_TYPE })
public class Resource {
    
    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);
    final EntityDefinitionStore entityDefs;
    
    @Autowired
    Resource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }
    
    /* REST methods */
    
    @GET
    public Response getCollection(@PathParam("type") final String typePath,
            @QueryParam("start-index") @DefaultValue("0") final int skip,
            @QueryParam("max-results") @DefaultValue("50") final int max) {
        
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                List<String> ids = iterableToList(entityDef.getService().list(skip, max));
                CollectionResponse collection = new CollectionResponse();
                for (String id : ids) {
                    String href = UriBuilder.fromResource(Resource.class).path(id).build(entityDef.getResourceName())
                            .toString();
                    collection.add(id, "self", entityDef.getType(), href);
                }
                
                return Response.ok(collection).build();
            }
        });
    }
    
    @POST
    public Response createEntity(@PathParam("type") final String typePath, final EntityBody newEntityBody) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                String id = entityDef.getService().create(newEntityBody);
                String uri = UriBuilder.fromResource(Resource.class).path(id).build(entityDef.getResourceName())
                        .toString();
                return Response.status(Status.CREATED).header("Location", uri).build();
            }
        });
    }
    
    @GET
    @Path("{id}")
    public Response getEntityOrAssociations(@PathParam("type") final String typePath, @PathParam("id") final String id) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                EntityBody entityBody = entityDef.getService().get(id);
                // TODO: add headers
                return Response.ok(entityBody).build();
            }
        });
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteEntity(@PathParam("type") final String typePath, @PathParam("id") final String id) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                entityDef.getService().delete(id);
                return Response.status(Status.NO_CONTENT).build();
            }
        });
    }
    
    @PUT
    @Path("{id}")
    public Response updateEntity(@PathParam("type") final String typePath, @PathParam("id") final String id,
            final EntityBody newEntityBody) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                entityDef.getService().update(id, newEntityBody);
                return Response.status(Status.NO_CONTENT).build();
            }
        });
    }
    
    /* Utility methods */
    
    private Response handle(String typePath, ResourceLogic logic) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(typePath);
        if (entityDef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        try {
            return logic.run(entityDef);
        } catch (EntityNotFoundException e) {
            LOG.error("Entity not found", e);
            return Response.status(Status.NOT_FOUND)
                    .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase()))
                    .build();
        } catch (Throwable t) {
            LOG.error("Error handling request", t);
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            Status.INTERNAL_SERVER_ERROR.getReasonPhrase())).build();
        }
    }
    
    private static interface ResourceLogic {
        Response run(EntityDefinition entityDef);
    }
    
    private static <T> List<T> iterableToList(Iterable<T> iter) {
        List<T> list = new ArrayList<T>();
        for (T value : iter) {
            list.add(value);
        }
        return list;
    }
}

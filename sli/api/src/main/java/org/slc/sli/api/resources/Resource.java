package org.slc.sli.api.resources;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.CollectionResponse;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.ErrorResponse;

/**
 * Jersey resource for all entities and associations.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Path("{type}")
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE })
public class Resource {
    
    private static final String SELF_LINK = "self";
    private static final String LINKS_ELEM = "links";
    public static final String XML_MEDIA_TYPE = MediaType.APPLICATION_XML;
    public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    
    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);
    final EntityDefinitionStore entityDefs;
    
    /**
     * Encapsulates each ReST method's logic to allow for less duplication of precondition and
     * exception
     * handling code.
     */
    private static interface ResourceLogic {
        Response run(EntityDefinition entityDef);
    }
    
    @Autowired
    Resource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }
    
    /* REST methods */
    
    /**
     * Create a new entity or association.
     * 
     * @param typePath
     *            resourceUri for the entity
     * @param newEntityBody
     *            entity data
     * @return Response with a status of CREATED and a Location header set pointing to where the new
     *         entity lives
     */
    @POST
    public Response createEntity(@PathParam("type") final String typePath, final EntityBody newEntityBody,
            @Context final UriInfo uriInfo) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                String id = entityDef.getService().create(newEntityBody);
                String uri = getURI(uriInfo, entityDef.getResourceName(), id).toString();
                return Response.status(Status.CREATED).header("Location", uri).build();
            }
        });
    }
    
    /**
     * Return either a single entity or a collection of entities depending on the combination of the
     * typePath and the type of entity the id references.
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or one of the association's source source ids.
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @return A single entity or association, unless the type references an association and the id
     *         represents the source entity. In that case a collection of associations.
     */
    @GET
    @Path("{id}")
    public Response getEntity(@PathParam("type") final String typePath, @PathParam("id") final String id,
            @QueryParam("start-index") @DefaultValue("0") final int skip,
            @QueryParam("max-results") @DefaultValue("50") final int max, @Context final UriInfo uriInfo) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                if (entityDef.isOfType(id)) {
                    EntityBody entityBody = entityDef.getService().get(id);
                    entityBody.put(LINKS_ELEM, getLinks(uriInfo, entityDef, id, entityBody));
                    return Response.ok(entityBody).build();
                } else if (entityDef instanceof AssociationDefinition) {
                    AssociationDefinition associationDefinition = (AssociationDefinition) entityDef;
                    Iterable<String> associationIds = null;
                    if (associationDefinition.getSourceEntity().isOfType(id)) {
                        associationIds = associationDefinition.getService().getAssociatedWith(id, skip, max);
                    } else if (associationDefinition.getTargetEntity().isOfType(id)) {
                        associationIds = associationDefinition.getService().getAssociatedTo(id, skip, max);
                    }
                    if (associationIds != null) {
                        CollectionResponse collection = new CollectionResponse();
                        for (String id : associationIds) {
                            String href = getURI(uriInfo, entityDef.getResourceName(), id).toString();
                            collection.add(id, SELF_LINK, entityDef.getType(), href);
                        }
                        return Response.ok(collection).build();
                    }
                }
                return Response.status(Status.NOT_FOUND).build();
            }
        });
    }
    
    /**
     * Delete an entity or association
     * 
     * @param typePath
     *            resourceUri of the entity
     * @param id
     *            id of the entity
     * @return Returns a NOT_CONTENT status code
     */
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
    
    /**
     * Update an existing entity or association.
     * 
     * @param typePath
     *            resourceUri for the entity
     * @param id
     *            id of the entity
     * @param newEntityBody
     *            entity data that will used to replace the existing entity data
     * @return Response with a NOT_CONTENT status code
     */
    @PUT
    @Path("{id}")
    public Response updateEntity(@PathParam("type") final String typePath, @PathParam("id") final String id,
            final EntityBody newEntityBody) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                EntityBody copy = new EntityBody(newEntityBody);
                copy.remove(LINKS_ELEM);
                LOG.debug("updating entity {}", copy);
                entityDef.getService().update(id, copy);
                LOG.debug("updating entity {}", copy);
                return Response.status(Status.NO_CONTENT).build();
            }
        });
    }
    
    /* Utility methods */
    
    /**
     * Handle preconditions and exceptions.
     */
    private Response handle(String typePath, ResourceLogic logic) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(typePath);
        if (entityDef == null) {
            return Response
                    .status(Status.NOT_FOUND)
                    .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                            "Invalid resource path: " + typePath)).build();
        }
        return logic.run(entityDef);
    }
    
    /**
     * Gets the links that should be included for the given resource
     * 
     * @param uriInfo
     *            the uri info for the request
     * @param defn
     *            the definition of the resource to look up the links for
     * @param id
     *            the id of the resource to include in the links
     * @param entityBody
     *            the entity making the links for
     * @return the list of links that the resource should include
     */
    private List<EmbeddedLink> getLinks(UriInfo uriInfo, EntityDefinition defn, String id, EntityBody entityBody) {
        List<EmbeddedLink> links = new LinkedList<EmbeddedLink>();
        links.add(new EmbeddedLink(SELF_LINK, defn.getType(), getURI(uriInfo, defn.getResourceName(), id).toString()));
        if (defn instanceof AssociationDefinition) {
            AssociationDefinition assocDef = (AssociationDefinition) defn;
            EntityDefinition sourceEntity = assocDef.getSourceEntity();
            links.add(new EmbeddedLink(assocDef.getSourceLink(), sourceEntity.getType(), getURI(uriInfo,
                    sourceEntity.getResourceName(), (String) entityBody.get(assocDef.getSourceKey())).toString()));
            EntityDefinition targetEntity = assocDef.getTargetEntity();
            links.add(new EmbeddedLink(assocDef.getTargetLink(), targetEntity.getType(), getURI(uriInfo,
                    targetEntity.getResourceName(), (String) entityBody.get(assocDef.getTargetKey())).toString()));
        } else {
            Collection<AssociationDefinition> associations = entityDefs.getLinked(defn);
            for (AssociationDefinition assoc : associations) {
                if (assoc.getSourceEntity().equals(defn)) {
                    links.add(new EmbeddedLink(assoc.getRelNameFromSource(), assoc.getType(), getURI(uriInfo,
                            assoc.getResourceName(), id).toString()));
                }
                if (assoc.getTargetEntity().equals(defn)) {
                    links.add(new EmbeddedLink(assoc.getRelNameFromTarget(), assoc.getType(), getURI(uriInfo,
                            assoc.getResourceName(), id).toString()));
                }
            }
        }
        return links;
    }
    
    private URI getURI(UriInfo uriInfo, String type, String id) {
        return uriInfo.getBaseUriBuilder().path(type).path(id).build();
    }
    
}

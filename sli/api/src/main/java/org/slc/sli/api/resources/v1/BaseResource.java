package org.slc.sli.api.resources.v1;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.CollectionResponse;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.resources.util.ResourceConstants;
import org.slc.sli.api.resources.util.ResourceUtil;

/**
 * Prototype new api end points and versioning base class
 * 
 * @author srupasinghe
 * 
 */
public class BaseResource implements CrudEndpoint {
    private String typePath = "";
    
    protected static final String FULL_ENTITIES_PARAM = "full-entities";
    public static final String XML_MEDIA_TYPE = MediaType.APPLICATION_XML;
    public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    public static final String SLC_XML_MEDIA_TYPE = "application/vnd.slc+xml";
    public static final String SLC_JSON_MEDIA_TYPE = "application/vnd.slc+json";
    public static final String SLC_LONG_XML_MEDIA_TYPE = "application/vnd.slc.full+xml";
    public static final String SLC_LONG_JSON_MEDIA_TYPE = "application/vnd.slc.full+json";
    
    public static final int MAX_MULTIPLE_UUIDS = 100;
    
    private static final Logger LOG = LoggerFactory.getLogger(BaseResource.class);
    private final EntityDefinitionStore entityDefs;
    
    /**
     * Encapsulates each ReST method's logic to allow for less duplication of precondition and
     * exception handling code.
     */
    protected static interface ResourceLogic {
        Response run(EntityDefinition entityDef);
    }
    
    @Autowired
    public BaseResource(EntityDefinitionStore entityDefs, String typePath) {
        if (entityDefs == null) {
            throw new NullPointerException("entityDefs");
        }
        this.entityDefs = entityDefs;
        this.typePath = typePath;
    }
    
    @Override
    public Response readAll(final UriInfo uriInfo) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
    
    /**
     * Create a new entity or association.
     * 
     * @param typePath
     *            resourceUri for the entity
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     * @return Response with a status of CREATED and a Location header set pointing to where the new
     *         entity lives
     * @response.representation.201.mediaType HTTP headers with a Created status code and a Location
     *                                        value.
     */
    public Response create(final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                String id = entityDef.getService().create(newEntityBody);
                String uri = ResourceUtil.getURI(uriInfo, entityDef.getResourceName(), id).toString();
                return Response.status(Status.CREATED).header("Location", uri).build();
            }
        });
    }
    
    /**
     * Get a single entity or association unless the URI represents an association and the id
     * represents a
     * source entity for that association.
     * 
     * @param idList
     *            comma separated list of the association id(s) or the association's source entity
     *            id(s)
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A single entity or association, unless the type references an association and the id
     *         represents the source entity. In that case a collection of associations.
     * @response.representation.200.mediaType application/json
     */
    public Response read(final String idList, final boolean fullEntities, final UriInfo uriInfo) {
        return handle(this.typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                // split list of IDs into individual ID(s)
                String[] ids = idList.split(",");
                boolean multipleIds = (ids.length > 1);
                List<EntityBody> results = new ArrayList<EntityBody>();
                
                // validate the number of input IDs is lower than the max acceptable amount
                if (ids.length > BaseResource.MAX_MULTIPLE_UUIDS) {
                    Response.Status errorStatus = Response.Status.PRECONDITION_FAILED;
                    String errorMessage = "Too many GUIDs: " + ids.length + " (input) vs "
                            + BaseResource.MAX_MULTIPLE_UUIDS + " (allowed)";
                    return Response
                            .status(errorStatus)
                            .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                                    errorMessage)).build();
                }
                
                // loop through all input ID(s)
                for (String id : ids) {
                    // ID is a valid entity from the collection
                    if (entityDef.isOfType(id)) {
                        EntityBody entityBody = entityDef.getService().get(id);
                        entityBody.put(ResourceConstants.LINKS, getLinks(uriInfo, entityDef, id, entityBody));
                        results.add(entityBody);
                    } else if (multipleIds) { // ID not found but multiple IDs are being searched
                                              // for
                        results.add(null);
                    } else { // ID not found and only one ID being searched for
                        return Response.status(Status.NOT_FOUND).build();
                    }
                }
                
                // get a response appropriate for the GET request
                Object responseBodyEntity = multipleIds ? results : results.get(0);
                
                // Return results as an array if multiple IDs were requested (comma separated list),
                // single entity otherwise
                return Response.ok(responseBodyEntity).build();
            }
        });
    }
    
    /**
     * Get the full entities, not just links
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param uriInfo
     * @return A single entity or association, unless the type references an association and the id
     *         represents the source entity. In that case a collection of associations.
     * @response.representation.200.mediaType application/json
     */
    
    public Response getFullEntities(final String id, final UriInfo uriInfo) {
        return read(id, true, uriInfo);
    }
    
    /**
     * Gets the target entities from an association when the source entity is specified for the
     * association.
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param offset
     *            number of results to skip
     * @param limit
     *            maximum number of results to return
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A collection of entities that are the targets of the specified source in an
     *         association
     * @response.representation.200.mediaType application/json
     */
    
    public Response getHoppedRelatives(final String id, final int offset, final int limit, final boolean fullEntities,
            final UriInfo uriInfo) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                if (entityDef instanceof AssociationDefinition) {
                    AssociationDefinition associationDefinition = (AssociationDefinition) entityDef;
                    Iterable<String> relatives = null;
                    EntityDefinition relative = null;
                    if (associationDefinition.getSourceEntity().isOfType(id)) {
                        relatives = associationDefinition.getService().getAssociatedEntitiesWith(id, offset, limit,
                                uriInfo.getRequestUri().getQuery());
                        relative = associationDefinition.getTargetEntity();
                    } else if (associationDefinition.getTargetEntity().isOfType(id)) {
                        relatives = associationDefinition.getService().getAssociatedEntitiesTo(id, offset, limit,
                                uriInfo.getRequestUri().getQuery());
                        relative = associationDefinition.getSourceEntity();
                    } else {
                        return Response.status(Status.NOT_FOUND).build();
                    }
                    
                    if (fullEntities) {
                        return Response.ok(getHoppedEntities(relatives, relative)).build();
                    } else {
                        CollectionResponse collection = getHoppedLinks(uriInfo, relatives, relative);
                        return Response.ok(collection).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            }
            
        });
    }
    
    private CollectionResponse getHoppedLinks(final UriInfo uriInfo, Iterable<String> relatives,
            EntityDefinition relative) {
        CollectionResponse collection = new CollectionResponse();
        if (relatives != null && relatives.iterator().hasNext()) {
            for (String id : relatives) {
                String href = ResourceUtil.getURI(uriInfo, relative.getResourceName(), id).toString();
                collection.add(id, ResourceConstants.SELF, relative.getType(), href);
            }
        }
        return collection;
    }
    
    private Iterable<EntityBody> getHoppedEntities(Iterable<String> relatives, EntityDefinition relativeDef) {
        return relativeDef.getService().get(relatives);
    }
    
    /**
     * Get the full entities, not just links
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param offset
     *            number of results to skip
     * @param limit
     *            maximum number of results to return
     * @param uriInfo
     * @return A collection of entities that are the targets of the specified source in an
     *         association
     * @response.representation.200.mediaType application/json
     */
    
    public Response getFullHoppedRelatives(final String id, final int offset, final int limit, final UriInfo uriInfo) {
        return getHoppedRelatives(id, offset, limit, true, uriInfo);
    }
    
    /**
     * Delete an entity or association
     * 
     * @param typePath
     *            resourceUri of the entity
     * @param id
     *            id of the entity
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    
    public Response delete(final String id) {
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
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    
    public Response update(final String id, final EntityBody newEntityBody) {
        return handle(typePath, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                EntityBody copy = new EntityBody(newEntityBody);
                copy.remove(ResourceConstants.LINKS);
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
        EntityDefinition entityDef = this.entityDefs.lookupByResourceName(typePath);
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
        List<EmbeddedLink> links = ResourceUtil.getSelfLink(uriInfo, id, defn);
        if (defn instanceof AssociationDefinition) {
            AssociationDefinition assocDef = (AssociationDefinition) defn;
            EntityDefinition sourceEntity = assocDef.getSourceEntity();
            links.add(new EmbeddedLink(assocDef.getSourceLink(), sourceEntity.getType(), ResourceUtil.getURI(uriInfo,
                    sourceEntity.getResourceName(), (String) entityBody.get(assocDef.getSourceKey())).toString()));
            EntityDefinition targetEntity = assocDef.getTargetEntity();
            links.add(new EmbeddedLink(assocDef.getTargetLink(), targetEntity.getType(), ResourceUtil.getURI(uriInfo,
                    targetEntity.getResourceName(), (String) entityBody.get(assocDef.getTargetKey())).toString()));
        } else {
            links.addAll(ResourceUtil.getAssociationsLinks(this.entityDefs, defn, id, uriInfo));
            links.addAll(ResourceUtil.getReferenceLinks(uriInfo, this.entityDefs, defn, entityBody));
        }
        return links;
    }
}

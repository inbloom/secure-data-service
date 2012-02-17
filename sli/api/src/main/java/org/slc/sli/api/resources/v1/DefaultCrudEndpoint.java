package org.slc.sli.api.resources.v1;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
class DefaultCrudEndpoint implements CrudEndpoint {
    public static final int MAX_MULTIPLE_UUIDS = 100;
    
    private final EntityDefinitionStore entityDefs;
    private final Logger logger;
    
    /**
     * Encapsulates each ReST method's logic to allow for less duplication of precondition and
     * exception handling code.
     */
    protected static interface ResourceLogic {
        public Response run(EntityDefinition entityDef);
    }

    /**
     * Constructor.
     * 
     * @param entityDefs
     * @param resourceName
     */
    public DefaultCrudEndpoint(final EntityDefinitionStore entityDefs) {
        this(entityDefs, LoggerFactory.getLogger(DefaultCrudEndpoint.class));
    }

    /**
     * Constructor.
     * 
     * @param entityDefs
     * @param resourceName
     * @param log
     */
    public DefaultCrudEndpoint(final EntityDefinitionStore entityDefs, final Logger logger) {
        if (entityDefs == null) {
            throw new NullPointerException("entityDefs");
        }
        if (logger == null) {
            throw new NullPointerException("logger");
        }
        this.entityDefs = entityDefs;
        this.logger = logger;
    }
    
    @Override
    public Response readAll(final String collectionName, final int offset, final int limit, final UriInfo uriInfo) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
    
    public Response create(final String collectionName, final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return handle(collectionName, entityDefs, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                String id = entityDef.getService().create(newEntityBody);
                String uri = ResourceUtil.getURI(uriInfo, entityDef.getResourceName(), id).toString();
                return Response.status(Status.CREATED).header("Location", uri).build();
            }
        });
    }
    
    public Response read(final String collectionName, final String idList, final UriInfo uriInfo) {
        this.logger.debug("URI info: ");
        this.logger.debug("AbPt: " + uriInfo.getAbsolutePath());
        this.logger.debug("Base: " + uriInfo.getBaseUri());
        this.logger.debug("Path: " + uriInfo.getPath());
        this.logger.debug("QPMS: " + uriInfo.getQueryParameters());
        return handle(collectionName, entityDefs, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                // split list of IDs into individual ID(s)
                String[] ids = idList.split(",");
                boolean multipleIds = (ids.length > 1);
                List<EntityBody> results = new ArrayList<EntityBody>();
                
                // validate the number of input IDs is lower than the max acceptable amount
                if (ids.length > DefaultCrudEndpoint.MAX_MULTIPLE_UUIDS) {
                    Response.Status errorStatus = Response.Status.PRECONDITION_FAILED;
                    String errorMessage = "Too many GUIDs: " + ids.length + " (input) vs " + DefaultCrudEndpoint.MAX_MULTIPLE_UUIDS + " (allowed)";
                    return Response.status(errorStatus).entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), errorMessage)).build();
                }
                
                //get query parameters
                MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
                
                //get query parameters for specific fields to include or exclude
                String includeFields = queryParameters.getFirst("includeFields");
                String excludeFields = queryParameters.getFirst("excludeFields");
                
                // loop through all input ID(s)
                for (String id : ids) {
                    // ID is a valid entity from the collection
                    if (entityDef.isOfType(id)) {
                        EntityBody entityBody = entityDef.getService().get(id, includeFields, excludeFields);
                        entityBody.put(ResourceConstants.LINKS, getLinks(uriInfo, entityDef, id, entityBody, entityDefs));
                        results.add(entityBody);
                    } else if (multipleIds) { // ID not found but multiple IDs searched for
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
    
    @SuppressWarnings("unused")
    private static Response getHoppedRelatives(final String resourceName, final EntityDefinitionStore entityDefs,
            final String id, final int offset, final int limit, final boolean fullEntities, final UriInfo uriInfo) {
        return handle(resourceName, entityDefs, new ResourceLogic() {
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
    
    private static CollectionResponse getHoppedLinks(final UriInfo uriInfo, final Iterable<String> relatives,
            final EntityDefinition relative) {
        final CollectionResponse collection = new CollectionResponse();
        // FIXME: Assert that the parameter is not null and don't do the special test.
        if (relatives != null && relatives.iterator().hasNext()) {
            for (final String id : relatives) {
                final String href = ResourceUtil.getURI(uriInfo, relative.getResourceName(), id).toString();
                collection.add(id, ResourceConstants.SELF, relative.getType(), href);
            }
        }
        return collection;
    }
    
    private static Iterable<EntityBody> getHoppedEntities(final Iterable<String> relatives,
            final EntityDefinition relativeDef) {
        return relativeDef.getService().get(relatives);
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
    
    public Response delete(final String collectionName, final String id, final UriInfo uriInfo) {
        return handle(collectionName, entityDefs, new ResourceLogic() {
            @Override
            public Response run(final EntityDefinition entityDef) {
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
    
    public Response update(final String collectionName, final String id, final EntityBody newEntityBody, final UriInfo uriInfo) {
        return handle(collectionName, entityDefs, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                EntityBody copy = new EntityBody(newEntityBody);
                copy.remove(ResourceConstants.LINKS);
                logger.debug("updating entity {}", copy);
                entityDef.getService().update(id, copy);
                logger.debug("updating entity {}", copy);
                return Response.status(Status.NO_CONTENT).build();
            }
        });
    }
    
    /* Utility methods */
    
    /**
     * Handle preconditions and exceptions.
     */
    private static Response handle(final String resourceName, final EntityDefinitionStore entityDefs,
            final ResourceLogic logic) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(resourceName);
        if (entityDef == null) {
            return Response
                    .status(Status.NOT_FOUND)
                    .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                            "Invalid resource path: " + resourceName)).build();
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
    private static List<EmbeddedLink> getLinks(final UriInfo uriInfo, final EntityDefinition defn, final String id,
            final EntityBody entityBody, final EntityDefinitionStore entityDefs) {
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
            links.addAll(ResourceUtil.getAssociationsLinks(entityDefs, defn, id, uriInfo));
            links.addAll(ResourceUtil.getReferenceLinks(uriInfo, entityDefs, defn, entityBody));
        }
        return links;
    }
}

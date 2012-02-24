package org.slc.sli.api.resources;

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
import javax.ws.rs.core.Response.ResponseBuilder;
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
import org.slc.sli.api.service.query.SortOrder;

/**
 * Jersey resource for all entities and associations.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE })
public class Resource {
    
    public static final String TOTAL_COUNT_HEADER = "TotalCount";
    public static final String LINK_HEADER = "Link";
    public static final String MAX_RESULTS_PARAM = "max-results";
    public static final String START_INDEX_PARAM = "start-index";
    public static final String FULL_ENTITIES_PARAM = "full-entities";
    public static final String SORT_BY_PARAM = "sort-by";
    public static final String SORT_ORDER_PARAM = "sort-order";
    public static final String XML_MEDIA_TYPE = MediaType.APPLICATION_XML;
    public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    public static final String SLC_XML_MEDIA_TYPE = "application/vnd.slc+xml";
    public static final String SLC_JSON_MEDIA_TYPE = "application/vnd.slc+json";
    public static final String SLC_LONG_XML_MEDIA_TYPE = "application/vnd.slc.full+xml";
    public static final String SLC_LONG_JSON_MEDIA_TYPE = "application/vnd.slc.full+json";
    
    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);
    private final EntityDefinition entityDef;
    private final EntityDefinitionStore entityDefs;
    
    /**
     * Encapsulates each ReST method's logic to allow for less duplication of precondition and
     * exception
     * handling code.
     */
    private static interface ResourceLogic {
        Response run(EntityDefinition entityDef);
    }
    
    // TODO if we have one resource per entity, this should take in an entity definition, not store
    public Resource(EntityDefinition entityDef, EntityDefinitionStore entityDefs) {
        this.entityDef = entityDef;
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
     * @param uriInfo
     * @return Response with a status of CREATED and a Location header set pointing to where the new
     *         entity lives
     * @response.representation.201.mediaType HTTP headers with a Created status code and a Location
     *                                        value.
     */
    @POST
    public Response createEntity(final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return handle(new ResourceLogic() {
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
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A single entity or association, unless the type references an association and the id
     *         represents the source entity. In that case a collection of associations.
     * @response.representation.200.mediaType application/json
     */
    @GET
    @Path("{id}")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getEntity(@PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return handle(new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                if (entityDef.isOfType(id)) {
                    EntityBody entityBody = entityDef.getService().get(id);
                    entityBody.put(ResourceConstants.LINKS, getLinks(uriInfo, entityDef, id, entityBody));
                    return Response.ok(entityBody).build();
                } else if (entityDef instanceof AssociationDefinition) {
                    AssociationDefinition associationDefinition = (AssociationDefinition) entityDef;
                    Iterable<String> associationIds = null;
                    long totalCount = 0;
                    
                    boolean checkAgainstSourceEntity = associationDefinition.getSourceEntity().isOfType(id);
                    boolean checkAgainstTargetEntity = associationDefinition.getTargetEntity().isOfType(id);
                    String query = uriInfo.getRequestUri().getQuery();
                    
                    if (checkAgainstSourceEntity && checkAgainstTargetEntity) {
                        associationIds = associationDefinition.getService().getAssociationsFor(id, skip, max, query,
                                sortBy, sortOrder);
                        totalCount = associationDefinition.getService().countAssociationsFor(id, query);
                    } else if (checkAgainstSourceEntity) {
                        associationIds = associationDefinition.getService().getAssociationsWith(id, skip, max, query,
                                sortBy, sortOrder);
                        totalCount = associationDefinition.getService().countAssociationsWith(id, query);
                    } else if (checkAgainstTargetEntity) {
                        associationIds = associationDefinition.getService().getAssociationsTo(id, skip, max, query,
                                sortBy, sortOrder);
                        totalCount = associationDefinition.getService().countAssociationsTo(id, query);
                    } else {
                        return Response.status(Status.NOT_FOUND).build();
                    }
                    
                    ResponseBuilder response;
                    if (fullEntities) {
                        response = Response.ok(getFullEntities(associationIds, entityDef, uriInfo, sortBy, sortOrder));
                    } else {
                        CollectionResponse collection = getShortEntities(uriInfo, entityDef, associationIds);
                        response = Response.ok(collection);
                    }
                    return addPagingHeaders(response, entityDef.getType(), skip, max, totalCount, uriInfo).build();
                }
                return Response.status(Status.NOT_FOUND).build();
            }
            
        });
    }
    
    private ResponseBuilder addPagingHeaders(ResponseBuilder resp, String type, int currentStart, int size, long total,
            UriInfo info) {
        int nextStart = currentStart + size;
        if (nextStart < total) {
            String nextLink = info.getRequestUriBuilder().replaceQueryParam(START_INDEX_PARAM, nextStart)
                    .replaceQueryParam(MAX_RESULTS_PARAM, size).build().toString();
            resp.header(LINK_HEADER, "<" + nextLink + ">; rel=next");
        }
        if (currentStart > 0) {
            int prevStart = currentStart - size;
            String prevLink = info.getRequestUriBuilder()
                    .replaceQueryParam(START_INDEX_PARAM, prevStart > 0 ? prevStart : 0)
                    .replaceQueryParam(MAX_RESULTS_PARAM, size).build().toString();
            resp.header(LINK_HEADER, "<" + prevLink + ">; rel=prev");
        }
        resp.header(TOTAL_COUNT_HEADER, total);
        return resp;
    }
    
    private CollectionResponse getShortEntities(final UriInfo uriInfo, EntityDefinition entityDef,
            Iterable<String> associationIds) {
        CollectionResponse collection = new CollectionResponse();
        if (associationIds != null && associationIds.iterator().hasNext()) {
            for (String id : associationIds) {
                String href = ResourceUtil.getURI(uriInfo, entityDef.getResourceName(), id).toString();
                collection.add(id, ResourceConstants.SELF, entityDef.getType(), href);
            }
        }
        return collection;
    }
    
    private Iterable<EntityBody> getFullEntities(Iterable<String> associationIds, EntityDefinition entityDef,
            UriInfo uriInfo, String sortBy, SortOrder sortOrder) {
        Iterable<EntityBody> entityBodies = entityDef.getService().get(associationIds, sortBy, sortOrder);
        addLinksToEntities(entityBodies, entityDef, uriInfo);
        return entityBodies;
    }
    
    /**
     * Get the full entities, not just links
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @param uriInfo
     * @return A single entity or association, unless the type references an association and the id
     *         represents the source entity. In that case a collection of associations.
     * @response.representation.200.mediaType application/json
     */
    @GET
    @Path("{id}")
    @Produces({ Resource.SLC_LONG_JSON_MEDIA_TYPE })
    public Response getFullEntities(@PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max, @Context final UriInfo uriInfo) {
        return getEntity(id, sortBy, sortOrder, skip, max, true, uriInfo);
    }
    
    /**
     * Gets the target entities from an association when the source entity is specified for the
     * association.
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A collection of entities that are the targets of the specified source in an
     *         association
     * @response.representation.200.mediaType application/json
     */
    @GET
    @Path("{id}/targets")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getHoppedRelatives(@PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return handle(new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                if (entityDef instanceof AssociationDefinition) {
                    AssociationDefinition associationDefinition = (AssociationDefinition) entityDef;
                    Iterable<String> relatives = null;
                    EntityDefinition relative = null;
                    if (associationDefinition.getSourceEntity().isOfType(id)) {
                        relatives = associationDefinition.getService().getAssociatedEntitiesWith(id, skip, max,
                                uriInfo.getRequestUri().getQuery(), sortBy, sortOrder);
                        relative = associationDefinition.getTargetEntity();
                    } else if (associationDefinition.getTargetEntity().isOfType(id)) {
                        relatives = associationDefinition.getService().getAssociatedEntitiesTo(id, skip, max,
                                uriInfo.getRequestUri().getQuery(), sortBy, sortOrder);
                        relative = associationDefinition.getSourceEntity();
                    } else {
                        return Response.status(Status.NOT_FOUND).build();
                    }
                    
                    if (fullEntities) {
                        return Response.ok(getHoppedEntities(relatives, relative, uriInfo, sortBy, sortOrder)).build();
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
    
    private Iterable<EntityBody> getHoppedEntities(Iterable<String> relatives, EntityDefinition relativeDef,
            UriInfo uriInfo, String sortBy, SortOrder sortOrder) {
        Iterable<EntityBody> entityBodies = relativeDef.getService().get(relatives, sortBy, sortOrder);
        addLinksToEntities(entityBodies, relativeDef, uriInfo);
        return entityBodies;
    }
    
    private void addLinksToEntities(Iterable<EntityBody> entityBodies, EntityDefinition relativeDef, UriInfo uriInfo) {
        for (EntityBody entityBody : entityBodies) {
            entityBody.put(ResourceConstants.LINKS,
                    getLinks(uriInfo, relativeDef, (String) entityBody.get("id"), entityBody));
        }
    }
    
    /**
     * Get the full entities, not just links
     * 
     * @param typePath
     *            resrouceUri for the entity/association
     * @param id
     *            either the association id or the association's source entity id
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @param uriInfo
     * @return A collection of entities that are the targets of the specified source in an
     *         association
     * @response.representation.200.mediaType application/json
     */
    @GET
    @Path("{id}/targets")
    @Produces({ Resource.SLC_LONG_JSON_MEDIA_TYPE })
    public Response getFullHoppedRelatives(@PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max, @Context final UriInfo uriInfo) {
        return getHoppedRelatives(id, sortBy, sortOrder, skip, max, true, uriInfo);
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
    @DELETE
    @Path("{id}")
    public Response deleteEntity(@PathParam("id") final String id) {
        return handle(new ResourceLogic() {
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
    @PUT
    @Path("{id}")
    public Response updateEntity(@PathParam("id") final String id, final EntityBody newEntityBody) {
        return handle(new ResourceLogic() {
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
    private Response handle(ResourceLogic logic) {
        if (entityDef == null) {
            return Response
                    .status(Status.NOT_FOUND)
                    .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                            "Invalid resource path")).build();
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

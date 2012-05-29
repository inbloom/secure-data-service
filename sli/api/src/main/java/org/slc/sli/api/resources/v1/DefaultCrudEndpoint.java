package org.slc.sli.api.resources.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderFactory;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Prototype new api end points and versioning base class
 *
 * @author srupasinghe
 * @author kmyers
 *
 */
@Component
@Scope("request")
@Consumes({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON+";charset=utf-8", HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML, HypermediaType.VENDOR_SLC_XML })
public class DefaultCrudEndpoint implements CrudEndpoint {
    /* Shared query parameters that are used by all endpoints */
    @QueryParam(ParameterConstants.INCLUDE_CUSTOM)
    @DefaultValue(ParameterConstants.DEFAULT_INCLUDE_CUSTOM)
    protected String includeCustomEntityStr;

    /* Critera you can override in sublcass */
    protected NeutralCriteria extraCriteria;

    /* The maximum number of values allowed in a comma separated string */
    public static final int MAX_MULTIPLE_UUIDS = 100;

    /* Access to entity definitions */
    private final EntityDefinitionStore entityDefs;

    private final String resourceName;

    /* Logger utility to use to output debug, warning, or other messages to the "console" */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCrudEndpoint.class);

    @Autowired
    private OptionalFieldAppenderFactory factory;

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

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
     *            access to entity definitions
     */
    public DefaultCrudEndpoint(final EntityDefinitionStore entityDefs, String resourceName) {
        if (entityDefs == null) {
            throw new NullPointerException("entityDefs");
        }
        if (resourceName == null) {
            throw new NullPointerException("resourceName");
        }
        this.entityDefs = entityDefs;
        this.resourceName = resourceName;
    }

    /**
     * Creates a new entity in a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param newEntityBody
     *            new map of keys/values for entity
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    @Override
    public Response create(final String resourceName, final EntityBody newEntityBody, final HttpHeaders headers,
            final UriInfo uriInfo) {
        return handle(resourceName, entityDefs, uriInfo, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                String id = entityDef.getService().create(newEntityBody);
                String uri = ResourceUtil.getURI(uriInfo, PathConstants.V1,
                        PathConstants.TEMP_MAP.get(entityDef.getResourceName()), id).toString();
                return Response.status(Status.CREATED).header("Location", uri).build();
            }
        });
    }

    /**
     * Reads one or more entities from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param key
     *            field to be queried against
     * @param value
     *            comma separated list of values to be found in the key
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    @Override
    public Response read(final String resourceName, final String key, final String value, final HttpHeaders headers,
            final UriInfo uriInfo) {
        // /v1/entity/{id}/associations
        return handle(resourceName, entityDefs, uriInfo, new ResourceLogic() {
            @Override
            public Response run(final EntityDefinition entityDef) {
//                DE260 - Logging of possibly sensitive data
//                LOGGER.debug("Attempting to read from {} where {} = {}",
//                        new Object[] { entityDef.getStoredCollectionName(), key, value });

                NeutralQuery neutralQuery = new ApiQuery(uriInfo);
                List<String> valueList = Arrays.asList(value.split(","));
                neutralQuery.addCriteria(new NeutralCriteria(key, "in", valueList));
                neutralQuery = addTypeCriteria(entityDef, neutralQuery);

                // a new list to store results
                List<EntityBody> results = new ArrayList<EntityBody>();

                // list all entities matching query parameters and iterate over results
                for (EntityBody entityBody : entityDef.getService().list(neutralQuery)) {
                    entityBody.put(ResourceConstants.LINKS,
                            ResourceUtil.getLinks(entityDefs, entityDef, entityBody, uriInfo));

                    // add the custom entity if it was requested
                    addCustomEntity(entityBody, entityDef, uriInfo);

                    // add entity to resulting response
                    results.add(entityBody);
                }

                if (results.isEmpty()) {
                    Status errorStatus = Status.NOT_FOUND;
                    return Response
                            .status(errorStatus)
                            .entity(new ErrorResponse(errorStatus.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                                    "Entity not found: " + key + "=" + value)).build();
                } else {
                    long pagingHeaderTotalCount = getTotalCount(entityDef.getService(), neutralQuery);
                    return addPagingHeaders(Response.ok(new EntityResponse(entityDef.getType(),
                            results)), pagingHeaderTotalCount, uriInfo).build();
                }
            }
        });
    }

    /**
     * Searches "resourceName" for entries where "key" equals "value", then for each result
     * uses "idkey" field's value to query "resolutionResourceName" against the ID field.
     *
     * @param resourceName
     *            where the entity should be located
     * @param key
     *            field to be queried against (when searching resources)
     * @param value
     *            comma separated list of expected values to be found for the key
     * @param idKey
     *            field in resource that contains the ID to be resolved
     * @param resolutionResourceName
     *            where to query for the entity with the ID taken from the "idKey" field
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    @Override
    public Response read(final String resourceName, final String key, final String value, final String idKey,
            final String resolutionResourceName, final HttpHeaders headers, final UriInfo uriInfo) {
        // /v1/entity/{id}/associations/entity
        return handle(resourceName, entityDefs, uriInfo, new ResourceLogic() {
            @Override
            public Response run(final EntityDefinition entityDef) {
                // look up information on association
                EntityDefinition endpointEntity = entityDefs.lookupByResourceName(resolutionResourceName);
                String resource1 = entityDef.getStoredCollectionName();
//                String resource2 = endpointEntity.getStoredCollectionName();

//                 DE260 - Logging of possibly sensitive data
//                 write some information to debug
//                LOGGER.debug("Attempting to list from {} where {} = {}", new Object[] { resource1, key, value });
//                LOGGER.debug("Then for each result, ");
//                LOGGER.debug(" going to read from {} where \"_id\" = {}.{}",
//                        new Object[] { resource2, resource1, idKey });

                NeutralQuery endpointNeutralQuery = new ApiQuery(uriInfo);
                NeutralQuery associationNeutralQuery = createAssociationNeutralQuery(key, value, idKey);
                associationNeutralQuery = addTypeCriteria(entityDef, associationNeutralQuery);

                // final/resulting information
                List<EntityBody> finalResults = new ArrayList<EntityBody>();

                List<String> ids = new ArrayList<String>();
                Map<String, List<EntityBody>> associations = new HashMap<String, List<EntityBody>>();
                // for each association
                for (EntityBody entityBody : entityDef.getService().list(associationNeutralQuery)) {
                    // add the custom entity if it was requested
                    addCustomEntity(entityBody, entityDef, uriInfo);

                    for (String id : entityBody.getId(idKey)) {
                        ids.add(id);
                        if (associations.containsKey(id)) {
                            associations.get(id).add(entityBody);
                        } else {
                            List<EntityBody> list = new ArrayList<EntityBody>();
                            list.add(entityBody);

                            associations.put(id, list);
                        }
                    }
                }

                // if (ids.size() == 0) {
                // return Response.ok(finalResults).build();
                // }

                if (!ids.isEmpty()) {
                    endpointNeutralQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
                    endpointNeutralQuery = addTypeCriteria(endpointEntity, endpointNeutralQuery);
                    for (EntityBody result : endpointEntity.getService().list(endpointNeutralQuery)) {
                        if (associations.get(result.get("id")) != null) {

                            // direct self reference dont need to include association in reponse
                            if (!endpointEntity.getResourceName().equals(entityDef.getResourceName())) {
                                result.put(resource1, associations.get(result.get("id")));
                            }
                        }

                        result.put(ResourceConstants.LINKS, ResourceUtil.getLinks(entityDefs,
                                entityDefs.lookupByResourceName(resolutionResourceName), result, uriInfo));
                        finalResults.add(result);
                    }

                    finalResults = appendOptionalFields(uriInfo, finalResults, DefaultCrudEndpoint.this.resourceName);
                }

                if (finalResults.isEmpty()) {
                    Status errorStatus = Status.NOT_FOUND;
                    return Response
                            .status(errorStatus)
                            .entity(new ErrorResponse(errorStatus.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                                    "Entity not found: " + key + "=" + value)).build();
                } else {
                    long pagingHeaderTotalCount = getTotalCount(endpointEntity.getService(), endpointNeutralQuery);
                    return addPagingHeaders(Response.ok(new EntityResponse(endpointEntity.getType(),
                            finalResults)), pagingHeaderTotalCount, uriInfo).build();
                }
            }
        });
    }

    /**
     * Reads one or more entities from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param idList
     *            a single ID or a comma separated list of IDs
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    @Override
    public Response read(final String resourceName, final String idList, final HttpHeaders headers,
            final UriInfo uriInfo) {
        // /v1/entity/{id}
        return handle(resourceName, entityDefs, uriInfo, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                int idLength = idList.split(",").length;

                if (idLength > DefaultCrudEndpoint.MAX_MULTIPLE_UUIDS) {
                    Status errorStatus = Status.PRECONDITION_FAILED;
                    String errorMessage = "Too many GUIDs: " + idLength + " (input) vs "
                            + DefaultCrudEndpoint.MAX_MULTIPLE_UUIDS + " (allowed)";
                    return Response
                            .status(errorStatus)
                            .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                                    errorMessage)).build();
                }

                List<String> ids = new ArrayList<String>();

                for (String id : idList.split(",")) {
                    ids.add(id);
                }

                NeutralQuery neutralQuery = new ApiQuery(uriInfo);
                neutralQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
                neutralQuery = addTypeCriteria(entityDef, neutralQuery);

                // final/resulting information
                List<EntityBody> finalResults = new ArrayList<EntityBody>();

                Iterable<EntityBody> entities;
                if (idLength == 1) {
                    entities = Arrays.asList(new EntityBody[] { entityDef.getService().get(idList, neutralQuery) });
                } else {
                    entities = entityDef.getService().list(neutralQuery);
                }

                for (EntityBody result : entities) {
                    if (result != null) {
                        result.put(ResourceConstants.LINKS,
                                ResourceUtil.getLinks(entityDefs, entityDef, result, uriInfo));

                        // add the custom entity if it was requested
                        addCustomEntity(result, entityDef, uriInfo);
                    }
                    finalResults.add(result);
                }

                finalResults = appendOptionalFields(uriInfo, finalResults, DefaultCrudEndpoint.this.resourceName);

                // Return results as an array if multiple IDs were requested (comma separated list),
                // single entity otherwise
                if (finalResults.isEmpty()) {
                    return Response
                            .status(Status.NOT_FOUND)
                            .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                                    "Entity not found: " + resourceName + "=" + idList)).build();
                } else if (finalResults.size() == 1) {
                    return addPagingHeaders(Response.ok(new EntityResponse(entityDef.getType(),
                            finalResults.get(0))), 1, uriInfo).build();
                } else {
                    long pagingHeaderTotalCount = getTotalCount(entityDef.getService(), neutralQuery);
                    return addPagingHeaders(Response.ok(new EntityResponse(entityDef.getType(),
                            finalResults)), pagingHeaderTotalCount, uriInfo).build();
                }
            }
        });
    }

    /**
     * Deletes a given entity from a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param id
     *            ID of object being deleted
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    @Override
    public Response delete(final String resourceName, final String id, final HttpHeaders headers, final UriInfo uriInfo) {
        return handle(resourceName, entityDefs, uriInfo, new ResourceLogic() {
            @Override
            public Response run(final EntityDefinition entityDef) {
                entityDef.getService().delete(id);
                return Response.status(Status.NO_CONTENT).build();
            }
        });
    }

    /**
     * Updates a given entity in a specific location or collection.
     *
     * @param resourceName
     *            where the entity should be located
     * @param id
     *            ID of object being updated
     * @param newEntityBody
     *            new map of keys/values for entity
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return resulting status from request
     */
    @Override
    public Response update(final String resourceName, final String id, final EntityBody newEntityBody,
            final HttpHeaders headers, final UriInfo uriInfo) {
        return handle(resourceName, entityDefs, uriInfo, new ResourceLogic() {
            @Override
            public Response run(EntityDefinition entityDef) {
                EntityBody copy = new EntityBody(newEntityBody);
                copy.remove(ResourceConstants.LINKS);

//                DE260 - Logging of possibly sensitive data
//                LOGGER.debug("updating entity {}", copy);
                entityDef.getService().update(id, copy);

//                DE260 - Logging of possibly sensitive data
//                LOGGER.debug("updating entity {}", copy);
                return Response.status(Status.NO_CONTENT).build();
            }
        });
    }

    /**
     * Reads all entities from a specific location or collection.
     *
     * @param collectionName
     *            where the entity should be located
     * @param headers
     *            HTTP header information (which includes request headers)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return requested information or error status
     */
    @Override
    public Response readAll(final String collectionName, final HttpHeaders headers, final UriInfo uriInfo) {
        return handle(collectionName, entityDefs, uriInfo, new ResourceLogic() {
            // v1/entity
            @Override
            public Response run(final EntityDefinition entityDef) {
                // final/resulting information
                List<EntityBody> results = new ArrayList<EntityBody>();

                Iterable<EntityBody> entityBodies = null;
                NeutralQuery query = new ApiQuery(uriInfo);
                query = addTypeCriteria(entityDef, query);
                if (extraCriteria != null) {
                    query.addCriteria(extraCriteria);
                }
                if (shouldReadAll()) {
                    entityBodies = SecurityUtil.sudoRun(new SecurityTask<Iterable<EntityBody>>() {

                        @Override
                        public Iterable<EntityBody> execute() {
                            NeutralQuery query = new ApiQuery(uriInfo);
                            query = addTypeCriteria(entityDef, query);
                            if (extraCriteria != null) {
                                query.addCriteria(extraCriteria);
                            }
                            // TODO Auto-generated method stub
                            return entityDef.getService().list(query);
                        }
                    });
                } else {
                    entityBodies = entityDef.getService().list(query);
                }
                for (EntityBody entityBody : entityBodies) {

                    // if links should be included then put them in the entity body
                    entityBody.put(ResourceConstants.LINKS,
                            ResourceUtil.getLinks(entityDefs, entityDef, entityBody, uriInfo));

                 results.add(entityBody);
                }

                long pagingHeaderTotalCount = getTotalCount(entityDef.getService(), query);
                return addPagingHeaders(Response.ok(new EntityResponse(entityDef.getType(), results)),
                        pagingHeaderTotalCount, uriInfo).build();
            }
        });
    }

    protected boolean shouldReadAll() {
        return false;
    }

    /**
     * Returns the sub-resource responsible for responding to requests for custom entity data
     *
     * @param id
     *            the id of the entity the custom resource is applied to
     */
    @Path("{id}/" + PathConstants.CUSTOM_ENTITIES)
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Override
    public CustomEntityResource getCustomEntityResource(@PathParam("id") String id) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(this.resourceName);
        return new CustomEntityResource(id, entityDef);
    }

    /* Utility methods */

    protected static long getTotalCount(EntityService basicService, NeutralQuery neutralQuery) {

        if (basicService == null) {
            return 0;
        }

        if (neutralQuery == null) {
            return basicService.count(new NeutralQuery());
        }

        int originalLimit = neutralQuery.getLimit();
        int originalOffset = neutralQuery.getOffset();
        neutralQuery.setLimit(0);
        neutralQuery.setOffset(0);
        long count = basicService.count(neutralQuery);
        neutralQuery.setLimit(originalLimit);
        neutralQuery.setOffset(originalOffset);
        return count;
    }

    /**
     * Retrieve the custom entity for the given request if flag includeCustom is set to true.
     *
     */
    private void addCustomEntity(EntityBody entityBody, final EntityDefinition entityDef, UriInfo uriInfo) {
        boolean includeCustomEntity = "true".equals(includeCustomEntityStr);
        if (includeCustomEntity) {
            String entityId = (String) entityBody.get("id");
            EntityBody custom = entityDef.getService().getCustom(entityId);
            if (custom != null) {
                entityBody.put(ResourceConstants.CUSTOM, custom);
            }
        }
    }

    /**
     * Handle preconditions and exceptions.
     */
    private Response handle(final String resourceName, final EntityDefinitionStore entityDefs,
            final UriInfo uriInfo, final ResourceLogic logic) {
        EntityDefinition entityDef = entityDefs.lookupByResourceName(resourceName);
        if (entityDef == null) {
            return Response
                    .status(Status.NOT_FOUND)
                    .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                            "Invalid resource path: " + resourceName)).build();
        }

        // log if entity is restricted.
        if (entityDef.isRestrictedForLogging()) {
            if (securityEventBuilder != null) {
                SecurityEvent event = securityEventBuilder.createSecurityEvent(DefaultCrudEndpoint.class.toString(),
                        uriInfo, "restricted entity \"" + entityDef.getResourceName() + "\" is accessed.");
                audit(event);
            } else {
                LOGGER.warn("Cannot create security event, when restricted entity \"" + entityDef.getResourceName() + "\" is accessed.");
            }
        }

        return logic.run(entityDef);
    }

    /**
     * Creates a query that looks up an association where key = value and only returns the specified
     * field.
     * A convenience method for querying for associations when resolving their endpoints.
     *
     * @param key
     * @param value
     *            a comma separated list of values
     * @param includeField
     * @return
     */
    private NeutralQuery createAssociationNeutralQuery(String key, String value,
            String includeField) {
        NeutralQuery neutralQuery = new NeutralQuery();
        List<String> list = new ArrayList<String>(Arrays.asList(value.split(",")));
        neutralQuery.addCriteria(new NeutralCriteria(key, NeutralCriteria.CRITERIA_IN, list));
        // neutralQuery.setIncludeFields(includeField);
        return neutralQuery;
    }

    /**
     * Append the optional fields to the given list of entities
     *
     * @param info
     *            UriInfo
     * @param entities
     *            The list of entities
     * @return
     */
    protected List<EntityBody> appendOptionalFields(UriInfo info, List<EntityBody> entities, String baseEndpoint) {

        if (factory == null) {
            return entities;
        }

        List<String> optionalFields = getOptionalFields(info);

        if (optionalFields != null) {
            for (String type : optionalFields) {
                for (String appenderType : type.split(",")) {
                    Map<String, String> values = extractOptionalFieldParams(appenderType);

                    OptionalFieldAppender appender = factory
                            .getOptionalFieldAppender(baseEndpoint + "_" + values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));

                    if (appender != null) {
                        entities = appender.applyOptionalField(entities,
                                values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));
                    }

                }
            }
        }

        return entities;
    }

    protected List<String> getOptionalFields(UriInfo info) {
        return info.getQueryParameters(true).get(ParameterConstants.OPTIONAL_FIELDS);
    }

    /**
     * Extract the parameters from the optional field value
     * @param optionalFieldValue The optional field value
     * @return
     */
    protected Map<String, String> extractOptionalFieldParams(String optionalFieldValue) {
        Map<String, String> values = new HashMap<String, String>();
        String appender = null, params = null;

        if (optionalFieldValue.contains(".")) {
            StringTokenizer st = new StringTokenizer(optionalFieldValue, ".");

            int index = 0;
            String token = null;
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                switch(index) {
                    case 0: appender = token; break;
                    case 1: params = token; break;
                }
                ++index;
            }
        } else {
            appender = optionalFieldValue;
        }

        values.put(OptionalFieldAppenderFactory.APPENDER_PREFIX, appender);
        values.put(OptionalFieldAppenderFactory.PARAM_PREFIX, params);

        return values;
    }

    /**
     * Add the type criteria to a given query if the stored collection of the
     * resource is different from its type
     * @param entityDefinition The entity definition for the resource
     * @param query The query to append the criteria
     * @return The modified query
     */
    protected NeutralQuery addTypeCriteria(EntityDefinition entityDefinition, NeutralQuery query) {

        if (query != null && entityDefinition != null
                && !entityDefinition.getType().equals(entityDefinition.getStoredCollectionName())) {
            query.addCriteria(new NeutralCriteria("type", NeutralCriteria.CRITERIA_IN,
                    Arrays.asList(entityDefinition.getType()), false));
        }

        return query;
    }

    private Response.ResponseBuilder addPagingHeaders(Response.ResponseBuilder resp, long total, UriInfo info) {
        if (info != null && resp != null) {
            NeutralQuery neutralQuery = new ApiQuery(info);
            int offset = neutralQuery.getOffset();
            int limit = neutralQuery.getLimit();

            int nextStart = offset + limit;
            if (nextStart < total) {
                neutralQuery.setOffset(nextStart);

                String nextLink = info.getRequestUriBuilder().replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + nextLink + ">; rel=next");
            }

            if (offset > 0) {
                int prevStart = Math.max(offset - limit, 0);
                neutralQuery.setOffset(prevStart);

                String prevLink = info.getRequestUriBuilder().replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + prevLink + ">; rel=prev");
            }

            resp.header(ParameterConstants.HEADER_TOTAL_COUNT, total);
        }

        return resp;
    }

    /**
     * Returns all entities for which the logged in User has permission and context.
     *
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    public Response readAll(final int offset, final int limit, HttpHeaders headers, final UriInfo uriInfo) {
        return this.readAll(resourceName, headers, uriInfo);
    }

    /**
     * Create a new entity.
     *
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessable.}
     */
    public Response create(final EntityBody newEntityBody, HttpHeaders headers, final UriInfo uriInfo) {
        return this.create(resourceName, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single entity
     *
     * @param id
     *            The Id of the entity
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single entity
     */
    public Response read(final String id, HttpHeaders headers, final UriInfo uriInfo) {
        return this.read(resourceName, id, headers, uriInfo);
    }

    /**
     * Delete a entity
     *
     * @param id
     *            The Id of the entity
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    public Response delete(final String id, HttpHeaders headers, final UriInfo uriInfo) {
        return this.delete(resourceName, id, headers, uriInfo);
    }

    /**
     * Update an existing entity.
     *
     * @param id
     *            The id of the entity
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    public Response update(final String id, final EntityBody newEntityBody, HttpHeaders headers, final UriInfo uriInfo) {
        return this.update(resourceName, id, newEntityBody, headers, uriInfo);
    }
}

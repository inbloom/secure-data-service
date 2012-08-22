package org.slc.sli.api.resources.generic.service;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.selectors.LogicalEntity;
import org.slc.sli.api.selectors.UnsupportedSelectorException;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.slc.sli.modeling.uml.ClassType;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Default implementation of the resource service.
 *
 * @author srupasinghe
 */

@Component
public class DefaultResourceService implements ResourceService {

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    @Autowired
    private LogicalEntity logicalEntity;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private ModelProvider provider;

    public static final int MAX_MULTIPLE_UUIDS = 100;

    protected static interface ServiceLogic {
        public ServiceResponse run(final Resource resource, EntityDefinition definition);
    }

    protected ServiceResponse handle(final Resource resource, ServiceLogic logic) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        ServiceResponse serviceResponse  = logic.run(resource, definition);

        return serviceResponse;
    }

    @Override
    public ServiceResponse getEntitiesByIds(final Resource resource, final String idList, final URI requestURI) {

        return handle(resource, new ServiceLogic() {
            @Override
            public ServiceResponse run(final Resource resource, EntityDefinition definition) {
                final int idLength = idList.split(",").length;

                if (idLength > MAX_MULTIPLE_UUIDS) {
                    String errorMessage = "Too many GUIDs: " + idLength + " (input) vs "
                            + MAX_MULTIPLE_UUIDS + " (allowed)";
                    throw new PreConditionFailedException(errorMessage);
                }

                final List<String> ids = Arrays.asList(idList.split(","));

                ApiQuery apiQuery = getApiQuery(definition, requestURI);

                apiQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
                apiQuery.setLimit(0);
                apiQuery.setOffset(0);

                // final/resulting information
                List<EntityBody> finalResults = null;
                try {
                    finalResults = logicalEntity.getEntities(apiQuery, resource.getResourceType());
                } catch (UnsupportedSelectorException e) {
                    finalResults = (List<EntityBody>) definition.getService().list(apiQuery);
                }

                if (idLength == 1 && finalResults.isEmpty()) {
                    throw new EntityNotFoundException(ids.get(0));
                }

                //inject error entities if needed
                finalResults = injectErrors(definition, ids, finalResults);

                return new ServiceResponse(finalResults, idLength);
            }
        });
    }

    @Override
    public ServiceResponse getEntities(final Resource resource, final URI requestURI,
                                       final boolean getAllEntities) {

        return handle(resource, new ServiceLogic() {
            @Override
            public ServiceResponse run(final Resource resource, EntityDefinition definition) {
                Iterable<EntityBody> entityBodies = null;
                final ApiQuery apiQuery = getApiQuery(definition, requestURI);

                if (getAllEntities) {
                    entityBodies = SecurityUtil.sudoRun(new SecurityUtil.SecurityTask<Iterable<EntityBody>>() {

                        @Override
                        public Iterable<EntityBody> execute() {
                            return logicalEntity.getEntities(apiQuery, resource.getResourceType());
                        }
                    });
                } else {
                    try {
                        entityBodies = logicalEntity.getEntities(apiQuery, resource.getResourceType());
                    } catch (UnsupportedSelectorException e) {
                        entityBodies = definition.getService().list(apiQuery);
                    }
                }
                long count = getEntityCount(definition, apiQuery);

                return new ServiceResponse((List<EntityBody>) entityBodies, count) ;
            }
        });
    }

    protected ApiQuery addTypeCriteria(EntityDefinition entityDefinition, ApiQuery apiQuery) {

        if (apiQuery != null && entityDefinition != null
                && !entityDefinition.getType().equals(entityDefinition.getStoredCollectionName())) {
            apiQuery.addCriteria(new NeutralCriteria("type", NeutralCriteria.CRITERIA_IN, Arrays.asList(entityDefinition
                    .getType()), false));
        }

        return apiQuery;
    }

    protected long getEntityCount(EntityDefinition definition, ApiQuery apiQuery) {
        long count = 0;

        if (definition.getService() == null) {
            return count;
        }

        if (apiQuery == null) {
            return definition.getService().count(new NeutralQuery());
        }

        int originalLimit = apiQuery.getLimit();
        int originalOffset = apiQuery.getOffset();
        apiQuery.setLimit(0);
        apiQuery.setOffset(0);

        count = definition.getService().count(apiQuery);
        apiQuery.setLimit(originalLimit);
        apiQuery.setOffset(originalOffset);

        return count;
    }

    protected ApiQuery getApiQuery(EntityDefinition definition, final URI requestURI) {
        ApiQuery apiQuery = new ApiQuery(requestURI);
        addTypeCriteria(definition, apiQuery);

        return apiQuery;
    }

    protected ApiQuery getApiQuery(EntityDefinition definition) {
        return getApiQuery(definition, null);
    }
    @Override
    public String postEntity(final Resource resource, EntityBody entity) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        return definition.getService().create(entity);
    }

    @Override
    public void putEntity(Resource resource, String id, EntityBody entity) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        EntityBody copy = new EntityBody(entity);
        copy.remove(ResourceConstants.LINKS);

        definition.getService().update(id, copy);
    }

    @Override
    public void patchEntity(Resource resource, String id, EntityBody entity) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        EntityBody copy = new EntityBody(entity);
        copy.remove(ResourceConstants.LINKS);

        definition.getService().patch(id, copy);
    }

    @Override
    public void deleteEntity(Resource resource, String id) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        definition.getService().delete(id);
    }

    @Override
    public String getEntityType(Resource resource) {
        return resourceHelper.getEntityDefinition(resource).getType();
    }

    @Override
    public ServiceResponse getEntities(final Resource base, final String id, final Resource resource, final URI requestURI) {
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        final String associationKey = getConnectionKey(base, resource);
        List<EntityBody> entityBodyList;
        List<String> valueList = Arrays.asList(id.split(","));

        final ApiQuery apiQuery = getApiQuery(definition, requestURI);
        apiQuery.addCriteria(new NeutralCriteria(associationKey, "in", valueList));

        try {
            entityBodyList = logicalEntity.getEntities(apiQuery, definition.getResourceName());
        } catch (final UnsupportedSelectorException e) {
            entityBodyList = (List<EntityBody>) definition.getService().list(apiQuery);
        }

        long count = getEntityCount(definition,apiQuery);
        return new ServiceResponse(entityBodyList, count) ;
    }

    @Override
    // TODO
    public ServiceResponse getEntities(Resource base, String id, Resource association, Resource resource, URI requestUri) {
        final EntityDefinition finalEntity = resourceHelper.getEntityDefinition(resource);
        final EntityDefinition  assocEntity= resourceHelper.getEntityDefinition(association);
        final String associationKey = getConnectionKey(base, association);

        List<String> valueList = Arrays.asList(id.split(","));
        final ApiQuery apiQuery = getApiQuery(assocEntity);
        apiQuery.addCriteria(new NeutralCriteria(associationKey, "in", valueList));

        final String resourceKey = getConnectionKey(association, resource);
        final List<String> filteredIdList = new ArrayList<String>();
        for (EntityBody entityBody : assocEntity.getService().list(apiQuery)) {
           filteredIdList.add(entityBody.get(resourceKey).toString());
        }

        List<EntityBody> entityBodyList;
        final ApiQuery finalApiQuery = getApiQuery(finalEntity, requestUri);
        finalApiQuery.addCriteria(new NeutralCriteria("_id", "in", filteredIdList));

        try {
            entityBodyList = logicalEntity.getEntities(finalApiQuery, finalEntity.getResourceName());
        } catch (final UnsupportedSelectorException e) {
            entityBodyList = (List<EntityBody>) finalEntity.getService().list(finalApiQuery);
        }

        long count = getEntityCount(finalEntity, finalApiQuery);

        return new ServiceResponse(entityBodyList, count) ;
    }

    private String getConnectionKey(final Resource fromEntity, final Resource toEntity) {
        final EntityDefinition toEntityDef = resourceHelper.getEntityDefinition(toEntity);
        final EntityDefinition fromEntityDef = resourceHelper.getEntityDefinition(fromEntity);

        ClassType fromEntityType = provider.getClassType(StringUtils.capitalize(fromEntityDef.getType()));
        ClassType toEntityType = provider.getClassType(StringUtils.capitalize(toEntityDef.getType()));

        return provider.getConnectionPath(fromEntityType,toEntityType);
    }

    protected List<EntityBody> injectErrors(EntityDefinition definition, final List<String> ids, List<EntityBody> finalResults) {
        int idLength = ids.size();

        if (idLength > 1) {
            Collections.sort(finalResults, new Comparator<EntityBody>() {
                @Override
                public int compare(EntityBody o1, EntityBody o2) {
                    return ids.indexOf(o1.get("id")) - ids.indexOf(o2.get("id"));
                }
            });

            int finalResultsSize = finalResults.size();

            // loop if results quantity does not matched requested quantity
            for (int i = 0; finalResultsSize != idLength && i < idLength; i++) {

                String checkedId = ids.get(i);

                boolean checkedIdMissing = false;

                try {
                    checkedIdMissing = !(finalResults.get(i).get("id").equals(checkedId));
                } catch (IndexOutOfBoundsException ioobe) {
                    checkedIdMissing = true;
                }

                // if a particular input ID is not present in the results at the appropriate
                // spot
                if (checkedIdMissing) {

                    Map<String, Object> errorResult = new HashMap<String, Object>();

                    // try individual lookup to capture specific error message (type)
                    try {
                        definition.getService().get(ids.get(i));
                    } catch (EntityNotFoundException enfe) {
                        errorResult.put("type", "Not Found");
                        errorResult.put("message", "Entity not found: " + checkedId);
                        errorResult.put("code", Response.Status.NOT_FOUND.getStatusCode());
                    } catch (AccessDeniedException ade) {
                        errorResult.put("type", "Forbidden");
                        errorResult.put("message", "Access DENIED: " + ade.getMessage());
                        errorResult.put("code", Response.Status.FORBIDDEN.getStatusCode());
                    } catch (Exception e) {
                        errorResult.put("type", "Internal Server Error");
                        errorResult.put("message", "Internal Server Error: " + e.getMessage());
                        errorResult.put("code", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                    }

                    finalResults.add(i, new EntityBody(errorResult));
                }
            }
        }

        return finalResults;
    }

}

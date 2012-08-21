package org.slc.sli.api.resources.generic.service;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.selectors.LogicalEntity;
import org.slc.sli.api.selectors.UnsupportedSelectorException;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slc.sli.modeling.uml.ClassType;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
        public List<EntityBody> run(final String resource, EntityDefinition definition);
    }

    protected List<EntityBody> handle(final Resource resource, ServiceLogic logic) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        List<EntityBody> entities = logic.run(resource.getResourceType(), definition);

        return entities;
    }

    @Override
    public List<EntityBody> getEntitiesByIds(final Resource resource, final String idList, final URI requestURI) {

        return handle(resource, new ServiceLogic() {
            @Override
            public List<EntityBody> run(final String resource, EntityDefinition definition) {
                final int idLength = idList.split(",").length;

                if (idLength > MAX_MULTIPLE_UUIDS) {
                    String errorMessage = "Too many GUIDs: " + idLength + " (input) vs "
                            + MAX_MULTIPLE_UUIDS + " (allowed)";
                    throw new PreConditionFailedException(errorMessage);
                }

                final List<String> ids = Arrays.asList(StringUtils.split(idList));

                ApiQuery apiQuery = getApiQuery(definition, requestURI);

                apiQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
                apiQuery.setLimit(0);
                apiQuery.setOffset(0);

                // final/resulting information
                List<EntityBody> finalResults = null;
                try {
                    finalResults = logicalEntity.getEntities(apiQuery, resource);
                } catch (UnsupportedSelectorException e) {
                    finalResults = (List<EntityBody>) definition.getService().list(apiQuery);
                }

                if (idLength == 1 && finalResults.isEmpty()) {
                    throw new EntityNotFoundException(ids.get(0));
                }

                return finalResults;
            }
        });
    }

    @Override
    public List<EntityBody> getEntities(final Resource resource, final URI requestURI,
                                        final boolean getAllEntities) {

        return handle(resource, new ServiceLogic() {
            @Override
            public List<EntityBody> run(final String resource, EntityDefinition definition) {
                Iterable<EntityBody> entityBodies = null;
                final ApiQuery apiQuery = getApiQuery(definition, requestURI);

                if (getAllEntities) {
                    entityBodies = SecurityUtil.sudoRun(new SecurityUtil.SecurityTask<Iterable<EntityBody>>() {

                        @Override
                        public Iterable<EntityBody> execute() {
                            return logicalEntity.getEntities(apiQuery, resource);
                        }
                    });
                } else {
                    try {
                        entityBodies = logicalEntity.getEntities(apiQuery, resource);
                    } catch (UnsupportedSelectorException e) {
                        entityBodies = definition.getService().list(apiQuery);
                    }
                }

                return (List<EntityBody>) entityBodies;
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

    @Override
    public long getEntityCount(Resource resource, final URI requestURI) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        ApiQuery apiQuery = getApiQuery(definition, requestURI);
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
    public void deleteEntity(Resource resource, String id) {
        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        definition.getService().delete(id);
    }

    @Override
    public String getEntityType(Resource resource) {
        return entityDefinitionStore.lookupByResourceName(resource.getResourceType()).getType();
    }

    @Override
    public List<EntityBody> getEntities(final Resource base, final String id, final Resource resource, final URI requestURI) {
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

        return entityBodyList;
    }

    @Override
    // TODO
    public List<EntityBody> getEntities(Resource base, String id, Resource association, Resource resource, URI requestUri) {
        final EntityDefinition finalEntity = resourceHelper.getEntityDefinition(resource);
        final EntityDefinition  assocEntity= resourceHelper.getEntityDefinition(association);
        final String associationKey = getConnectionKey(base, association);

        List<String> valueList = Arrays.asList(id.split(","));
        final ApiQuery apiQuery = getApiQuery(assocEntity, requestUri);
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
            entityBodyList = (List<EntityBody>) finalEntity.getService().list(apiQuery);
        }

        return entityBodyList;
    }

    private String getConnectionKey(final Resource fromEntity, final Resource toEntity) {
        final EntityDefinition toEntityDef = resourceHelper.getEntityDefinition(toEntity);
        final EntityDefinition fromEntityDef = resourceHelper.getEntityDefinition(fromEntity);
        ClassType fromEntityType = provider.getClassType(StringUtils.capitalize(fromEntityDef.getType()));
        ClassType toEntityType = provider.getClassType(StringUtils.capitalize(toEntityDef.getType()));
        return provider.getConnectionPath(fromEntityType,toEntityType);
    }

}

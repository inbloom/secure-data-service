package org.slc.sli.api.resources.generic.service;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.selectors.LogicalEntity;
import org.slc.sli.api.selectors.UnsupportedSelectorException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

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
    private ModelProvider provider;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private List<EntityDecorator> entityDecorators;

    public static final int MAX_MULTIPLE_UUIDS = 100;

    protected static interface ServiceLogic {
        public List<EntityBody> run(final String resource, EntityDefinition definition);
    }

    protected List<EntityBody> handle(final Resource resource, ServiceLogic logic) {
        EntityDefinition definition = getEntityDefinition(resource);

        return logic.run(resource.getResourceType(), definition);
    }

    @Override
    public List<EntityBody> getEntitiesByIds(final Resource resource, final String idList, final URI requestURI, final MultivaluedMap<String, String> queryParams) {
        EntityDefinition definition = getEntityDefinition(resource);
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
            finalResults = logicalEntity.getEntities(apiQuery, resource.getResourceType());
        } catch (UnsupportedSelectorException e) {
            finalResults = (List<EntityBody>) definition.getService().list(apiQuery);
        }

        //apply the decorators
        for (EntityBody entityBody : finalResults) {
            for (EntityDecorator entityDecorator : entityDecorators) {
                entityBody = entityDecorator.decorate(entityBody, definition, queryParams);
            }
        }

        return finalResults;
    }

    @Override
    public List<EntityBody> getEntities(final Resource resource, final URI requestURI, final MultivaluedMap<String, String> queryParams) {

        return handle(resource, new ServiceLogic() {
            @Override
            public List<EntityBody> run(final String resource, EntityDefinition definition) {
                Iterable<EntityBody> entityBodies = null;
                final ApiQuery apiQuery = getApiQuery(definition, requestURI);

                if (shouldReadAll()) {
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

    protected boolean shouldReadAll() {
        return false;
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
    public Long getEntityCount(Resource resource, final URI requestURI, MultivaluedMap<String, String> queryParams) {
        EntityDefinition definition = getEntityDefinition(resource);
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
        EntityDefinition definition = getEntityDefinition(resource);

        return definition.getService().create(entity);
    }

    @Override
    public void putEntity(Resource resource, String id, EntityBody entity) {
        EntityDefinition definition = getEntityDefinition(resource);

        EntityBody copy = new EntityBody(entity);
        copy.remove(ResourceConstants.LINKS);

        definition.getService().update(id, copy);
    }

    @Override
    public void deleteEntity(Resource resource, String id) {
        EntityDefinition definition = getEntityDefinition(resource);

        definition.getService().delete(id);
    }

    public EntityDefinition getEntityDefinition(final Resource resource) {
        return entityDefinitionStore.lookupByResourceName(resource.getResourceType());
    }

    @Override
    public String getEntityType(Resource resource) {
        return entityDefinitionStore.lookupByResourceName(resource.getResourceType()).getType();
    }

    @Override
    public List<EntityBody> getEntities(final String base, final String id, final Resource resource, final URI requestURI) {
        final EntityDefinition definition = getEntityDefinition(resource);
        List<EntityBody> entityBodyList;
        final ApiQuery apiQuery = getApiQuery(definition, requestURI);
        try {
            entityBodyList = logicalEntity.getEntities(apiQuery, definition.getResourceName());
        } catch (final UnsupportedSelectorException e) {
            entityBodyList = (List<EntityBody>) definition.getService().list(apiQuery);
        }

        return entityBodyList;
    }

    @Override
    // TODO
    public List<EntityBody> getEntities(String base, String id, String association, Resource resource, UriInfo uriInfo) {
        throw new UnsupportedOperationException("TODO");
    }

}

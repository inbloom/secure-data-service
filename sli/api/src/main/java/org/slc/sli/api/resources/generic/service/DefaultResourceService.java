package org.slc.sli.api.resources.generic.service;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.selectors.LogicalEntity;
import org.slc.sli.api.selectors.UnsupportedSelectorException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
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

    @Override
    public List<EntityBody> getEntitiesByIds(final String resource, final String idList, final URI requestURI, final MultivaluedMap<String, String> queryParams) {
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
            finalResults = logicalEntity.getEntities(apiQuery, resource);
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
    public List<EntityBody> getEntities(final String resource, final URI requestURI, final MultivaluedMap<String, String> queryParams) {
        EntityDefinition definition = getEntityDefinition(resource);

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
    public long getEntityCount(String resource, final URI requestURI, MultivaluedMap<String, String> queryParams) {
        EntityDefinition definition = getEntityDefinition(resource);
        ApiQuery apiQuery = getApiQuery(definition, requestURI);

        if (definition.getService() == null) {
            return 0;
        }

        if (apiQuery == null) {
            return definition.getService().count(new NeutralQuery());
        }

        int originalLimit = apiQuery.getLimit();
        int originalOffset = apiQuery.getOffset();
        apiQuery.setLimit(0);
        apiQuery.setOffset(0);
        long count = definition.getService().count(apiQuery);
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
    public String postEntity(final String resource, EntityBody entity) {
        EntityDefinition definition = getEntityDefinition(resource);

        return definition.getService().create(entity);
    }

    public EntityDefinition getEntityDefinition(final String resource) {
        //FIXME TODO
        final String resourceType = resource.split("/")[1];
        return entityDefinitionStore.lookupByResourceName(resourceType);
    }

    @Override
    public String getEntityType(String resource) {
        return entityDefinitionStore.lookupByResourceName(resource).getType();
    }

    @Override
    // TODO: change from UriInfo
    public List<EntityBody> getEntities(final String base, final String id, final String resource, final URI requestURI,
                                        MultivaluedMap<String, String> queryParams) {
        final EntityDefinition definition = getEntityDefinition(resource);
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
    public List<EntityBody> getEntities(String base, String id, String association, String resource, UriInfo uriInfo) {
        final EntityDefinition finalEntity = getEntityDefinition(resource);
        final EntityDefinition  assocEntity= getEntityDefinition(association);
        final String associationKey = getConnectionKey(base, association);

        List<String> valueList = Arrays.asList(id.split(","));
        final ApiQuery apiQuery = getApiQuery(assocEntity, uriInfo.getRequestUri(),);
        apiQuery.addCriteria(new NeutralCriteria(associationKey, "in", valueList));

        final String resourceKey = getConnectionKey(association,resource);
        final ArrayList<String> filteredIdList = new ArrayList<String>();
        for(EntityBody entityBody : assocEntity.getService().list(apiQuery)) {
           filteredIdList.add(entityBody.get(resourceKey).toString());
        }
        List<EntityBody> entityBodyList;
        final ApiQuery finalApiQuery = getApiQuery(finalEntity, uriInfo.getRequestUri(),);
        finalApiQuery.addCriteria(new NeutralCriteria("_id", "in", valueList));
        try {
            entityBodyList = logicalEntity.getEntities(apiQuery, definition.getResourceName());
        } catch (final UnsupportedSelectorException e) {
            entityBodyList = (List<EntityBody>) finalEntity.getService().list(apiQuery);
        }
        return entityBodyList;
    }

    private String getConnectionKey(final String fromEntity, final String toEntity) {
        final EntityDefinition toEntityDef = getEntityDefinition(toEntity);
        final EntityDefinition fromEntityDef = getEntityDefinition(fromEntity);
        ClassType fromEntityType = provider.getClassType(StringUtils.capitalize(fromEntityDef.getType()));
        ClassType toEntityType = provider.getClassType(StringUtils.capitalize(toEntityDef.getType()));
        return provider.getConnectionPath(fromEntityType,toEntityType);
    }

}

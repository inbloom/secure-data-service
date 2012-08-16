package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.service.query.UriInfoToApiQueryConverter;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private ModelProvider provider;

    @Autowired
    private ResourceHelper resourceHelper;


    public EntityBody getEntity(String resource, String id) {
        EntityDefinition definition = getEntityDefinition(resource);

        return definition.getService().get(id);
    }

    public List<EntityBody> getEntities(String resource) {
        EntityDefinition definition = getEntityDefinition(resource);

        return (List<EntityBody>) definition.getService().list(new NeutralQuery());
    }

    @Override
    public String postEntity(String resource, EntityBody entity) {
        EntityDefinition definition = getEntityDefinition(resource);

        return definition.getService().create(entity);
    }

    public EntityDefinition getEntityDefinition(String resource) {
        return entityDefinitionStore.lookupByResourceName(resource);
    }
    public List<EntityDefinition> getEntities(String resource,UriInfo uriInfo) {
        List<EntityDefinition> result = null;
       EntityDefinition entityDefinition = getEntityDefinition(resource);
        ApiQuery apiQuery = new ApiQuery(uriInfo);
        apiQuery = addCriteria(apiQuery,uriInfo);
        return result;
    }

    private ApiQuery addCriteria(ApiQuery apiQuery,UriInfo uriInfo) {
        List<String> ids = resourceHelper.getIds(uriInfo);
        return apiQuery;
    }

}

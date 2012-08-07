package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
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

        return definition.getService().create(entity);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EntityDefinition getEntityDefinition(String resource) {
        return entityDefinitionStore.lookupByResourceName(resource);
    }
}

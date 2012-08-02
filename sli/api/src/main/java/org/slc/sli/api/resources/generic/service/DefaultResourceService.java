package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/2/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
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

    protected EntityDefinition getEntityDefinition(String resource) {
        return entityDefinitionStore.lookupByResourceName(resource);
    }
}

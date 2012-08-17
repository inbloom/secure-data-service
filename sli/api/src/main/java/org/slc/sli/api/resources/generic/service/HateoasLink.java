package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/17/12
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class HateoasLink {

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    public List<EntityBody> add(final String resource, List<EntityBody> entities, final UriInfo uriInfo) {

        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource);

        for (EntityBody entity : entities) {
            entity.put(ResourceConstants.LINKS,
                    ResourceUtil.getLinks(entityDefinitionStore, definition, entity, uriInfo));
        }

        return entities;
    }
}

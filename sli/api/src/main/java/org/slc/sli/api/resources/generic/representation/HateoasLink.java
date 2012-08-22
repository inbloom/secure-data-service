package org.slc.sli.api.resources.generic.representation;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Helper class for adding hateoas links to entities
 *
 * @author srupasinghe
 */

@Component
public class HateoasLink {

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    public List<EntityBody> add(final String resource, List<EntityBody> entities, final UriInfo uriInfo) {

        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource);

        for (EntityBody entity : entities) {
            List<EmbeddedLink> links = ResourceUtil.getLinks(entityDefinitionStore, definition, entity, uriInfo);

            if (!links.isEmpty()) {
                entity.put(ResourceConstants.LINKS, links);
            }
        }

        return entities;
    }
}

package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Adds custom data to the entities
 *
 */
@Component
public class CustomEntityDecorator implements EntityDecorator {

    @Override
    public EntityBody decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams) {
        // TODO
        final Boolean includeCustomEntity = Boolean.valueOf(queryParams.get(ParameterConstants.INCLUDE_CALCULATED).get(0));

        if (includeCustomEntity) {
            String entityId = (String) entity.get("id");
            EntityBody custom = definition.getService().getCustom(entityId);
            if (custom != null) {
                entity.put(ResourceConstants.CUSTOM, custom);
            }
        }

        return entity;
    }
}

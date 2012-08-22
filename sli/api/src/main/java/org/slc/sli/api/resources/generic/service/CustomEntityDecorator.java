package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * Adds custom data to the entities
 *
 */
@Component
public class CustomEntityDecorator implements EntityDecorator {

    @Override
    public void decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams) {
        List<String> params = queryParams.get(ParameterConstants.INCLUDE_CUSTOM);
        final Boolean includeCustomEntity = Boolean.valueOf((params != null) ? params.get(0) : "false");

        if (includeCustomEntity) {
            String entityId = (String) entity.get("id");
            EntityBody custom = definition.getService().getCustom(entityId);
            if (custom != null) {
                entity.put(ResourceConstants.CUSTOM, custom);
            }
        }
    }
}

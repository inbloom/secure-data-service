package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.CalculatedData;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Adds calculated data to the entities
 *
 */
@Component
public class CalculatedValuesDecorator implements EntityDecorator {
    @Override
    public EntityBody decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams) {
        boolean includeCalculatedValues = "true".equals(queryParams.get(ParameterConstants.INCLUDE_CALCULATED));

        if (includeCalculatedValues) {
            String entityId = (String) entity.get("id");
            CalculatedData<String> calculatedValues = definition.getService().getCalculatedValues(entityId);
            if (calculatedValues != null) {
                entity.put(ResourceConstants.CALCULATED_VALUE_TYPE, calculatedValues.getCalculatedValues());
            }
        }

        return entity;
    }
}

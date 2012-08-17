package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.CalculatedData;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

/**
 * Adds aggregate data to the entities
 *
 */
@Component
public class AggregateEntityDecorator implements EntityDecorator {
    @Override
    public EntityBody decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams) {
        boolean includeAggregateValues = definition.supportsAggregates() &&
                "true".equals(queryParams.get(ParameterConstants.INCLUDE_AGGREGATES));

        if (includeAggregateValues) {
            String entityId = (String) entity.get("id");
            CalculatedData<Map<String, Integer>> aggregates = definition.getService().getAggregates(entityId);
            if (aggregates != null) {
                entity.put(ResourceConstants.AGGREGATE_VALUE_TYPE, aggregates.getCalculatedValues());
            }
        }

        return entity;
    }
}

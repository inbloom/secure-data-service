package org.slc.sli.api.config;


import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;

/**
 * Add the entity type to the response body
 */
@Component
public class TypeTreatment implements Treatment {
    private static final String TYPE_STRING = "entityType";

    @Override
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        exposed.remove(TYPE_STRING);
        return exposed;
    }

    @Override
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity) {
        stored.put(TYPE_STRING, defn.getType());
        return stored;
    }

}

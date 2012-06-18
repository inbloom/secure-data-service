package org.slc.sli.api.config;

import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;

/**
 * Add the entity ID to the response body
 *
 * @author nbrown
 *
 */
@Component
public class IdTreatment implements Treatment {
    private static final String ID_STRING = "id";

    @Override
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        exposed.remove(ID_STRING);
        return exposed;
    }

    @Override
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity) {
        stored.put(ID_STRING, entity.getEntityId());
        return stored;
    }

}

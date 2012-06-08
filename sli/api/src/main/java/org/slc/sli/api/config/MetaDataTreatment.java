package org.slc.sli.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Add the "updated" and "created" fields to the response body.
 *
 * @author jnanney
 *
 */
@Component
public class MetaDataTreatment implements Treatment {

    @Autowired
    private Repository<Entity> repo;

    public static final String METADATA = "metaData";


    @Override
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        exposed.remove(METADATA);
        return exposed;
    }

    @Override
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity) {
        stored.put(METADATA, entity.getMetaData());
        
        return stored;
    }

}

package org.slc.sli.api.config;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;

public class IdTreatment implements Treatment {
    private static final String ID_STRING = "id";

    @Override
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        exposed.remove(ID_STRING);
        return exposed;
    }

    @Override
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, String id) {
        stored.put(ID_STRING, id);
        return stored;
    }
    

}

package org.slc.sli.api.representation;

import java.util.HashMap;

/**
 * Contents of entities, for use in XML return
 */
public class Entities extends HashMap<String, Object> {
    
    private static final long serialVersionUID = -8766900333518618999L;
    
    public Entities(EntityBody entityBody, String entityCollectionName) {
        super();
        this.put(entityCollectionName, entityBody);
    }
}

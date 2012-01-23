package org.slc.sli.api.representation;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Contents of entities, for use in XML return
 */
public class Entities extends HashMap<String, Object> {
    
    private static final long serialVersionUID = -8766900333518618999L;

    public Entities(String entityCollectionName, EntityBody entityBody) {
        super();
        this.put(entityCollectionName, entityBody);
    }

    public HashMap<String, Object> getEntityBody() {
        Entry<String, Object> entityCollection = this.entrySet().iterator().next();
        @SuppressWarnings(value="unchecked")
        HashMap<String, Object> entityBody = (HashMap<String, Object>) entityCollection.getValue();
        return entityBody;
    }
}

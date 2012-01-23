package org.slc.sli.api.representation;

import java.util.HashMap;

/**
 * Contents of Home resource, for use in XML return
 */
public class Home extends HashMap<String, Object> {
    
    private static final long serialVersionUID = -8766900333518618999L;
    
    public Home(HashMap<String, Object> linksMap, String entityCollectionName) {
        super();
        this.put(entityCollectionName, linksMap);
    }
}

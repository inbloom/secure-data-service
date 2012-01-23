package org.slc.sli.api.representation;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Contents of Home resource, for use in XML return
 */
//public class Home extends MutablePair<String, HashMap<String, Object>> {
public class Home extends HashMap<String, Object> {
    
    private static final long serialVersionUID = -8766900333518618999L;

    public Home(String entityCollectionName, HashMap<String, Object> linksMap) {
        super();
        this.put(entityCollectionName, linksMap);
    }

    public HashMap<String, Object> getLinksMap() {
        Entry<String, Object> collectionEntry = this.entrySet().iterator().next();
        @SuppressWarnings(value = "unchecked")
        HashMap<String, Object> linksMap = (HashMap<String, Object>) collectionEntry.getValue();
        return linksMap;
    }
}

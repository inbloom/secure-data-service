package org.slc.sli.entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Simple application entity
 *       
 * @author David Wu dwu@wgen.net
 * @author Robert Bloh rbloh@wgen.net
 * 
 */
public class GenericEntity<String, Object> extends LinkedHashMap<String, Object> implements Map<String, Object> {
    
    public GenericEntity() {  
        super();
    }
    
    public GenericEntity(Map<String, Object> map) {  
        super(map);
    }
    
    public void appendToList(String key, GenericEntity obj) {
        if (!containsKey(key)) {
            put(key, (Object) new ArrayList<GenericEntity>());
        }
        ArrayList<GenericEntity> list = (ArrayList<GenericEntity>) get(key);
        list.add(obj);
        put(key, (Object) list);
    }
}

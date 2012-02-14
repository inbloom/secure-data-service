package org.slc.sli.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple application entity
 *       
 * @author David Wu dwu@wgen.net
 * @author Robert Bloh rbloh@wgen.net
 * 
 */
public class GenericEntity extends LinkedHashMap<String, Object> {
    
    public GenericEntity() {  
        super();
    }
    
    public GenericEntity(Map<String, Object> map) {  
        super(map);
    }
    
    public String getString(String key) {
        return (String) (get(key));
    }
    
    public Map getMap(String key) {
        return (Map) (get(key));    
    }
    
    public List getList(String key) {
        return (List) (get(key));
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

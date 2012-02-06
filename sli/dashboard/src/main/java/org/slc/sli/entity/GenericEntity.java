package org.slc.sli.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

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
    
}

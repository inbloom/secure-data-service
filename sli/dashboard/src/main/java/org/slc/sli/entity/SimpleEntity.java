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
public class SimpleEntity<String, Object> extends LinkedHashMap<String, Object> implements Map<String, Object> {
    
    public SimpleEntity() {  
        super();
    }
    
    public SimpleEntity(Map<String, Object> map) {  
        super(map);
    }
    
}

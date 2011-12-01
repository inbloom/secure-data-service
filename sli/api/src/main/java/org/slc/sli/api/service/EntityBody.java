package org.slc.sli.api.service;

import java.util.Map;

/**
 * Contents of an entity body
 * 
 * @author nbrown
 * 
 */
public class EntityBody {
    
    private final Map<String, Object> contents;
    
    public EntityBody(Map<String, Object> contents) {
        super();
        this.contents = contents;
    }
    
    public Map<String, Object> getContents() {
        return contents;
    }
    
}

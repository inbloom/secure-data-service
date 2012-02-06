package org.slc.sli.entity;

import java.util.Map;

import org.slc.sli.domain.Entity;

/**
 * Generic class for all domain entities
 *       
 * @author David Wu
 * 
 */
public class GenericEntity implements Entity {
    
    String type;
    String entityId;
    Map<String, Object> body;
    
    public GenericEntity() {        
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    /**
     * @return the entity type as string, can be entity type for
     *         core entity or association entity
     */
    public String getType() {
        return type;
    }
    
    /**
     * @return the global unique id of the entity as string
     */
    public String getEntityId() {
        return entityId;
    }
    
    /**
     * @return the entity body wrapped by a map
     */
    public Map<String, Object> getBody() {
        return body;
    }

}

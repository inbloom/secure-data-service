package org.slc.sli.entity;

import java.util.Map;

/**
 * Define the entity interface that provides getter method to retrieve the fields
 * for entities including core entities and association entities
 * 
 * TODO: Not sure whether we should depend on the Entity class in common/domain
 *       For now, I'm creating a standalone class
 *       
 * @author David Wu
 * 
 */
public class GenericEntity {
    
    String type;
    String entityId;
    Map<String, Object> body;
    
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

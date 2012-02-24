package org.slc.sli.api.client;

import java.util.Map;

/**
 * Generic entity returned by the SLI API ReSTful service. Each entity has a unique
 * identifier, a data collection, and a collection of resource links. This interface provides
 * the most stripped down interface required by all entities.
 * 
 * @author asaarela
 */
public interface Entity {
    
    /**
     * Get the type for this entity.
     */
    EntityType getEntityType();
    
    /**
     * Get the ID for the entity.
     * 
     * @return id String
     */
    String getId();
    
    /**
     * Get the data associated with this entity.
     * 
     * @return Map of data.
     */
    Map<String, Object> getData();
    
    /**
     * Get a list of links for this entity.
     * 
     * @return a map of link type name to Link instance.
     */
    Map<String, Link> getLinks();
    
}

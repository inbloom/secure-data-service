package org.slc.sli.api.client;

import java.util.List;
import java.util.Map;

/**
 * Generic entity returned by the SLI API ReSTful service. Each entity has a unique
 * identifier, a data collection, and a collection of resource links. This interface provides
 * the most stripped down interface required by all entities.
 * 
 * @author asaarela
 */
public interface Entity {
    
    /** Key to locate 'links' field of the Entity. */
    public static final String LINKS_KEY = "links";
    
    /** Key to locate the Entity's id field */
    public static final String ENTITY_ID_KEY = "id";
    
    /**
     * Get the data associated with this entity.
     * 
     * @return Map of data.
     */
    Map<String, Object> getData();
    
    /**
     * Standard helper functions.
     */
    
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
     * Get a list of links for this entity.
     * 
     * @return a List of links.
     */
    List<Link> getLinks();
    
}

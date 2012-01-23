package org.slc.sli.api.service;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.Entity;

/**
 * Core service for retrieving entities in DB
 */
public interface CoreEntityService {
    
    /**
     * Create an entity and store it in the data store
     * 
     * @param body
     *            the body of the entity
     * @param type
     *            the type of the entity
     * @return id of the new entity
     */
    public String create(EntityBody body, String type);
    
    /**
     * Retrieves an entity from the data store
     * 
     * @param id
     *            the id of the entity to retrieve
     * @return the body of the entity
     */
    public Entity get(String id);
    
    /**
     * Change an entity in the data store
     * 
     * @param entity
     *            the entity to update
     * @param updates
     *            the updates to body of the entity
     * @return if the entity was changed
     */
    public boolean update(Entity entity, EntityBody updates);
    
    /**
     * Delete an entity from the data store
     * 
     * @param id
     *            id of the entity to delete
     * @return if the entity was deleted
     */
    public boolean delete(String id);
    
}

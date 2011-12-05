package org.slc.sli.api.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;

/**
 * Service for retrieving entities in DB
 * 
 * @author nbrown
 * 
 */
public interface EntityService {
    
    /**
     * Create an entity and store it in the data store
     * 
     * @param content
     *            the body of the entity
     * @return id of the new entity
     */
    public String create(EntityBody content);
    
    /**
     * Delete an entity from the data store
     * 
     * @param id
     *            the id of the entity to delete
     * @return if the delete was successful
     */
    public boolean delete(String id);
    
    /**
     * Change an entity in the data store
     * 
     * @param id
     *            the id of the entity to update
     * @param content
     *            the new body of the entity
     * @return if the entity was changed
     */
    public boolean update(String id, EntityBody content);
    
    /**
     * Retrieves an entity from the data store
     * 
     * @param id
     *            the id of the entity to retrieve
     * @return the body of the entity
     */
    public EntityBody get(String id);
    
    /**
     * Get multiple entities from the data store
     * 
     * @param ids
     *            the ids of the entities to retrieve
     * @return the entities matching the given ids
     */
    public Iterable<EntityBody> get(Iterable<String> ids);
    
    /**
     * List the ids of the entities in the data store
     * 
     * @return the ids of the entities in the data store
     */
    public Iterable<String> list(int start, int numResults);
    
    /**
     * Get the entities associated with a given entity in the data store
     * 
     * @param id
     *            the id of the entity to look for associations for
     * @param assocType
     *            the type of associated entities to look for
     * @return the ids of associated entities
     */
    public Iterable<String> getAssociated(String id, EntityDefinition assocType, int start, int numResults);
}

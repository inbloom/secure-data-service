package org.slc.sli.api.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.NeutralQuery;



/**
 * Service for retrieving entities in DB
 * 
 * @author nbrown
 * 
 */
public interface EntityService {
    
    /**
     * Returns number of entities stored.
     * 
     * @return number of entities that have been stored/exist
     */
    public long count(NeutralQuery neutralQuery);
    
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
     */
    public void delete(String id);
    
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
     * Retrieves an entity from the data store with certain fields added/removed.
     * 
     * @param id
     *            the id of the entity to retrieve
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public EntityBody get(String id, NeutralQuery neutralQuery);

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     * 
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public Iterable<EntityBody> list(NeutralQuery neutralQuery);

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     * 
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public Iterable<String> listIds(NeutralQuery neutralQuery);
    
    /**
     * Get multiple entities from the data store
     * 
     * @param ids
     *            the ids of the entities to retrieve
     * @return the entities matching the given ids
     */
    public Iterable<EntityBody> get(Iterable<String> ids);
    
    /**
     * Get multiple entities from the data store
     * 
     * @param ids
     *            the ids of the entities to retrieve
     * @param sortBy
     *            field to sort the ids by
     * @param sortOrder
     *            sort order
     * @return the entities matching the given ids
     */
    public Iterable<EntityBody> get(Iterable<String> ids, NeutralQuery neutralQuery);
    
    /**
     * Whether or not an element exists with the given id
     * 
     * @param id
     *            the id to check
     * @return true iff there is an entity with this id
     */
    public boolean exists(String id);
    
    /**
     * Retrieve entity definition
     * 
     * @return the definition of the entity
     */
    public EntityDefinition getEntityDefinition();
    
}

package org.slc.sli.api.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.query.SortOrder;

import java.util.Map;


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
    public EntityBody get(String id, Map<String, String> queryParameters);

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     * 
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public Iterable<EntityBody> list(Map<String, String> queryParameters);
    
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
    public Iterable<EntityBody> get(Iterable<String> ids, String sortBy, SortOrder sortOrder);
    
    /**
     * List the ids of the entities in the data store
     * 
     * @param start
     *            the index of the first index to return
     * @param numResults
     *            the number of results to return
     * @return the ids of the entities in the data store
     */
    public Iterable<String> list(int start, int numResults);
    
    /**
     * List the ids of the entities in the data store, filtered by a query
     * 
     * @param start
     *            the index of the first index to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the string to query against
     * @return a list of ids of matching entities
     */
    public Iterable<String> list(int start, int numResults, String queryString);
    
    /**
     * List the ids of the entities in the data store, filtered by a query
     * 
     * @param start
     *            the index of the first index to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the string to query against
     * @param sortOrder
     *            the field to sort against
     * @param sortOrder
     *            the order of the sort
     * @return a list of ids of matching entities
     */
    public Iterable<String> list(int start, int numResults, String queryString, String sortBy, SortOrder sortOrder);
    
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

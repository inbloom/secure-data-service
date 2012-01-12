package org.slc.sli.dal.repository;

import java.util.Map;

import org.slc.sli.domain.Entity;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Define the entity repository interface that provides basic CRUD and field
 * query methods for entities including core entities and association entities
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */
public interface EntityRepository {
    
    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @param id
     *            the global unique id of the entity
     * @return the entity retrieved
     */
    public Entity find(String entityType, String id);
    
    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @param skip
     *            the beginning index of the entity that will be returned
     * @param max
     *            the max number of entities that will be returned
     * @return the collection of entities
     */
    public Iterable<Entity> findAll(String entityType, int skip, int max);
    
    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @return the collection of entities
     */
    public Iterable<Entity> findAll(String entityType);
    
    /**
     * @param collection
     *            the collection the entity is in
     * @param entity
     *            the entity that will be updated
     */
    public void update(String collection, Entity entity);
    
    /**
     * Create an entry with the collection set to the type name
     * 
     * @param type
     *            the type of entity to be persisted
     * @param body
     *            the entity body that will be persisted
     * @return the entity that has been persisted
     */
    public Entity create(String type, Map<String, Object> body);

    /**
     * @param type
     *            the type of entity to be persisted
     * @param body
     *            the entity body that will be persisted
     * @param collectioName
     *            the name of the collection to save it into
     * @return the entity that has been persisted
     */
    public Entity create(String type, Map<String, Object> body, String collectionName);
    
    /**
     * @param entityType
     *            the entity type need to be deleted, can be entity type for
     *            core entity or association entity
     * @param id
     *            the global unique id of the entity
     */
    public void delete(String entityType, String id);
    
    /**
     * @param entityType
     *            the entity type need to be deleted, can be entity type for
     *            core entity or association entity
     */
    public void deleteAll(String entityType);
    
    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @param fields
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("firstName","Jane")
     * @param skip
     *            the beginning index of the entity that will be returned
     * @param max
     *            the max number of entities that will be returned
     * @return the collection of entities
     */
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields, int skip, int max);
    
    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @param fields
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("firstName","Jane")
     * @return the collection of entities
     */
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields);
    
    

    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @param query
     *            the query to filter returned collection results
     * @param skip
     *            the beginning index of the entity that will be returned
     * @param max
     *            the max number of entities that will be returned
     * 
     * @return the collection of entities
     */
    public Iterable<Entity> findByFields(String entityType, Query query, int skip, int max);

    public boolean matchQuery(String entityType, String id, Query query);

}

package org.slc.sli.dal.repository;

import java.util.Map;

import org.slc.sli.domain.Entity;

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
     * @param type
     *            the type of entity to be persisted
     * @param body
     *            the entity body that will be persisted
     * @return the entity that has been persisted
     */
    public Entity create(String type, Map<String, Object> body);
    
    /**
     * @param entityType
     *            the entity type need to be deleted, can be entity type for
     *            core entity or association entity
     * @param id
     *            the global unique id of the entity
     */
    void delete(String entityType, String id);
    
    /**
     * @param entityType
     *            the entity type need to be deleted, can be entity type for
     *            core entity or association entity
     */
    void deleteAll(String entityType);
    
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
    Iterable<Entity> findByFields(String entityType, Map<String, String> fields, int skip, int max);
    
    /**
     * @param entityType
     *            the entity type need to be retrieved, can be entity type for
     *            core entity or association entity
     * @param fields
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("firstName","Jane")
     * @return the collection of entities
     */
    Iterable<Entity> findByFields(String entityType, Map<String, String> fields);
    
}

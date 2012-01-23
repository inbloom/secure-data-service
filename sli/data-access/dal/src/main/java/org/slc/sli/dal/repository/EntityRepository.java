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
     * @param collectioName
     *            the name of the collection to look in
     * @param id
     *            the global unique id of the entity
     * @return the entity retrieved
     */
    public Entity find(String collectioName, String id);
    
    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param skip
     *            the beginning index of the entity that will be returned
     * @param max
     *            the max number of entities that will be returned
     * @return the collection of entities
     */
    public Iterable<Entity> findAll(String collectionName, int skip, int max);
    
    /**
     * @param collectioName
     *            the name of the collection to look in
     * @return the collection of entities
     */
    public Iterable<Entity> findAll(String collectioName);
    
    /**
     * @param collection
     *            the collection the entity is in
     * @param entity
     *            the entity that will be updated
     * @return whether or not the entity was updated
     */
    public boolean update(String collection, Entity entity);
    
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
     * @param collectionName
     *            the name of the collection to delete from
     * @param id
     *            the global unique id of the entity
     */
    public boolean delete(String collectionName, String id);
    
    /**
     * @param collectionName
     *            the name of the collection to delete from
     */
    public void deleteAll(String collectionName);
    
    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param fields
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("firstName","Jane")
     * @param skip
     *            the beginning index of the entity that will be returned
     * @param max
     *            the max number of entities that will be returned
     * @return the collection of entities
     */
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields, int skip, int max);
    
    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param fields
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("firstName","Jane")
     * @return the collection of entities
     */
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields);
    
    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            the query to filter returned collection results
     * @param skip
     *            the beginning index of the entity that will be returned
     * @param max
     *            the max number of entities that will be returned
     * 
     * @return the collection of entities
     */
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max);
    
    /*
     * matchQuery method is a temporary solution for association/sourceGUID/targets type of
     * filtering as the current data model does not have any reference between source entity and
     * target entity. since mongodb does not provide any join collection query and the current
     * collection configuration map each entity type to one collection, do the filtering on
     * association/sourceGUID/targets will be difficult to accomplish directly. matchQuery method
     * is used to check if the id with specific type matches the query after the id of the target
     * entity has been retrieved by traversal from source entity -> association entity -> target
     * entity
     */
    
    /**
     * @param collectioName
     *            the name of the collection to look in
     * @param id
     *            the global unique id of the entity
     * @param query
     *            the query for checking if specified entity matches
     * @return true if specified entity matches query, otherwise return false
     */
    public boolean matchQuery(String collectioName, String id, Query query);
    
}

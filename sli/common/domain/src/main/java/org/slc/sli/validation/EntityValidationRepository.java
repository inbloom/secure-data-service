package org.slc.sli.validation;

import java.util.Map;

import org.slc.sli.domain.Entity;

/**
 * Interface for basic read/query methods for entities to be used in validation
 * @author srupasinghe
 *
 */
public interface EntityValidationRepository {
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
}

package org.slc.sli.domain;

import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;

/**
 * Define the object repository interface that provides basic CRUD and field
 * query methods for objects including core objects and association objects
 *
 * @author Dong Liu dliu@wgen.net
 *
 */
public interface Repository<T> {

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param id
     *            the global unique id of the object
     * @return the object retrieved
     */
    public T find(String collectionName, String id);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param id
     *            the global unique id of the object
     * @param query
     *            all parameters to be included in query
     * @return the object retrieved
     */
    public T find(String collectionName, Map<String, String> query);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            all parameters to be included in query
     * @return the object retrieved
     */
    public Iterable<T> findAll(String collectionName, Map<String, String> query);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            all parameters to be included in query
     * @return the object retrieved
     */
    public Iterable<T> findAll(String collectionName, SmartQuery query);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param skip
     *            the beginning index of the object that will be returned
     * @param max
     *            the max number of objects that will be returned
     * @return the collection of objects
     */
    public Iterable<T> findAll(String collectionName, int skip, int max);

    /**
     * @param collectioName
     *            the name of the collection to look in
     * @return the collection of objects
     */
    public Iterable<T> findAll(String collectioName);

    /**
     * @param collection
     *            the collection the object is in
     * @param object
     *            the object that will be updated
     * @return whether or not the object was updated
     */
    public boolean update(String collection, T object);

    /**
     * Create an entry with the collection set to the type name
     *
     * @param type
     *            the type of object to be persisted
     * @param body
     *            the object body that will be persisted
     * @return the object that has been persisted
     */
    public T create(String type, Map<String, Object> body);

    /**
     * @param type
     *            the type of object to be persisted
     * @param body
     *            the object body that will be persisted
     * @param collectioName
     *            the name of the collection to save it into
     * @return the object that has been persisted
     */
    public T create(String type, Map<String, Object> body, String collectionName);

    /**
     * @param type
     *            the type of object to be persisted
     * @param body
     *            the object body that will be persisted
     * @param metaData
     *            the object meta data that will be persisted
     * @param collectionName
     *            the name of the collection to save it into
     * @return the object that has been persisted
     */
    public T create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName);

    /**
     * @param collectionName
     *            the name of the collection to delete from
     * @param id
     *            the global unique id of the object
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
     *            the beginning index of the object that will be returned
     * @param max
     *            the max number of objects that will be returned
     * @return the collection of objects
     */
    public Iterable<T> findByFields(String collectionName, Map<String, String> fields, int skip, int max);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param paths
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("body.firstName","Jane"),
     *            or new HashMap().put("metadata.regionId","Region")
     * @param skip
     *            the beginning index of the object that will be returned
     * @param max
     *            the max number of objects that will be returned
     * @return the collection of objects
     */
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths, int skip, int max);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param fields
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("firstName","Jane")
     * @return the collection of objects
     */
    public Iterable<T> findByFields(String collectionName, Map<String, String> fields);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param paths
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("body.firstName","Jane"),
     *            or new HashMap().put("metadata.regionId","Region")
     * @return the collection of objects
     */
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            the query to filter returned collection results
     * @param skip
     *            the beginning index of the object that will be returned
     * @param max
     *            the max number of objects that will be returned
     *
     * @return the collection of objects
     */
    public Iterable<T> findByQuery(String collectionName, Query query, int skip, int max);

    /**
     * Fetches first element from given query
     *
     * @param collectionName
     * @param query
     * @return
     */
    public T findOne(String collectionName, Query query);

    /**
     * Get the number of elements in the collection matching a particular query
     *
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            the query to look for
     * @return the number of objects matching the query in the collection
     */
    public long count(String collectionName, Query query);

    /**
     * Filter a collection of IDs by
     *
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            the query used to find objects
     * @param skip
     *            start index of the object that will be returned
     * @param max
     *            maximum number of results returned
     * @return
     */
    public Iterable<String> findIdsByQuery(String collectionName, Query query, int skip, int max);

}

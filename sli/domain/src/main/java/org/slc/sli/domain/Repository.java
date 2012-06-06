package org.slc.sli.domain;

import java.util.Map;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.springframework.data.mongodb.core.index.IndexDefinition;
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
     *            the name of the collection to look in
     * @param id
     *            the global unique id of the object
     * @return the object retrieved
     */
    public T findById(String collectionName, String id);

    public boolean exists(String collectionName, String id);

    /**
     * Fetches first element from given query
     *
     * @param collectionName
     * @param query
     * @return
     */
    public T findOne(String collectionName, NeutralQuery neutralQuery);

    /**
     * Get the number of elements in the collection matching a particular query
     *
     * @param collectionName
     *            the name of the collection to look in
     * @return the collection of objects
     */
    public Iterable<T> findAll(String collectionName);

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
    public Iterable<T> findAll(String collectionName, NeutralQuery neutralQuery);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param paths
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("body.firstName","Jane"),
     *            or new HashMap().put("metadata.tenantId","Region")
     * @return the collection of objects
     */
    public Iterable<T> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery);

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
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery);

    /**
     * Get the number of elements in the collection matching a particular query
     *
     * @param collectionName
     *            the name of the collection to look in
     * @param query
     *            the query to look for
     * @return the number of objects matching the query in the collection
     */
    public long count(String collectionName, NeutralQuery neutralQuery);

    /**
     * @param collection
     *            the collection the object is in
     * @param object
     *            the object that will be updated
     * @return whether or not the object was updated
     */
    public boolean update(String collection, T object);

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
     * Execute a mongo command
     *
     * @param command the command to execute
     * @return the result of that command
     */
    public abstract CommandResult execute(DBObject command);

    /**
     * Get the actual db collection
     *
     * @param collectionName the collection name
     * @return the mongo db collection
     */
    public DBCollection getCollection(String collectionName);

    /**
     * @param collectionName
     *            the name of the collection to look in
     * @param paths
     *            a map with key value pairs as string that define the search
     *            criteria for example: new HashMap().put("body.firstName","Jane"),
     *            or new HashMap().put("metadata.tenantId","Region")
     * @return the collection of objects
     */
    @Deprecated
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
    @Deprecated
    public Iterable<T> findByQuery(String collectionName, Query query, int skip, int max);

    /**check if the collection exists in database
     *
     * @param collection: name of the collection
     * @return
     */
    public boolean collectionExists(String collection);

    /**Create a collection
     *
     * @param collection
     */
    public void createCollection(String collection);

    /**
     * ensureIndex for a collection the database
     *
     * @param index   : the index to be ensured
     * @param collection : name of collection
     */
    public void ensureIndex(IndexDefinition index, String collection);

    /**
     * Supports configuring a write concern on a repository.
     *
     * @param writeConcern
     */
    public void setWriteConcern(String writeConcern);

}

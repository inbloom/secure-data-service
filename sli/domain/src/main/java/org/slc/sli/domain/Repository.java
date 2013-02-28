/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.domain;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

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

    public List<T> insert(List<T> records, String collectionName);

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
     * @param collectionName
     *            the name of the collection to look in
     * @param neutralQuery
     *            the query to filter returned collection results
     *
     * @return the collection of objects
     */
    public Iterable<T> findAll(String collectionName, NeutralQuery neutralQuery);

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
    public boolean update(String collection, T object, boolean isSuperdoc);

    /**
     * Make an update to a single entity through a query
     * Note, this does not go through the validator, caller is expected to ensure the update will
     * keep the object valid
     * It also does not encrypt values, so it cannot be used to update PII data
     *
     * @param collection
     *            the collection the entity is in
     * @param query
     *            the query to make
     * @param update
     *            the update to make
     * @return whether or not the object was updated
     */
    public boolean doUpdate(String collection, NeutralQuery query, Update update);

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
     * @param query TODO
     */
    public void deleteAll(String collectionName, NeutralQuery query);

    /**
     * Get the actual db collection
     *
     * @param collectionName
     *            the collection name
     * @return the mongo db collection
     */
    public DBCollection getCollection(String collectionName);

    /**
     * Get the available collections.
     *
     * @return List<DBCollections> collections.
     */
    public List<DBCollection> getCollections(boolean includeSystemCollections);

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

    /**
     * check if the collection exists in database
     *
     * @param collection
     *            : name of the collection
     * @return
     */
    public boolean collectionExists(String collection);

    /**
     * Supports configuring a write concern on a repository.
     *
     * @param writeConcern
     */
    public void setWriteConcern(String writeConcern);

    /**
     * Support configurability of performing refrence checking as a part of schema validation
     *
     * @param referenceCheck
     */
    public void setReferenceCheck(String referenceCheck);

    public long count(String collectionName, Query query);

    public T createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries);

    public boolean updateWithRetries(String collection, T object, int noOfRetries);

    /**
     * Updates only the provided keys with their values in the target ID'd entity
     * in the specified collection name.
     *
     * @param collectionName
     *            where the entity to be patched can be found
     * @param id
     *            the id of the entity to patch
     * @param newValues
     *            the new values to be persisted to the entity
     * @return true if successful, false otherwise
     */
    boolean patch(String type, String collectionName, String id, Map<String, Object> newValues);

    /**
     * Update all documents matching the query rather than just one.
     *
     * @param query
     * @param update
     * @param entityReferenced
     * @return
     */
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced);

    /**
     *
     * @param collectionName
     * @param neutralQuery
     * @param update
     * @return
     */
    public abstract T findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update);
}

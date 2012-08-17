/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.dal;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Way to retry mongo commands
 *
 * @author tshewchuk
 *
 */
public class RetryMongoCommand {
    private final MongoTemplate template;
    private final Logger LOG;

    public RetryMongoCommand(MongoTemplate template, Logger log) {
        this.template = template;
        this.LOG = log;
    }

    // MongoRepository methods.

    /**
     * Retry n times inserting a record into a collection
     *
     * @param record
     *            record to insert
     * @param collectionName
     *            name of collection to insert new record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public <T> void insertWithRetries(final T record, final String collectionName, int tries) throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times inserting a records batch into a collection
     *
     * @param records
     *            records batch to insert
     * @param collectionName
     *            name of collection to insert new record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public <T> void insertWithRetries(final List<T> records, final String collectionName, int tries)
            throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times finding the record matching the query in a collection
     *
     * @param query
     *            query specifying record to find
     * @param entityClass
     *            class of record entity
     * @param collectionName
     *            name of collection to search for record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return T
     *         record within collection matching query
     */
    public <T> T findOneWithRetries(final Query query, final Class<T> entityClass, final String collectionName,
            int tries) throws MongoException {
        tries = 0;
        return null;
    }

    /**
     * Retry n times finding all records matching the query in a collection
     *
     * @param query
     *            query specifying records to find
     * @param entityClass
     *            class of record entity
     * @param collectionName
     *            name of collection to insert new record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return Iterable<T>
     *         list of records within collection matching query
     */
    public <T> List<T> findWithRetries(final Query query, final Class<T> entityClass, final String collectionName,
            int tries) throws MongoException {
        tries = 0;
        return null;
    }

    /**
     * Retry n times retrieving a collection
     *
     * @param collectionName
     *            name of collection to insert new record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return DBCollection
     *         DBCollection object representing collection
     */
    public DBCollection getCollectionWithRetries(final String collectionName, int tries) throws MongoException {
        tries = 0;
        return null;
    }

    /**
     * Retry n times updating a record in a collection
     *
     * @param record
     *            record to insert
     * @param collectionName
     *            name of collection to insert new record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return WriteResult
     *         WriteResult object representing result of update
     */
    public WriteResult updateFirstWithRetries(final Query query, final Update update, final String collectionName, int tries)
            throws MongoException {
        WriteResult result = null;
        tries = 0;
        return result;
    }

    /**
     * Retry n times removing the record matching the query in a collection
     *
     * @param query
     *            query specifying record to find
     * @param entityClass
     *            class of record entity
     * @param collectionName
     *            name of collection to search for record
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return T
     *         deleted record within collection matching query
     */
    public <T> T findAndRemoveWithRetries(final Query query, final Class<T> entityClass, final String collectionName,
            int tries) throws MongoException {
        tries = 0;
        return null;
    }

    // BatchJobMongoDA methods.

    /**
     * Retry n times removing all records or those of a certain tenant in a collection
     *
     * @param collectionName
     *            name of collection to remove records
     * @param obj
     *            object matching tenant, if not null
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return DBCollection
     *         DBCollection object representing collection
     */
    public void collectionRemoveWithRetries(final String collectionName, DBObject obj, int tries) throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times saving an object into the DB
     *
     * @param objectToSave
     *            object to be saved
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public void saveWithRetries(final Object objectToSave, int tries) throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times saving an object into a clooection in the DB
     *
     * @param objectToSave
     *            object to be saved
     * @param collectionName
     *            name of collection to save object
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public void saveWithRetries(final Object objectToSave, final String collectionName, int tries) throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times inserting a DB object into a collection
     *
     * @param collectionName
     *            name of collection to save object
     * @param object
     *            DBObject to insert
     * @param writeConcern
     *            WriteConcern for insert
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public <T> void collectionInsertWithRetries(final String collectionName, final DBObject object, final WriteConcern writeConcern, int tries)
            throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times removing a DB object into a collection
     *
     * @param collectionName
     *            name of collection to remove object
     * @param object
     *            DBObject to remove
     * @param writeConcern
     *            WriteConcern for removal
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public <T> void collectionRemoveWithRetries(final String collectionName, final DBObject object, final WriteConcern writeConcern, int tries)
            throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times modifying records matching certain query criteria
     *
     * @param collectionName
     *            name of collection to remove records
     * @param query
     *            query specifying records to modify
     * @param obj
     *            parameter to modify
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return DBCollection
     *         DBCollection object representing collection
     */
    public void collectionFindAndModifyWithRetries(final String collectionName, Query query, DBObject obj, int tries) throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times inserting a DB object into a collection
     *
     * @param collectionName
     *            name of collection to save object
     * @param entities
     *            Entities to insert
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public <T> void collectionInsertWithRetries(final String collectionName, final DBObject entities, int tries)
            throws MongoException {
        tries = 0;
    }

    /**
     * Retry n times finding records matching certain query criteria
     *
     * @param collectionName
     *            name of collection to remove records
     * @param ref
     *            parameter specifying find results
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     *
     * @return DBCursor
     *         DBCursor object representing find results
     */
    public DBCursor collectionFindWithRetries(final String collectionName, BasicDBObject ref, int tries) throws MongoException {
        DBCursor cursor = null;
        tries = 0;
        return cursor;
    }

    /**
     * Retry n times removing records matching certain query criteria
     *
     * @param collectionName
     *            name of collection to remove records
     * @param ref
     *            parameter specifying remove results
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    public void collectionRemoveWithRetries(final String collectionName, BasicDBObject ref, int tries) throws MongoException {
        tries = 0;
    }
}

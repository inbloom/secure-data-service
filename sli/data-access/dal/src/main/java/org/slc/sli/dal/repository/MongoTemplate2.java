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


package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoWriter;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.Assert;

/**
 * @author okrook
 *
 */
public class MongoTemplate2 extends MongoTemplate {

    /**
     * Constructor used for a basic template configuration
     *
     * @param mongo
     * @param databaseName
     */
    public MongoTemplate2(Mongo mongo, String databaseName) {
        super(mongo, databaseName);
    }

    /**
     * Constructor used for a template configuration with user credentials in the form of
     * {@link org.springframework.data.authentication.UserCredentials}
     *
     * @param mongo
     * @param databaseName
     * @param userCredentials
     */
    public MongoTemplate2(Mongo mongo, String databaseName, UserCredentials userCredentials) {
        super(mongo, databaseName, userCredentials);
    }

    /**
     * Constructor used for a basic template configuration
     *
     * @param mongoDbFactory
     */
    public MongoTemplate2(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }

    /**
     * Constructor used for a basic template configuration.
     *
     * @param mongoDbFactory
     * @param mongoConverter
     */
    public MongoTemplate2(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) {
        super(mongoDbFactory, mongoConverter);
    }

    public void insert(Collection<? extends Object> batchToSave, String collectionName, WriteConcern writeConcern) {
        doInsertBatch(collectionName, batchToSave, this.getConverter(), writeConcern);
    }

    protected <T> void doInsertBatch(String collectionName, Collection<? extends T> batchToSave, MongoWriter<T> writer, WriteConcern writeConcern) {

        Assert.notNull(writer);

        List<DBObject> dbObjectList = new ArrayList<DBObject>();
        for (T o : batchToSave) {
            BasicDBObject dbDoc = new BasicDBObject();

            maybeEmitEvent(new BeforeConvertEvent<T>(o));
            writer.write(o, dbDoc);

            maybeEmitEvent(new BeforeSaveEvent<T>(o, dbDoc));
            dbObjectList.add(dbDoc);
        }
        List<ObjectId> ids = insertDBObjectList(collectionName, dbObjectList, writeConcern);
        int i = 0;
        for (T obj : batchToSave) {
            if (i < ids.size()) {
                populateIdIfNecessary(obj, ids.get(i));
                maybeEmitEvent(new AfterSaveEvent<T>(obj, dbObjectList.get(i)));
            }
            i++;
        }
    }

    protected List<ObjectId> insertDBObjectList(String collectionName, final List<DBObject> dbDocList, final WriteConcern writeConcern) {
        if (dbDocList.isEmpty()) {
            return Collections.emptyList();
        }

        execute(collectionName, new CollectionCallback<Void>() {
            @Override
            public Void doInCollection(DBCollection collection) throws MongoException, DataAccessException {
                collection.insert(dbDocList.toArray((DBObject[]) new BasicDBObject[dbDocList.size()]), writeConcern);

                return null;
            }
        });

        List<ObjectId> ids = new ArrayList<ObjectId>();
        for (DBObject dbo : dbDocList) {
            Object id = dbo.get("_id");
            if (id instanceof ObjectId) {
                ids.add((ObjectId) id);
            } else {
                // no id was generated
                ids.add(null);
            }
        }
        return ids;
    }
}

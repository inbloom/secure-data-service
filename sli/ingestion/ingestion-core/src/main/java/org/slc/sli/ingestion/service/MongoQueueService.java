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


package org.slc.sli.ingestion.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.queue.ItemKeys;
import org.slc.sli.ingestion.queue.ItemValues;

/**
 * Simple queue service that uses Mongo to store the queued work items.
 *
 * @author smelody
 *
 */
@Component
public class MongoQueueService implements QueueService {

    /**
     * Injecting the templates to get access to the DB. Spring data 1.0.0 M 4 has no support for
     * findAndModify
     */
    @Autowired
    private MongoTemplate batchJobMongoTemplate;

    private static final String COLLECTION_NAME = "ingestionJobQueue";
    private DB db;

    @PostConstruct
    public void init() {

        db = batchJobMongoTemplate.getDb();

    }

    @Override
    public void postItem(Map<String, Object> item) {

        DBCollection coll = db.getCollection(COLLECTION_NAME);
        BasicDBObject obj = new BasicDBObject(item);

        coll.save(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> reserveItem() {
        DBCollection coll = db.getCollection(COLLECTION_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put(ItemKeys.STATE, ItemValues.UNCLAIMED);

        BasicDBObject update = new BasicDBObject();

        BasicDBObject updateAction = new BasicDBObject();

        update.put(ItemKeys.STATE, ItemValues.WORKING);

        updateAction.put("$set", update);
        DBObject sortOrder = null;
        DBObject fields = null;
        DBObject result = coll.findAndModify(query, fields, sortOrder, false, updateAction, true, false);

        if (result != null) {
            return result.toMap();
        } else {

            // Nothing to reserve
            return null;
        }

    }

    @Override
    public List<Map<String, Object>> fetchItems(String workerId) {
        DBCollection coll = db.getCollection(COLLECTION_NAME);

        BasicDBObject query = new BasicDBObject();

        coll.find(query);
        return null;
    }

    @Override
    public long count() {

        DBCollection coll = db.getCollection(COLLECTION_NAME);

        return coll.count();
    }

    @Override
    public void purgeExpiredItems() {
        // mongo requires this empty override
    }

    @Override
    public int clear() {
        DBCollection coll = db.getCollection(COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();

        WriteResult res = coll.remove(query);
        return res.getN();
    }

}

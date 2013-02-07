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
package org.slc.sli.ingestion.util;

import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tke
 *
 */
public class MongoCommanderTest {

    private MongoTemplate mockedMongoTemplate = Mockito.mock(MongoTemplate.class);
    private DB db = Mockito.mock(DB.class);
    private DBCollection assessmentCollection = Mockito.mock(DBCollection.class);
    private DBCollection assessmentFamilyCollection = Mockito.mock(DBCollection.class);
    private DBCollection assessmentItem = Mockito.mock(DBCollection.class);
    private DBCollection settings = Mockito.mock(DBCollection.class);

    private final String dbName = "commanderTest";

    private Set<String> shardCollections = new TreeSet<String>();

    private Set<String> indexes = new TreeSet<String>();

    private Map<String, Integer> collectionOrder = new HashMap<String, Integer>();

    private Map<String, DBCollection> collectionIns = new HashMap<String, DBCollection>();

    private CommandResult res = Mockito.mock(CommandResult.class);


    @Before
    public void setup() {
        shardCollections.add("assessment");
        shardCollections.add("assessmentFamily");
        shardCollections.add("assessmentItem");

        collectionOrder.put("assessment", 3);
        collectionOrder.put("assessmentFamily", 1);
        collectionOrder.put("assessmentItem", 2);

        collectionIns.put("assessment", assessmentCollection);
        collectionIns.put("assessmentFamily", assessmentFamilyCollection);
        collectionIns.put("assessmentItem", assessmentItem);

        indexes.add("assessment,false,creationTime");
        indexes.add("assessmentFamily,false,creationTime");
        indexes.add("assessmentItem,false,creationTime");

        Mockito.when(mockedMongoTemplate.getDb()).thenReturn(db);
        Mockito.when(db.getName()).thenReturn(dbName);
        Mockito.when(db.getCollection("assessment")).thenReturn(assessmentCollection);
        Mockito.when(db.getCollection("assessmentFamily")).thenReturn(assessmentFamilyCollection);
        Mockito.when(db.getCollection("assessmentItem")).thenReturn(assessmentItem);
        Mockito.when(db.getSisterDB(Matchers.anyString())).thenReturn(db);
        Mockito.when(db.getCollection("settings")).thenReturn(settings);

        Mockito.when(res.ok()).thenReturn(true);
        //Mockito.when(res.getErrorMessage()).thenReturn("Mocked error message");

        Mockito.when(assessmentCollection.getFullName()).thenReturn(dbName + ".assessment");
        Mockito.when(assessmentFamilyCollection.getFullName()).thenReturn(dbName + ".assessmentFamily");
        Mockito.when(assessmentItem.getFullName()).thenReturn(dbName + ".assessmentItem");

    }

    private boolean verifyIndex(List<DBObject> dbIndexes, String target) {
        for (DBObject index : dbIndexes) {
            if (index.containsKey(target)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("boxing")
    private DBObject buildOpts(String ns, int indexOrder) {
        DBObject options = new BasicDBObject();
        options.put("name", "idx_" + indexOrder);
        options.put("unique", false);
        options.put("ns", ns);

        return options;
    }

    @Test
    public void testEnsureIndexes() {
        String result = MongoCommander.ensureIndexes("tenantDB_indexes.txt", dbName, mockedMongoTemplate);
        assertNull(result);

        for (String collection : shardCollections) {
            DBObject asskeys = new BasicDBObject();
            asskeys.put("creationTime", 1);
            DBObject options = buildOpts(dbName + "." + collection, collectionOrder.get(collection));
            Mockito.verify(collectionIns.get(collection), Mockito.times(1)).createIndex(asskeys, options);
        }
    }

    @Test
    public void testEnsureSetIndexes() {
        String result = MongoCommander.ensureIndexes(indexes, dbName, mockedMongoTemplate);
        assertNull(result);

        for (String collection : shardCollections) {
            DBObject asskeys = new BasicDBObject();
            asskeys.put("creationTime", 1);
            DBObject options = buildOpts(dbName + "." + collection, collectionOrder.get(collection));
            Mockito.verify(collectionIns.get(collection), Mockito.times(1)).createIndex(asskeys, options);
        }
    }


    @Test
    public void testPreSplit() {
        List<DBObject> shards = new ArrayList<DBObject>();
        shards.add(new BasicDBObject("_id", "shard0"));
        shards.add(new BasicDBObject("_id", "shard1"));
        BasicDBList listShards = new BasicDBList();
        listShards.add(new BasicDBObject("shards", shards));

        List<String> lShards = new ArrayList<String>();
        lShards.add("shard0");
        lShards.add("lShard1");

        Mockito.when(db.command((DBObject) Matchers.any())).thenReturn(res);
        Mockito.when(res.get("shards")).thenReturn(listShards);
        String result = MongoCommander.preSplit(shardCollections, dbName, mockedMongoTemplate);
        assertNull(result);

        Mockito.verify(db, Mockito.times(1)).command(new BasicDBObject("enableSharding", dbName));
        Mockito.verify(db, Mockito.times(2)).command(new BasicDBObject("listShards", 1));
        //Verify total number of mongo command calls
        Mockito.verify(db, Mockito.times(11)).command(Matchers.any(DBObject.class));

        //For setBalancerState
        Mockito.verify(settings, Mockito.times(1)).update(Matchers.any(DBObject.class), Matchers.any(DBObject.class), Matchers.eq(true), Matchers.eq(false));
    }

}

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
package org.slc.sli.ingestion.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Provides functionality to parse tenant DB index file, ensure indexes and pre-split
 *
 * @author tke
 *
 */
public final class MongoCommander {

    /**
     * No instance should be created.
     * All methods are static.
     */
    private MongoCommander() { }

    private static final Logger LOG = LoggerFactory.getLogger(MongoCommander.class);

    private static final String ID = "_id";

    public static void ensureIndexes(String indexFile, String db, MongoTemplate mongoTemplate) {
        Set<String> indexes = IndexFileParser.readIndexes(indexFile);

        ensureIndexes(indexes, db, mongoTemplate);
    }

    /**
     * Ensure indexes for db
     *
     * @param indexes : set of indexes
     * @param db : target database
     * @param mongoTemplate
     */
    @SuppressWarnings("boxing")
    public static void ensureIndexes(Set<String> indexes, String db, MongoTemplate mongoTemplate) {
        if (indexes != null) {
            LOG.info("Ensuring {} indexes for {} db", indexes.size(), db);
            DB dbConn = mongoTemplate.getDb();

            if (!dbConn.getName().equals(db)) {
                dbConn = dbConn.getSisterDB(db);
            }

            int indexOrder = 0; // used to name the indexes

            // each index is a comma delimited string in the format:
            // (collection, unique, indexKeys ...)
            for (String indexEntry : indexes) {
                indexOrder++;
                MongoIndex index = IndexFileParser.parseIndex(indexEntry);

                DBObject options = new BasicDBObject();
                options.put("name", "idx_" + indexOrder);
                options.put("unique", index.isUnique());
                options.put("ns", dbConn.getCollection(index.getCollection()).getFullName());

                try {
                    dbConn.getCollection(index.getCollection()).createIndex(index.getKeys(), options);
                } catch (MongoException e) {
                    LOG.error("Failed to ensure index:{}", e.getMessage());
                }
            }
        } else {
            throw new IllegalStateException("Indexes configuration not found.");
        }
    }

    /**
     * get list of  the shards
     * @param dbConn
     * @return
     */
    private static List<String> getShards(DB dbConn) {
        List<String> shards = new ArrayList<String>();

        DBObject listShardsCmd = new BasicDBObject("listShards", 1);
        CommandResult res = dbConn.command(listShardsCmd);

        BasicDBList listShards = (BasicDBList) res.get("shards");

        //Only get shards for sharding mongo
        if (listShards != null) {
            ListIterator<Object> iter = listShards.listIterator();

            while (iter.hasNext()) {
                BasicDBObject shard = (BasicDBObject) iter.next();
                shards.add(shard.getString(ID));
            }
        }
        return shards;
    }

    private static DBObject buildSplitCommand(String collection, String splitString) {
        DBObject splitCmd = new BasicDBObject();
        splitCmd.put("split", collection);
        splitCmd.put("middle", new BasicDBObject(ID, splitString));

        return splitCmd;
    }

    /**
     * Move chunks of a collections to different shards
     * @param collection : the collection to be split
     * @param shards : list of all shards
     * @param dbConn
     */
    private static void moveChunks(String collection, List<String> shards, DB dbConn) {
        int numShards = shards.size();

        if (numShards == 0) {
            return;
        }

        int charOffset = 256 / numShards;

        List<String> moveStrings = new ArrayList<String>();
        moveStrings.add("00");

        //caculate splits and add to the moves array
        for (int shard = 1; shard <= numShards; shard++) {
            String splitString;
            if (shard == numShards) {
                splitString = " ";
            } else {
                splitString = Integer.toHexString(charOffset * shard);
            }
            moveStrings.add(splitString);
            dbConn.command(buildSplitCommand(collection, splitString));
        }

        //explictly move chunks to each shard
        for (int index = 0; index < numShards; index++) {
            DBObject moveCommand = new BasicDBObject();
            moveCommand.put("moveChunk", collection);
            moveCommand.put("find", new BasicDBObject(ID, moveStrings.get(index)));
            moveCommand.put("to", shards.get(index));

            dbConn.command(moveCommand);
        }
    }

    /**
     * set the state of balancer.
     *
     * @param dbConn
     * @param state
     */
    private static void setBalancerState(DB dbConn, boolean state) {
        DBObject balancer = new BasicDBObject(ID, "balancer");
        DBObject updateObj = new BasicDBObject();
        String stopped = state ? "false" : "true";
        updateObj.put("$set", new BasicDBObject("stopped", stopped));
        dbConn.getSisterDB("config").getCollection("settings").update(balancer, updateObj, true, false);
    }

    /**
     * Pre-split database
     * @param shardCollections: the set of collections to be split
     * @param dbName
     * @param mongoTemplate
     */
    public static void preSplit(Set<String> shardCollections, String dbName, MongoTemplate mongoTemplate) {
        DB dbConn = mongoTemplate.getDb().getSisterDB("admin");

        DBObject enableShard = new BasicDBObject("enableSharding", dbName);
        dbConn.command(enableShard);

        List<String> shards = getShards(dbConn);

        //Don't do anything if it is non-sharded
        if (shards.size() == 0) {
            return;
        }

        for (String coll : shardCollections) {
            String collection = dbName + "." + coll;

            DBObject shardColl = new BasicDBObject();
            shardColl.put("shardCollection", collection);
            shardColl.put("key", new BasicDBObject(ID, 1));
            dbConn.command(shardColl);

            moveChunks(collection, shards, dbConn);

            //explicitly add endpoint at beginning of range
            String startSplitString = " ";
            dbConn.command(buildSplitCommand(collection, startSplitString));

            //explicitly add an end split at 'year + 1 + "a" '
            //since 'year + "z" ' potentially cuts off some records
            String endSplitString = "||";
            dbConn.command(buildSplitCommand(collection, endSplitString));
        }

        //set balancer off
        setBalancerState(dbConn, false);
    }

}

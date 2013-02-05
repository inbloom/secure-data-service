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
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.ingestion.model.RecordHash;

/**
 * Provides functionality to parse tenant DB index file, ensure indexes and pre-split
 *
 * @author tke
 *
 */
public final class MongoCommander {

    private static IndexParser<String> indexTxtFileParser = new IndexTxtFileParser();
    private static IndexParser<Set<String>> indexSliFormatParser = new IndexSliFormatParser();
    public static final String STR_0X38 = "00000000000000000000000000000000000000";

    private MongoCommander() {
        /*
         * No instance should be created.
         * All methods are static.
         */
    }

    private static final Logger LOG = LoggerFactory.getLogger(MongoCommander.class);

    private static final String ID = "_id";

    public static String ensureIndexes(String indexFile, String db, MongoTemplate mongoTemplate) {
        Set<MongoIndex> indexes = indexTxtFileParser.parse(indexFile);
        DB dbConn = getDB(db, mongoTemplate);
        return ensureIndexes(indexes, dbConn);
    }

    /**
     * Ensure indexes for db
     *
     * @param indexes : set of indexes
     * @param db : target database
     * @param mongoTemplate
     * @return Error description, or null if no errors
     */
    public static String ensureIndexes(Set<String> indexes, String db, MongoTemplate mongoTemplate) {
        if (indexes != null) {
            DB dbConn = getDB(db, mongoTemplate);

            Set<MongoIndex> indexSet = indexSliFormatParser.parse(indexes);
            return ensureIndexes(indexSet, dbConn);
        } else {
            throw new IllegalStateException("Indexes configuration not found.");
        }
    }

    public static String ensureIndexes(Set<MongoIndex> indexes, DB dbConn) {
        if (indexes != null) {
            LOG.info("Ensuring {} indexes for {} db", indexes.size(), dbConn);

            int indexOrder = 0; // used to name the indexes

            for (MongoIndex indexEntry : indexes) {
                indexOrder++;
                String result = ensureIndexes(indexEntry, dbConn, indexOrder);
                if (result != null) {
					return result;
				}
            }
            return null;
        } else {
            throw new IllegalStateException("Indexes configuration not found.");
        }
    }

    @SuppressWarnings("boxing")
    public static String ensureIndexes(MongoIndex index, DB dbConn, int indexOrder) {
        DBObject options = new BasicDBObject();
        options.put("name", "idx_" + indexOrder);
        options.put("unique", index.isUnique());
        options.put("ns", dbConn.getCollection(index.getCollection()).getFullName());

        try {
            dbConn.getCollection(index.getCollection()).createIndex(new BasicDBObject(index.getKeys()), options);
            return null;
        } catch (MongoException e) {
            LOG.error("Failed to ensure index:{}", e.getMessage());
            return "Failed to ensure index:" + e.getMessage();
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
        if (!res.ok()) {
            LOG.error("Error getting shards for {}: {}", dbConn, res.getErrorMessage());
        }

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
     * Move chunks of a collection to different shards
     * @param collection : the collection to be split
     * @param shards : list of all shards
     * @param dbConn
     * @return Error description, or null if no errors
     */
    private static String moveChunks(String collection, List<String> shards, DB dbConn) {
        int numShards = shards.size();

        if (numShards == 0) {
            return null;
        }

        //
        // Fraction of 256 that each block will occupy
        //
        int charOffset = 256 / numShards;

        //
        // Keep list of the start point for each chunk, so we can move the chunk after creating them
        //
        List<String> moveStrings = new ArrayList<String>();
        moveStrings.add("$minkey");

        //
        // We want one chunk per shard, so we need to do numShards-1 splits
        // Calculate where to split and do it
        //
        for (int shard = 1; shard <= numShards-1; shard++) {
        	//
        	// Split points based on the fraction of 256 that each chunk should occupy
        	//
            String splitString = Integer.toHexString(charOffset * shard);
            moveStrings.add(splitString);
            CommandResult result = dbConn.command(buildSplitCommand(collection, splitString));
            if (!result.ok()) {
                LOG.error("Error splitting chunk in {}: {}", collection, result.getErrorMessage());
                return result.getErrorMessage();
            }
        }

        //
        // Move each chunk to the correct shard
        // One of these moves will get a "that chunk is already on that shard" error
        // which is ok
        //
        for (int index = 0; index < numShards; index++) {
            DBObject moveCommand = new BasicDBObject();
            moveCommand.put("moveChunk", collection);
            moveCommand.put("find", new BasicDBObject(ID, moveStrings.get(index)));
            moveCommand.put("to", shards.get(index));

            CommandResult result = dbConn.command(moveCommand);
            if (!result.ok()) {
            	if (!result.getErrorMessage().equals("that chunk is already on that shard")) {
            		LOG.error("Error moving chunk in {}: {}", collection, result.getErrorMessage());
            		return result.getErrorMessage();
            	}
            }
        }
        return null;
    }

    /**
     * set the state of balancer.
     *
     * @param dbConn
     * @param state
     * @return Error description, or null if no errors
     */
    private static String setBalancerState(DB dbConn, boolean state) {
        DBObject balancer = new BasicDBObject(ID, "balancer");
        DBObject updateObj = new BasicDBObject();
        String stopped = state ? "false" : "true";
        updateObj.put("$set", new BasicDBObject("stopped", stopped));
        WriteResult wresult = dbConn.getSisterDB("config").getCollection("settings").update(balancer, updateObj, true, false);
        if (wresult != null) {
            CommandResult result = wresult.getLastError();
            if (!result.ok()) {
                LOG.error("Error setting balancer state to {}: {}", state, result.getErrorMessage());
                return result.getErrorMessage();
            }
        }
        return null;
    }

    /**
     * Pre-split database
     * @param shardCollections: the set of collections to be split
     * @param dbName
     * @param mongoTemplate
     * @return Error description, or null if no errors
     */
    public static String preSplit(Set<String> shardCollections, String dbName, MongoTemplate mongoTemplate) {
        DB dbConn = mongoTemplate.getDb().getSisterDB("admin");

        //Don't do anything if it is non-sharded
        List<String> shards = getShards(dbConn);
        if (shards.size() == 0) {
            return null;
        }

        //set balancer off
        String sresult = setBalancerState(dbConn, false);
        if (sresult != null) {
        	return sresult;
        }

        // Enable sharding for this database
        DBObject enableShard = new BasicDBObject("enableSharding", dbName);
        CommandResult result = dbConn.command(enableShard);
        if (!result.ok()) {
            LOG.error("Error enabling sharding on {}: {}", dbConn, result.getErrorMessage());
            return result.getErrorMessage();
        }

        for (String coll : shardCollections) {
            String collection = dbName + "." + coll;

            // Enable sharding for this collection, sharding on _id
            DBObject shardColl = new BasicDBObject();
            shardColl.put("shardCollection", collection);
            shardColl.put("key", new BasicDBObject(ID, 1));
            result = dbConn.command(shardColl);
            if (!result.ok()) {
                LOG.error("Error enabling shard'ing on {}: {}", collection, result.getErrorMessage());
               return result.getErrorMessage();
            }

            sresult = moveChunks(collection, shards, dbConn);
            if (sresult != null) {
				return sresult;
			}
        }
        return preSplitRecordHash(dbName, mongoTemplate);
    }

    private static String preSplitRecordHash(String dbName,  MongoTemplate mongoTemplate) {
        DB dbConn = mongoTemplate.getDb().getSisterDB("admin");
        List<String> shards = getShards(dbConn);
        if (shards != null && shards.size() > 0) {
            int numShards = shards.size();
            LOG.info("Shard count = {}. Setting up sharding config for recordHash!", numShards);
            String collection = dbName + "." + "recordHash";
            DBObject shardColl = new BasicDBObject();
            shardColl.put("shardCollection", collection);
            shardColl.put("key", new BasicDBObject(ID, 1));
            CommandResult result = dbConn.command(shardColl);
            if (!result.ok()) {
                LOG.error("Error enabling shard'ing on recordHash: {}", result.getErrorMessage());
               return result.getErrorMessage();
            }

            int charOffset = 256 / numShards;
            List<byte[]> shardPoints = new ArrayList<byte[]>();
            shardPoints.add(null);
            for (int shard = 1; shard < numShards; shard++) {
                String splitString = Integer.toHexString(charOffset * shard);
                if (splitString.length() < 2) {
                    splitString = "0" + splitString;
                }

                splitString = splitString + STR_0X38;
                byte[] splitPoint = RecordHash.hex2Binary(splitString);
                shardPoints.add(splitPoint);
                LOG.info("Adding recordHash splitPoint [" + RecordHash.binary2Hex(splitPoint) + "]");

                DBObject splitCmd = new BasicDBObject();
                splitCmd.put("split", collection);
                splitCmd.put("middle", new BasicDBObject(ID, splitPoint));
                result = dbConn.command(splitCmd);
                if (!result.ok()) {
                    LOG.error("Error splitting chunk in recordHash: {}", result.getErrorMessage());
                    return result.getErrorMessage();
                }
            }

            for (int index = 0; index < numShards; index++) {
                DBObject moveCommand = new BasicDBObject();
                moveCommand.put("moveChunk", collection);
                moveCommand.put("find", new BasicDBObject(ID, index == 0 ? "$minkey" : shardPoints.get(index)));
                moveCommand.put("to", shards.get(index));
                result = dbConn.command(moveCommand);
                if (!result.ok()) {
                	if (!result.getErrorMessage().equals("that chunk is already on that shard")) {
                		LOG.error("Error moving chunk in recordHash: {}", result.getErrorMessage());
                		return result.getErrorMessage();
                	}
                }
            }
        } else {
            LOG.info("No shards or shard count < 0. Not setting sharding config for recordHash!");
        }
        return null;
    }


    /**
     * @param db
     * @param mongoTemplate
     * @return
     */
    public static DB getDB(String db, MongoTemplate mongoTemplate) {
        DB dbConn = mongoTemplate.getDb();
        return getDB(db, dbConn);
    }

    public static DB getDB(String db, DB dbConn) {
        return dbConn.getName().equals(db) ? dbConn : dbConn.getSisterDB(db);
    }

}

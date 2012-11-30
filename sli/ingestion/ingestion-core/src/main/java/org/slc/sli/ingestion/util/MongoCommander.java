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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author tke
 *
 */
public final class MongoCommander {

    private MongoCommander() { }

    protected static final Logger LOG = LoggerFactory.getLogger(MongoCommander.class);

    /**
     * Executes mongo command
     *
     * @param db
     *            the database name in mongo
     * @param script
     *            the name of the java script to be executed
     * @param jsContent
     *            javascript content to be evaluated before script execution
     * @return
     */
    public static void exec(String db, String script, String jsContent) {
        try {
            URL scriptFile = Thread.currentThread().getContextClassLoader().getResource(script);
            if (scriptFile != null) {
                String path = (new File(scriptFile.getFile())).getPath();
                String[] args = { "mongo", db, "--eval", jsContent, path };

                LOG.info("Running process with args: {} {} {} {} {}", args);
                ProcessBuilder pb = new ProcessBuilder(args);
                Process pr = pb.start();
                try {
                    pr.waitFor();
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage());
                }
                if (pr.exitValue() != 0) {
                    LOG.error("Failed to execute the script " + script + " during tenant onboarding");
                    throw new RuntimeException("Failed to execute the script " + script + " during tenant onboarding");
                }
            } else {
                LOG.error("Failed to locate the script " + script + " during tenant onboarding");
                throw new RuntimeException("Failed to locate the script " + script + " during tenant onboarding");
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static boolean validIndex (String line) {
        if (line.startsWith("#")) {
            return false;
        }
        String[] indexTokens = line.split(",");
        if (indexTokens.length < 3) {
            return false;
        }

        return true;
    }

    public static void ensureIndexes(String indexFile, String db, MongoTemplate mongoTemplate) {
        InputStream indexesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(indexFile);

        DataInputStream in = new DataInputStream(indexesStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        Set<String> indexes = new TreeSet<String>();

        String currentLine;

        //Reading in all the indexes
        try {
            while((currentLine = br.readLine()) != null) {
                //skipping lines starting with #
                if(validIndex(currentLine)) {
                    indexes.add(currentLine);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to create index from {}", indexFile);
        }

        ensureIndexes(indexes, db, mongoTemplate);
    }

    /**
     * Ensure indexes for db
     *
     * @param indexes : set of indexes
     * @param db : target database
     * @param mongoTemplate
     */
    public static void ensureIndexes(Set<String> indexes, String db, MongoTemplate mongoTemplate) {
        if (indexes != null) {
            LOG.info("Ensuring {} indexes for {} db", indexes.size(), db);
            DB dbConn = mongoTemplate.getDb();

            if(!dbConn.getName().equals(db)) {
                dbConn = dbConn.getSisterDB(db);
            }

            int indexOrder = 0; // used to name the indexes

            // each index is a comma delimited string in the format:
            // (collection, unique, indexKeys ...)
            for (String indexEntry : indexes) {
                indexOrder++;
                String[] indexTokens = indexEntry.split(",");

                if (indexTokens.length < 3) {
                    throw new IllegalStateException("Expected at least 3 tokens for index config definition: "
                            + indexTokens);
                }

                String collection = indexTokens[0];
                boolean unique = Boolean.parseBoolean(indexTokens[1]);
                DBObject keys = new BasicDBObject();

                for (int i = 2; i < indexTokens.length; i++) {
                    String [] index = indexTokens[i].split(":");

                    //default order of the index
                    int order = 1;

                    //If the key specifies order
                    if (index.length == 2) {
                        //remove all the non visible characters from order string
                        order = Integer.parseInt(index[1].replaceAll("\\s", ""));
                    } else if(index.length != 1) {
                        throw new IllegalStateException("Unexpected index order: "
                                + indexTokens[i]);
                    }

                    keys.put(index[0], order);
                }

                DBObject options = new BasicDBObject();
                options.put("name", "idx_" + indexOrder);
                options.put("unique", unique);
                options.put("ns", dbConn.getCollection(collection).getFullName());

                try{
                    dbConn.getCollection(collection).createIndex(keys, options);
                } catch(Exception e) {
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

        BasicDBList listShards = (BasicDBList)res.get("shards");

        //Only get shards for sharding mongo
        if(listShards != null) {
            ListIterator<Object> iter = listShards.listIterator();

            while(iter.hasNext()) {
                BasicDBObject shard = (BasicDBObject) iter.next();
                shards.add(shard.getString("_id"));
            }
        }
        return shards;
    }

    private static DBObject buildSplitCommand(String collection, String splitString) {
        DBObject splitCmd = new BasicDBObject();
        splitCmd.put("split", collection);
        splitCmd.put("middle", new BasicDBObject("_id", splitString));

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

        if(numShards == 0) {
            return;
        }

        int charOffset = (int)Math.floor(256 / numShards);

        List<String> moveStrings = new ArrayList<String>();
        moveStrings.add("00");

        CommandResult a;
        //caculate splits and add to the moves array
        for(int shard = 1; shard <= numShards; shard++) {
            String splitString;
            if(shard == numShards) {
                splitString = " ";
            } else {
                splitString = Integer.toHexString(charOffset * shard).toString();
            }
            moveStrings.add(splitString);

            a = dbConn.command(buildSplitCommand( collection, splitString));
        }

        //explictly move chunks to each shard
        for(int index = 0 ; index < numShards; index++) {
            DBObject moveCommand = new BasicDBObject();
            moveCommand.put("moveChunk", collection);
            moveCommand.put("find", new BasicDBObject("_id", moveStrings.get(index)));
            moveCommand.put("to", shards.get(index));

            a = dbConn.command(moveCommand);
        }
    }

    /**
     * set the state of balancer.
     *
     * @param dbConn
     * @param state
     */
    private static void setBalancerState(DB dbConn, boolean state) {
        DBObject balancer = new BasicDBObject("_id", "balancer");
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
        CommandResult res = dbConn.command(enableShard);

        List<String> shards = getShards(dbConn);

        //Don't do anything if it is non-sharded
        if (shards.size() == 0) {
            return ;
        }

        for(String coll : shardCollections) {
            String collection = dbName + "." + coll;

            DBObject shardColl = new BasicDBObject();
            shardColl.put("shardCollection", collection);
            shardColl.put("key", new BasicDBObject("_id", 1));
            dbConn.command(shardColl);

            moveChunks(collection, shards, dbConn);

            //explicitly add endpoint at beginning of range
            String startSplitString = " ";
            dbConn.command(buildSplitCommand( collection, startSplitString));

            //explicitly add an end split at 'year + 1 + "a" '
            //since 'year + "z" ' potentially cuts off some records
            String endSplitString = "||";
            dbConn.command(buildSplitCommand(collection, endSplitString));
        }

        //set balancer off
        setBalancerState(dbConn, false);
    }

}

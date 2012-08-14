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

package org.slc.sli.aggregation.mapreduce.map;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;

/**
 * ConfigurableChainedMapper
 * 
 * A chained mapper implementation that allows for the creation of a map chain via configuration
 * rather than hard-coding.
 * 
 * A chained mapper requires, at a minimum, the following configuration values:
 * 
 * { "map_chain" : [
 * {
 * "description" : "Describe this mapper.",
 * "map_class" : "org.slc.sli.aggregation.mapreduce..map.IDMapper",
 * "input_collection": "sli.assessment",
 * "input_key_field" : "_id",
 * "input_key_type" : "org.apache.hadoop.io.Text",
 * "input_value_type" : "org.bson.BSONObject",
 * "read_from_secondaries" : true,
 * "output_key_type" : "org.slc.sli.aggregation.mapreduce.TenantAndIdEmittableKey",
 * "output_value_type" : "org.apache.hadoop.io.LongWritable",
 * "by_value" : false,
 * "query" : { "body.assessmentIdentificationCode.ID" : "Grade 7 2011 State Math" }
 * "fields" : { "_id" : 1 },
 * "hadoop_options' : [ {"name":"value"}, ... ]
 * }, {
 * mapper 2 configuration }, ... ] }"
 * 
 */
public class ConfigurableMapperBuilder {
    
    private ObjectMapper om = new ObjectMapper();
    private final static Logger log = Logger.getLogger("ConfigurableChainedMapper");
    private final DB db;
    
    /**
     * mapper_entry available keys when defining a chain mapper.
     */
    enum MapperEntry {
        DESCRIPTION, MAP_CLASS, INPUT_KEY_FIELD, READ_FROM_SECONDARIES, OUTPUT_KEY_TYPE, OUTPUT_VALUE_TYPE, FIELDS, HADOOP_OPTIONS;
        
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
        
        public static MapperEntry parseValue(String s) {
            return MapperEntry.valueOf(s.toUpperCase());
        }
    }
    
    public ConfigurableMapperBuilder(DB db) {
        this.db = db;
    }
    
    public static final String CHAIN_CONF = "mapDefn";
    
    public Job makeJob(Configuration conf, Map<String, Object> configInput) {
        
        try {
            setMongoOptions(conf, configInput);
            return parseMapper(conf, configInput);
        } catch (IOException e) {
            log.severe("Invalid map/reduce configuration detected : parsing failed : " + e.toString());
            throw new IllegalArgumentException(
                    "Invalid map/reduce configuration detected : parsing failed. Check log for details.");
        } catch (ClassNotFoundException e) {
            log.severe("Invalid map/reduce configuration detected : parsing failed : " + e.toString());
            throw new IllegalArgumentException(
                    "Invalid map/reduce configuration detected : parsing failed. Check log for details.");
        }
    }
    
    private void setMongoOptions(Configuration job, Map<String, Object> config) {
        MongoConfigUtil.setQuery(job, buildQuery(config));
        MongoConfigUtil.setInputKey(job, (String) config.get("input_key_field"));
        MongoConfigUtil.setInputURI(job, "mongodb://" + (String) config.get("input_collection"));
        MongoConfigUtil.setReadSplitsFromSecondary(job, (Boolean) config.get("read_from_secondaries"));
        
        /**
         * Configure how hadoop calculates splits.
         * 
         * We enable input splits to avoid having the entire job executed on a single hadoop node.
         * 
         * We enable shard chunk splitting to allow mongo to specify how to split the input.
         * 
         * We disable read splits from shards because we want hadoop connecting to mongos, not
         * mongod directly. This avoids incorrect results in situations where data is in the process
         * of migration at the same time hadoop is trying to read it.
         * 
         * TODO - determine if we also need to set the input split key pattern. This depends
         * on how well data is distributed by _id. Setting the key pattern gives finer grained
         * control over how splits are calculated.
         */
        MongoConfigUtil.setCreateInputSplits(job, true);
        MongoConfigUtil.setShardChunkSplittingEnabled(job, false);
        MongoConfigUtil.setReadSplitsFromShards(job, false);
    }
    
    private String buildQuery(Map<String, Object> config) {
        String query = (String) config.get("query");
        if (query != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> lookUp = (Map<String, Object>) config.get("lookupParams");
            if (lookUp != null) {
                String sub = doLookup(lookUp);
                query = query.replaceAll("@" + lookUp.get("as") + "@", sub);
            }
        }
        return query;
    }
    
    private String doLookup(Map<String, Object> lookUp) {
        String collectionName = (String) lookUp.get("collection");
        DBCollection coll = db.getCollection(collectionName);
        String key = (String) lookUp.get("get");
        BasicDBObject dbQuery = new BasicDBObject((Map<?, ?>) lookUp.get("query"));
        BasicDBObject keys = new BasicDBObject(key, "1");
        if ((Boolean) lookUp.get("multi")) {
            DBCursor results = coll.find(dbQuery, keys);
            StringBuilder result = new StringBuilder("[");
            while(results.hasNext()){
                result.append(results.next().get(key));
            }
            result.append("]");
            return result.toString();
        } else {
            return coll.findOne(dbQuery, keys).get(key).toString();
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Job parseMapper(Configuration conf, Map<String, Object> mapperDef) throws IOException,
            ClassNotFoundException {
        
        String fields = (String) mapperDef.get(MapperEntry.FIELDS.toString());
        Class mapper = Class.forName((String) mapperDef.get(MapperEntry.MAP_CLASS.toString()));
        Class outputKey = mapperDef.containsKey(MapperEntry.OUTPUT_KEY_TYPE.toString()) ? Class
                .forName((String) mapperDef.get(MapperEntry.OUTPUT_KEY_TYPE.toString())) : Text.class;
        Class outputValue = mapperDef.containsKey(MapperEntry.OUTPUT_VALUE_TYPE.toString()) ? Class
                .forName((String) mapperDef.get(MapperEntry.OUTPUT_VALUE_TYPE.toString())) : Text.class;
        String hadoopOptions = (String) mapperDef.get(MapperEntry.HADOOP_OPTIONS.toString());
        String keyField = (String) mapperDef.get(MapperEntry.INPUT_KEY_FIELD.toString());
        
        // validate we have enough to continue
        if (mapper == null) {
            throw new IllegalArgumentException("Invalid map/reduce configuration detected : no mapper class specified.");
        }
        if (keyField == null) {
            throw new IllegalArgumentException("Invalid map/reduce configuration detected : no key field specified.");
        }
        if (fields == null) {
            throw new IllegalArgumentException(
                    "Invalid map/reduce configuration detected : no mongo input fields specified.");
        }
        
        /**
         * Any additional hadoop options are added to the configuration as key/value pairs.
         */
        if (hadoopOptions != null) {
            List<Map<String, String>> options = (List<Map<String, String>>) om.readValue(hadoopOptions,
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> option : options) {
                for (Map.Entry<String, String> keyValue : option.entrySet()) {
                    conf.set(keyValue.getKey(), keyValue.getValue());
                }
            }
        }
        
        Job job = new Job(conf);
        job.setMapperClass(mapper);
        job.setInputFormatClass(MongoInputFormat.class);
        job.setMapOutputKeyClass(outputKey);
        job.setMapOutputValueClass(outputValue);
        job.setOutputFormatClass(MongoAggFormatter.class);
        return job;
    }
}

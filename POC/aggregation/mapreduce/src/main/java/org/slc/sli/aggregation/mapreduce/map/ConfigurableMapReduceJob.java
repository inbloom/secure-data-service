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

package org.slc.sli.aggregation.mapreduce.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.codehaus.jackson.map.ObjectMapper;
import org.hsqldb.lib.StringInputStream;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ConfigSections;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.MapConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ReduceConfig;
import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.io.MongoTenantAndIdInputFormat;
import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * ConfigurableMapReduceJob
 *
 * A configurable mapper implementation that allows for the creation of a map via configuration
 * rather than hard-coding.
 *
 * @see @link https://thesli.onconfluence.com/display/sli/US3042+%28Spike%29+Configurable+Aggregates
 *    for format.
 */
public class ConfigurableMapReduceJob extends Mapper<EmittableKey, BSONWritable, EmittableKey, BSONWritable> {

    static Logger log = Logger.getLogger("ConfigurableMapReduceJob");

    static ObjectMapper om = new ObjectMapper();
    static final String MONGO_HOST = "localhost:27017";

    private static String mapCollection;
    private static Map<String, Object> mapFields;
    private static JobConfiguration.mapper mapper;
    private static Map<String, Object> mapQuery;
    private static String reduceCollection;
    private static String reduceField;
    private static JobConfiguration.function reduceFunction;

    public static JobConf parse(Configuration conf, final String json) throws IOException {
        ConfigSections s = JobConfiguration.readStream(new StringInputStream(json));
        return parse(conf, s);
    }

    public static JobConf parse(Configuration conf, final InputStream stream) throws IOException {
        ConfigSections s = JobConfiguration.readStream(stream);
        return parse(conf, s);
    }

    public static JobConf parse(Configuration conf, ConfigSections s) throws IOException {
        JobConf rval = new JobConf(conf);
        reduceFunction = s.getMetadata().getFunction();

        parseMapper(s);
        parseReducer(s);
        finalizeConfig(rval, s);

        return rval;
    }

    protected static void parseMapper(ConfigSections s) {
        MapConfig mapConf = s.getMapper();
        if (mapConf != null) {
            mapCollection = mapConf.getCollection();
            mapFields = mapConf.getFields();
            mapper = JobConfiguration.mapper.valueOf(mapConf.getMapper());
            mapQuery = mapConf.getQuery();
        }
    }

    public static void parseReducer(ConfigSections s) {
        ReduceConfig reduceConfig = s.getReduce();
        if (reduceConfig != null) {
            reduceCollection = reduceConfig.getCollection();
            reduceField = reduceConfig.getField();
        }
    }



    @SuppressWarnings("rawtypes")
    protected static JobConf finalizeConfig(JobConf jobConf, ConfigSections s) throws IOException {

        Class<? extends Mapper> mapperClass = JobConfiguration.mapper.getMapClass(mapper);
        Class<? extends Reducer> reducerClass = JobConfiguration.function.getReduceClass(reduceFunction);
        Map<String, String> idFields = s.getMapper().getMapIdFields();

        // validate we have enough to continue
        boolean valid = true;
        if (mapperClass == null) {
            log.severe("Invalid map/reduce configuration detected : no mapper class specified.");
            valid = false;
        }
        if (idFields == null) {
            idFields = new HashMap<String, String>();
            log.severe("Invalid map/reduce configuration detected : no map id fields specified.");
            valid = false;
        }
        if (mapCollection == null) {
            log.severe("Invalid map/reduce configuration detected : no map collection specified.");
            valid = false;
        }
        if (mapQuery == null) {
            log.severe("Invalid map/reduce configuration detected : no map query specified.");
            valid = false;
        }
        if (mapFields == null) {
            log.severe("Invalid map/reduce configuration detected : no map input fields specified.");
            valid = false;
        }
        if (reducerClass == null) {
            log.severe("Invalid map/reduce configuration detected : no reducer class specified.");
            valid = false;
        }
        if (reduceCollection == null) {
            log.severe("Invalid map/reduce configuration detected : no reduce collection specified.");
            valid = false;
        }
        if (reduceField == null) {
            log.severe("Invalid map/reduce configuration detected : no reduce field specified.");
            valid = false;
        }

        if (!valid) {
            throw new IllegalArgumentException("Invalid mapper specified. Check log for details.");
        }

        jobConf.set("mapred.output.dir",
                String.format("%s-%s-%d", s.getMapper().getMapper(), s.getMetadata().getFunction(), System.currentTimeMillis()));

        jobConf.setJobName(s.getMetadata().getDescription() == null ? "M/R Job" : s.getMetadata().getDescription());

        // enable speculative execution. Multiple mapper tasks are created for the same split.
        // First one to finish wins; the remaining tasks are terminated.
        jobConf.setSpeculativeExecution(true);
        jobConf.setUseNewMapper(true);
        jobConf.setUseNewReducer(true);

        /**
         * TODO -- decide if this is required.
        String id = conf.get("@ID@");
        String tenantId = conf.get("@TENANT_ID@");
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String s = (String) value;
                if (s.indexOf("@ID@") >= 0 && id != null) {
                    s = s.replace("@ID@", id);
                    query.put(entry.getKey(), s);
                }
                if (s.indexOf("@TENANT_ID@") >= 0 && tenantId != null) {
                    s = s.replace("@TENANT_ID@", tenantId);
                    query.put(entry.getKey(), s);
                }
            }
        }

        if (updateField.indexOf("@ID@") >= 0 && id != null) {
            updateField = updateField.replace("@ID@", id);
        }
        if (updateField.indexOf("@TENANT_ID@") >= 0 && tenantId != null) {
            updateField = updateField.replace("@TENANT_ID@", tenantId);
        }
        */

        MongoConfigUtil.setQuery(jobConf, new BasicDBObject(mapQuery));

        Map<String, Object> fullFields = new HashMap<String, Object>();
        for (String f : idFields.values()) {
            fullFields.put(f, 1);
        }
        fullFields.putAll(mapFields);

        MongoConfigUtil.setFields(jobConf, new BasicDBObject(fullFields));
        MongoConfigUtil.setInputKey(jobConf, idFields.get("id"));
        MongoConfigUtil.setInputURI(jobConf, "mongodb://" + MONGO_HOST + "/" + mapCollection);
        MongoConfigUtil.setMapperOutputKey(jobConf, TenantAndIdEmittableKey.class);
        MongoConfigUtil.setMapperOutputValue(jobConf, BSONWritable.class);
        MongoConfigUtil.setOutputKey(jobConf, TenantAndIdEmittableKey.class);
        MongoConfigUtil.setOutputValue(jobConf, BSONWritable.class);

        // TODO - this probably should be configurable
        MongoConfigUtil.setReadSplitsFromSecondary(jobConf, true);

        MongoConfigUtil.setSplitSize(jobConf, 32);

        jobConf.setClass("mapred.input.key.class", TenantAndIdEmittableKey.class, EmittableKey.class);
        jobConf.setClass("mapred.input.value.class", BSONWritable.class, Object.class);

        jobConf.setClass("mapred.output.key.class", TenantAndIdEmittableKey.class, EmittableKey.class);
        jobConf.setClass("mapred.output.value.class", BSONWritable.class, Object.class);

        jobConf.setClass("mapreduce.inputformat.class", MongoTenantAndIdInputFormat.class, MongoInputFormat.class);
        jobConf.setClass("mapreduce.outputformat.class", MongoAggFormatter.class, MongoOutputFormat.class);
        MongoConfigUtil.setInputFormat(jobConf,  MongoTenantAndIdInputFormat.class);
        MongoConfigUtil.setOutputFormat(jobConf, MongoAggFormatter.class);

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
        MongoConfigUtil.setCreateInputSplits(jobConf, true);
        MongoConfigUtil.setShardChunkSplittingEnabled(jobConf, true);
        MongoConfigUtil.setReadSplitsFromShards(jobConf, false);

        MongoConfigUtil.setOutputURI(jobConf, "mongodb://" + MONGO_HOST + "/" + reduceCollection);

        jobConf.setJarByClass(JobConfiguration.class);

        MongoConfigUtil.setMapper(jobConf,  mapperClass);
        jobConf.setClass(JobContext.MAP_CLASS_ATTR, mapperClass, Mapper.class);

        MongoConfigUtil.setReducer(jobConf, reducerClass);
        jobConf.setClass(JobContext.REDUCE_CLASS_ATTR, reducerClass, Reducer.class);

        // Set this relatively high to keep the total map execution time low.
        // Formula:  1.75 * (# nodes * max tasks)
        // TODO : replace this hardcoded value with one calculated from configuration information.
        jobConf.setNumReduceTasks(52);

        // Add the configuration itself to the JobConf.
        JobConfiguration.toHadoopConfiguration(s, jobConf);

        return jobConf;
    }


    /**
     * UpdatePlaceholders - Update all fields, replacing any placeholder values with configuration
     * properties.
     *
     * @param conf - configuration containing placeholder values.
     * @param metadata - job metadata information.
     * @param hadoop - job hadoop configuration.
     */
    protected static String updatePlaceholders(Configuration conf, final String resourcePath) {

        InputStream s = ConfigSections.class.getClassLoader().getResourceAsStream(resourcePath);
        String confString = new Scanner(s, "UTF-8").useDelimiter("\\A").next();

        String id = conf.get("@ID@");
        String tenantId = conf.get("@TENANT_ID@");

        if (id == null && tenantId == null) {
            return confString;
        }


        if (confString.indexOf("@ID@") >= 0 && id != null) {
            confString = confString.replaceAll("@ID@", id);
        }
        if (confString.indexOf("@TENANT_ID@") >= 0 && tenantId != null) {
            confString = confString.replaceAll("@TENANT_ID@", tenantId);
        }

        return confString;
    }
}

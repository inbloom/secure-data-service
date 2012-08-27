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
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.codehaus.jackson.map.ObjectMapper;
import org.hsqldb.lib.StringInputStream;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.BandsConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ConfigSections;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.HadoopConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.MetadataConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.RangeConfig;
import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

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

    public static JobConf parseMapper(Configuration conf, final String resourcePath) throws IOException {

        String configString = updatePlaceholders(conf, resourcePath);

        ConfigSections s = JobConfiguration.readStream(new StringInputStream(configString));
        JobConf rval = new JobConf(conf);
        if (s.getCalculatedValue() != null) {
            rval = ConfigurableMapReduceJob.parseCalculatedValueConfig(rval, s.getCalculatedValue());
        }
        if (s.getAggregation() != null) {
            rval = ConfigurableMapReduceJob.parseAggregationConfig(rval, s.getAggregation());
        }
        return rval;
    }

    public static JobConf parseCalculatedValueConfig(JobConf conf, JobConfiguration.CalculatedValueConfig mapperDef) throws IOException {
        return parseMapperConfig(conf, mapperDef.getMetadata(), mapperDef.getHadoop());
    }

    public static JobConf parseAggregationConfig(JobConf conf, JobConfiguration.AggregationConfig mapperDef) throws IOException {
        return parseMapperConfig(conf, mapperDef.getMetadata(), mapperDef.getHadoop());
    }

    @SuppressWarnings("rawtypes")
    protected static JobConf parseMapperConfig(JobConf conf, MetadataConfig metadata, HadoopConfig hadoop) throws IOException {

        JobConfiguration.MapConfig mapper = hadoop.getMapper();
        JobConfiguration.ReduceConfig reducer = hadoop.getReduce();
        Class<? extends Mapper> mapperClass = mapper.getMapperClass();
        Class<? extends Writable> inputKeyType = mapper.getInput().getKeyTypeClass();
        Class<? extends Writable> inputValueType = mapper.getInput().getValueTypeClass();
        Class<? extends InputFormat> inputFormatType = mapper.getInput().getFormatTypeClass();
        Class<? extends Writable> outputKeyType = mapper.getOutput().getKeyTypeClass();
        Class<? extends Writable> outputValueType = mapper.getOutput().getValueTypeClass();
        Class<? extends MongoOutputFormat> outputFormatType = mapper.getOutput().getFormatTypeClass();
        String keyFieldName = mapper.getInput().getKeyField();
        String collectionName = mapper.getInput().getCollection();
        Map<String, Object> query = mapper.getInput().getQuery();
        Map<String, Object> fields = mapper.getInput().getFields();
        Map<String, Object> hadoopOptions = hadoop.getOptions();

        Class<? extends Reducer> reducerClass = reducer.getReducerClass();
        String updateCollection = reducer.getCollection();
        String updateField = reducer.getField();

        // validate we have enough to continue
        boolean valid = true;
        if (mapperClass == null) {
            log.severe("Invalid map/reduce configuration detected : no mapper class specified.");
            valid = false;
        }
        if (inputKeyType == null) {
            log.severe("Invalid map/reduce configuration detected : no input key type specified.");
            valid = false;
        }
        if (inputValueType == null) {
            log.severe("Invalid map/reduce configuration detected : no input value type specified.");
            valid = false;
        }
        if (outputKeyType == null) {
            log.severe("Invalid map/reduce configuration detected : no output key type specified.");
            valid = false;
        }
        if (outputValueType == null) {
            log.severe("Invalid map/reduce configuration detected : no output value type specified.");
            valid = false;
        }
        if (keyFieldName == null) {
            log.severe("Invalid map/reduce configuration detected : no key field specified.");
            valid = false;
        }
        if (collectionName == null) {
            log.severe("Invalid map/reduce configuration detected : no mongo collection specified.");
            valid = false;
        }
        if (query == null) {
            log.severe("Invalid map/reduce configuration detected : no mongo input query specified.");
            valid = false;
        }
        if (fields == null) {
            log.severe("Invalid map/reduce configuration detected : no mongo input fields specified.");
            valid = false;
        }
        if (reducerClass == null) {
            log.severe("Invalid map/reduce configuration detected : no reducer class specified.");
            valid = false;
        }
        if (updateCollection == null) {
            log.severe("Invalid map/reduce configuration detected : no output collection specified.");
            valid = false;
        }
        if (updateField == null) {
            log.severe("Invalid map/reduce configuration detected : no output field specified.");
            valid = false;
        }

        if (!valid) {
            throw new IllegalArgumentException("Invalid mapper specified. Check log for details.");
        }

        JobConf mapperConf = new JobConf(conf);
        // enable speculative execution. Multiple mapper tasks are created for the same split.
        // First one to finish wins; the remaining tasks are terminated.
        mapperConf.setSpeculativeExecution(true);
        mapperConf.setUseNewMapper(true);
        mapperConf.setUseNewReducer(true);

        mapperConf.set("JOB_DESCRIPTION", metadata.getDescription());
        mapperConf.set("JOB_ABBREVIATION", metadata.getAbbreviation());
        mapperConf.set("JOB_TYPE", metadata.getType());
        mapperConf.setClass("JOB_VALUE_TYPE", metadata.getValueTypeClass(), Object.class);

        ObjectMapper m = new ObjectMapper();
        BandsConfig bands = metadata.getBands();
        if (bands != null) {
            String s = m.writeValueAsString(bands);
            mapperConf.set("JOB_AGGREGATE_BANDS", s);
        }

        RangeConfig range = metadata.getValidRange();
        if (range != null) {
            String s = m.writeValueAsString(range);
            mapperConf.set("JOB_VALID_RANGE", s);
        }

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

        MongoConfigUtil.setQuery(mapperConf, new BasicDBObject(query));
        MongoConfigUtil.setFields(mapperConf, new BasicDBObject(fields));
        MongoConfigUtil.setInputKey(mapperConf, keyFieldName);
        MongoConfigUtil.setInputURI(mapperConf, "mongodb://localhost:27017/" + collectionName);
        MongoConfigUtil.setMapperOutputKey(mapperConf, outputKeyType);
        MongoConfigUtil.setMapperOutputValue(mapperConf, outputValueType);
        MongoConfigUtil.setOutputKey(mapperConf, outputKeyType);
        MongoConfigUtil.setOutputValue(mapperConf, outputValueType);
        MongoConfigUtil.setReadSplitsFromSecondary(mapperConf, mapper.getInput().getReadFromSecondaries());

        mapperConf.setClass("mapred.input.key.class", inputKeyType, Object.class);
        mapperConf.setClass("mapred.input.value.class", inputValueType, Object.class);
        if (inputFormatType != null) {
            mapperConf.setClass("mapreduce.inputformat.class", inputFormatType, InputFormat.class);
            MongoConfigUtil.setInputFormat(mapperConf,  inputFormatType);
        }

        mapperConf.setClass("mapred.output.key.class", outputKeyType, Object.class);
        mapperConf.setClass("mapred.output.value.class", inputValueType, Object.class);
        if (outputFormatType != null) {
            mapperConf.setClass("mapreduce.outputformat.class", outputFormatType, MongoOutputFormat.class);
            MongoConfigUtil.setOutputFormat(mapperConf, outputFormatType);
        }

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
        MongoConfigUtil.setCreateInputSplits(mapperConf, true);
        MongoConfigUtil.setShardChunkSplittingEnabled(mapperConf, true);
        MongoConfigUtil.setReadSplitsFromShards(mapperConf, false);

        MongoConfigUtil.setOutputURI(mapperConf, "mongodb://localhost:27017/" + updateCollection);
        mapperConf.set(MongoAggFormatter.UPDATE_FIELD, updateField);

        if (reducer.getIdMap() != null) {
            mapperConf.set(JobConfiguration.ID_MAP_PROPERTY, om.writeValueAsString(reducer.getIdMap()));
        }
        if (reducer.getMapField() != null) {
            mapperConf.set(JobConfiguration.MAP_FIELD_PROPERTY, reducer.getMapField());
        }

        if (metadata.getValidRange() != null) {
            mapperConf.set(JobConfiguration.VALID_RANGE_PROPERTY, om.writeValueAsString(metadata.getValidRange()));
        }

        if (metadata.getBands() != null) {
            mapperConf.set(JobConfiguration.BANDS_PROPERTY, om.writeValueAsString(metadata.getBands()));
        }

        /**
         * Any additional hadoop options are added to the configuration as key/value pairs.
         */
        if (hadoopOptions != null) {
            for (Map.Entry<String, Object> option : hadoopOptions.entrySet()) {
                mapperConf.set(option.getKey(), option.getValue().toString());
            }
        }

        mapperConf.setJarByClass(mapperClass);

        MongoConfigUtil.setMapper(mapperConf,  mapperClass);
        mapperConf.setClass(JobContext.MAP_CLASS_ATTR, mapperClass, Mapper.class);

        MongoConfigUtil.setReducer(mapperConf, reducerClass);
        mapperConf.setClass(JobContext.REDUCE_CLASS_ATTR, reducerClass, Reducer.class);

        return mapperConf;
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

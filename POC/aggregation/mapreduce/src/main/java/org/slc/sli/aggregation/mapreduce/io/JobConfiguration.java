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

package org.slc.sli.aggregation.mapreduce.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.hsqldb.lib.StringInputStream;

import org.slc.sli.aggregation.mapreduce.map.IDMapper;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;


/**
 * JobConfiguration - Parse a map/reduce configuration file.  Exposes constants and helper
 * methods to retrieve specific values.
 */
public class JobConfiguration {

    static Logger log = Logger.getLogger("JobConfiguration");
    static ObjectMapper om = new ObjectMapper();

    /**
     * readResource - read configuration from the given resource.
     *
     * @param resourceName Resource to raad configuration from.
     * @throws IOException
     */
    public static ConfigSections readResource(final String configResource) {
        InputStream s = ConfigSections.class.getClassLoader().getResourceAsStream(configResource);
        return readStream(s);
    }

    /**
     * readStream - read configuration from the given input stream.
     *
     * @param s Stream to read configuration from.
     * @throws IOException
     */
    public static ConfigSections readStream(InputStream s) throws IllegalArgumentException {
        ConfigSections sections = null;
        try {
            sections = new ObjectMapper().readValue(s, ConfigSections.class);
        } catch (IOException e) {
            log.severe("Invalid map/reduce configuration detected : parsing failed : "
                + e.toString());
            throw new IllegalArgumentException("Invalid map/reduce configuration detected : parsing failed. Check log for details.");
        }
        return sections;
    }

    /**
     * Read the job configuration from an existing configuration.
     *
     * @param conf configuration to look at.
     * @return ConfigSections if the context was valid, null if not.
     * @throws IOException
     */
    public static ConfigSections readFromConfiguration(final Configuration conf) throws IOException {
        String sliConf = conf.get(JobConfiguration.CONFIGURATION_PROPERTY);
        if (sliConf == null) {
            throw new IOException("Configuration is misssing section: " + JobConfiguration.CONFIGURATION_PROPERTY);
        }
        return readStream(new StringInputStream(sliConf));
    }

    /**
     * ConfigSections - helper class to hold top level configuration sections.
     */
    public static class ConfigSections {
        @JsonProperty(METADATA_PROPERTY)
        private MetadataConfig metadata;
        public void setMetadata(final MetadataConfig v) { metadata = v; }
        public final MetadataConfig getMetadata() { return metadata; }

        @JsonProperty(MAP_PROPERTY)
        private MapConfig mapper;
        public void setMapper(final MapConfig v) { mapper = v; }
        public final MapConfig getMapper() { return mapper; }

        @JsonProperty(REDUCE_PROPERTY)
        private ReduceConfig reducer;
        public void setReduce(final ReduceConfig v) { reducer = v; }
        public final ReduceConfig getReduce() { return reducer; }

        @JsonProperty(SCHEDULE_PROPERTY)
        private ScheduleConfig schedule;
        public void setSchedule(final ScheduleConfig v) { schedule = v; }
        public final ScheduleConfig getSchedule() { return schedule; }
     }


    /**
     * MetadataConfig - helper class to hold metadata configuration.
     */
    public static class MetadataConfig {
        @JsonProperty(NAMESPACE_PROPERTY)
        private String namespace;
        public void setNamespace(final String v) { namespace = v; }
        public final String getNamespace() { return namespace; }

        @JsonProperty(DESCRIPTION_PROPERTY)
        private String description;
        public void setDescription(final String v) { description = v; }
        public final String getDescription() { return description; }

        @JsonProperty(ENTITY_PROPERTY)
        private JobConfiguration.entity entity;
        public void setEntity(final String v) { entity = JobConfiguration.entity.valueOf(v); }
        public final JobConfiguration.entity getEntity() { return entity; }

        @JsonProperty(OPERATION_PROPERTY)
        private JobConfiguration.operation operation;
        public void setOperation(final String v) { operation = JobConfiguration.operation.valueOf(v); }
        public final JobConfiguration.operation getOperation() { return operation; }

        @JsonProperty(FUNCTION_PROPERTY)
        private JobConfiguration.function function;
        public void setFunction(final String v) { function = JobConfiguration.function.valueOf(v); }
        public final JobConfiguration.function getFunction() { return function; }

        @JsonProperty(PARAMETERS_PROPERTY)
        private ParametersConfig parameters;
        public void setParameters(final ParametersConfig v) { parameters = v; }
        public final ParametersConfig getParameters() { return parameters; }

        @JsonProperty(WHAT_PROPERTY)
        private java.util.Map<String, Object> what;
        public void setWhat(final Map<String, Object> v) { what = v; }
        public final Map<String, Object> getWhat() { return what; }

        @JsonProperty(CUT_POINTS_PROPERTY)
        private ArrayList<CutPointConfig> cutPoints;
        public void setCutPoints(final ArrayList<CutPointConfig> v) { cutPoints = v; }
        public final ArrayList<CutPointConfig> getCutPoints() { return cutPoints; }
    }

    /**
     * CutPointsConfig - individual band configuration.
     */
    public static class CutPointConfig {
        @JsonProperty(RANGE_PROPERTY)
        private RangeConfig range;
        public void setRange(final RangeConfig v) { range = v; }
        public final RangeConfig getRange() { return range; }

        @JsonProperty(MATCH_PROPERTY)
        private String match;
        public void setMatch(final String v) { match = v; }
        public final String getMatch() { return match; }

        @JsonProperty(DESCRIPTION_PROPERTY)
        private String description;
        public void setDescription(final String v) { description = v; }
        public final String getDescription() { return description; }

        @JsonProperty(EMIT_PROPERTY)
        private String emit;
        public void setEmit(final String v) { emit = v; }
        public final String getEmit() { return emit; }

        @JsonProperty(RANK_PROPERTY)
        private Long rank;
        public void setRank(final Long v) { rank = v; }
        public final Long getRank() { return rank; }
    }

    /**
     * RangeConfig - helper class to hold range configuration.
     */
    public static class RangeConfig {
        @JsonProperty(MAX_PROPERTY)
        private Double max;
        public void setMax(final Double v) { max = v; }
        public final Double getMax() { return max; }

        @JsonProperty(MIN_PROPERTY)
        private Double min;
        public void setMin(final Double v) { min = v; }
        public final Double getMin() { return min; }
    }

    /**
     * ParametersConfig - helper class to hold job parameters.
     */
    public static class ParametersConfig {

        @JsonProperty(N_PROPERTY)
        private int n;
        public void getN(final int v) { n = v; }
        public final int getN() { return n; }

        @JsonProperty(PERIOD_PROPERTY)
        private JobConfiguration.period periodValue;
        public void setPeriod(final String v) { periodValue = JobConfiguration.period.valueOf(v); }
        public final JobConfiguration.period getPeriod() { return periodValue; }
    }

    /**
     * MapConfig - helper class to hold mapper configuration.
     */
    public static class MapConfig {

        @JsonProperty(MAPPER_PROPERTY)
        private String mapper;
        public void setMapper(final String v) { mapper = v; }
        public final String getMapper() { return mapper; }

        @JsonProperty(COLLECTION_PROPERTY)
        private String collection;
        public void setCollection(final String v) { collection = v; }
        public final String getCollection() { return collection; }

        @JsonProperty(QUERY_PROPERTY)
        private java.util.Map<String, Object> query;
        public void setQuery(final Map<String, Object> v) { query = v; }
        public final Map<String, Object> getQuery() { return query; }

        @JsonProperty(FIELDS_PROPERTY)
        private java.util.Map<String, Object> fields;
        public void setFields(final Map<String, Object> v) { fields = v; }
        public final Map<String, Object> getFields() { return fields; }

        @JsonProperty(MAP_ID_FIELDS_PROPERTY)
        private java.util.Map<String, String> mapIdFields;
        public void setMapIdFields(final Map<String, String> v) { mapIdFields = v; }
        public final Map<String, String> getMapIdFields() { return mapIdFields; }
    }

    /**
     * ReduceConfig - helper class to hold reducer configuration.
     */
    public static class ReduceConfig {

        @JsonProperty(COLLECTION_PROPERTY)
        private String collection;
        public void setCollection(final String v) { collection = v; }
        public final String getCollection() { return collection; }

        @JsonProperty(FIELD_PROPERTY)
        private String field;
        public void setField(final String v) { field = v; }
        public final String getField() { return field; }

        public ReduceConfig() { }
    }

    /**
     * ScheduleConfig - helper class to hold schedule configuration.
     */
    public static class ScheduleConfig {
        @JsonProperty(EVENT_PROPERTY)
        private String event;
        public void setEvent(final String v) { event = v; }
        public final String getEvent() { return event; }

        @JsonProperty(TRIGGER_PROPERTY)
        private java.util.Map<String, Object> trigger;
        public void setTrigger(final Map<String, Object> v) { trigger = v; }
        public final Map<String, Object> getTrigger() { return trigger; }

        @JsonProperty(WAITING_PERIOD_PROPERTY)
        private Long waitingPeriod;
        public void setWaitingPeriod(final Long v) { waitingPeriod = v; }
        public final Long getWaitingPeriod() { return waitingPeriod; }

        @JsonProperty(COMMAND_PROPERTY)
        private String command;
        public void setCommand(final String v) { command = v; }
        public final String getCommand() { return command; }

        @JsonProperty(ARGUMENTS_PROPERTY)
        private String arguments;
        public void setArguments(final String v) { arguments = v; }
        public final String getArguments() { return arguments; }

        @JsonProperty(RETRY_ON_FAILURE_PROPERTY)
        private Boolean retryOnFailure;
        public void setRetryOnFailure(Boolean v) { retryOnFailure = v; }
        public Boolean getRetryOnFailure() { return retryOnFailure; }

        public ScheduleConfig() { }
    }

    public static final String CONFIGURATION_PROPERTY = "configuration";
    public static final String METADATA_PROPERTY = "metadata";
    public static final String NAMESPACE_PROPERTY = "namespace";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String ENTITY_PROPERTY = "entity";
    public static final String OPERATION_PROPERTY = "operation";
    public static final String FUNCTION_PROPERTY = "function";
    public static final String PARAMETERS_PROPERTY = "parameters";
    public static final String N_PROPERTY = "n";
    public static final String PERIOD_PROPERTY = "period";
    public static final String WHAT_PROPERTY = "what";
    public static final String CUT_POINTS_PROPERTY = "cut_points";
    public static final String RANK_PROPERTY = "rank";
    public static final String EMIT_PROPERTY = "emit";
    public static final String RANGE_PROPERTY = "range";
    public static final String MIN_PROPERTY = "min";
    public static final String MAX_PROPERTY = "max";
    public static final String MATCH_PROPERTY = "match";
    public static final String MAP_PROPERTY = "map";
    public static final String MAPPER_PROPERTY = "mapper";
    public static final String COLLECTION_PROPERTY = "collection";
    public static final String QUERY_PROPERTY = "query";
    public static final String FIELDS_PROPERTY = "fields";
    public static final String MAP_ID_FIELDS_PROPERTY = "map_id_fields";
    public static final String REDUCE_PROPERTY = "reduce";
    public static final String FIELD_PROPERTY = "field";
    public static final String SCHEDULE_PROPERTY = "schedule";
    public static final String EVENT_PROPERTY = "event";
    public static final String TRIGGER_PROPERTY = "trigger";
    public static final String WAITING_PERIOD_PROPERTY = "waiting_period";
    public static final String COMMAND_PROPERTY = "command";
    public static final String ARGUMENTS_PROPERTY = "arguments";
    public static final String RETRY_ON_FAILURE_PROPERTY = "retry_on_failure";

    /**
     * Valid functions
     */
    public static enum function {
        Nth_highest, Nth_recent, count, percentage, percentile_rank;

        public static
        Class<? extends Reducer<TenantAndIdEmittableKey, BSONWritable, TenantAndIdEmittableKey, BSONWritable>>
        getReduceClass(function f) {
            Class<? extends Reducer<TenantAndIdEmittableKey, BSONWritable, TenantAndIdEmittableKey, BSONWritable>> rval = null;
            switch (f) {
                case Nth_highest:
                    rval = org.slc.sli.aggregation.functions.Highest.class;
                break;
            }
            return rval;
        }
    }

    /**
     * Valid operations
     */
    public static enum operation {
        calculate_value, aggregate }

    /**
     * Valid entities.
     */
    public static enum entity {
        assessment, attendance, discipline, student }

    /**
     * Valid reporting periods.
     */
    public static enum period {
        session, year, all }

    /**
     * Valid mappers.
     */
    public static enum mapper {
        IDMapper, StringMapper, LongMapper, DoubleMapper, EnumMapper;

        private static IDMapper<TenantAndIdEmittableKey> tmp = new IDMapper<TenantAndIdEmittableKey>();

        @SuppressWarnings("rawtypes")
        public static
        Class<? extends Mapper> getMapClass(mapper m) {
            Class<? extends Mapper> rval = null;

            switch(m) {
                case IDMapper:
                    rval = tmp.getClass();
                break;
                case StringMapper:
                    rval = org.slc.sli.aggregation.mapreduce.map.StringValueMapper.class;
                break;
                case LongMapper:
                    rval = org.slc.sli.aggregation.mapreduce.map.LongValueMapper.class;
                break;
                case DoubleMapper:
                    rval = org.slc.sli.aggregation.mapreduce.map.DoubleValueMapper.class;
                break;
                case EnumMapper:
                    rval = org.slc.sli.aggregation.mapreduce.map.EnumValueMapper.class;
                break;
            }
            return rval;
        }
    }

    /**
     * Write the configuration sections to the provided configuration.
     * @throws IOException
     */
    public static void toHadoopConfiguration(final ConfigSections s, Configuration cfg)
        throws IOException {
        // Add the configuration itself to the JobConf.
        String seralized = om.writeValueAsString(s);
        cfg.set(JobConfiguration.CONFIGURATION_PROPERTY, seralized);
    }

    /**
     * Read the configuration sections from the provided configuration.
     * @param cfg
     * @return
     * @throws IOException
     */
    public static ConfigSections fromHadoopConfiguration(Configuration cfg) throws IOException {
        String seralized = cfg.get("JOB_CONFIGURATION");
        return om.readValue(seralized, ConfigSections.class);
    }
}

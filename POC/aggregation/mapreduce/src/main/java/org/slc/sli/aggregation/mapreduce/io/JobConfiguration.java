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

package org.slc.sli.aggregation.mapreduce.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * JobConfiguration - Parse a map/reduce configuration file.  Exposes constants and helper
 * methods to retrieve specific values.
 */
// Some Jackson annotations were briefly deprecated for version 1.5 - ignore deprecation for now.
@SuppressWarnings("deprecation")
public class JobConfiguration {

    static Logger log = Logger.getLogger("JobConfiguration");

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
     * ConfigSections - helper class to hold top level configuration sections.
     */
    public static class ConfigSections {
        @JsonProperty(CALCULATED_VALUE_PROPERTY)
        private CalculatedValueConfig calculatedValue;
        public void setCalculatedValue(final CalculatedValueConfig v) { calculatedValue = v; }
        public final CalculatedValueConfig getCalculatedValue() { return calculatedValue; }

        @JsonProperty(AGGREGATION_PROPERTY)
        private AggregationConfig aggregation;
        public void setAggregation(final AggregationConfig v) { aggregation = v; }
        public final AggregationConfig getAggregation() { return aggregation; }

        @JsonProperty(SCHEDULE_PROPERTY)
        private ScheduleConfig schedule;
        public void setSchedule(final ScheduleConfig v) { schedule = v; }
        public final ScheduleConfig getSchedule() { return schedule; }

        public ConfigSections() { }
     }

    /**
     *
     * CalculatedValueConfig - helper class to hold calculated value configuration.
     */
    public static class CalculatedValueConfig {
        @JsonProperty(METADATA_PROPERTY)
        private MetadataConfig metadata;
        public void setMetadata(final MetadataConfig v) { metadata = v; }
        public final MetadataConfig getMetadata() { return metadata; }

        @JsonProperty(HADOOP_PROPERTY)
        private HadoopConfig hadoop;
        public void setHadoop(final HadoopConfig v) { hadoop = v; }
        public final HadoopConfig getHadoop() { return hadoop; }

        public CalculatedValueConfig() { }
    }

    /**
     * AggregationConfig - helper class to hold aggregation configuration.
     */
    public static class AggregationConfig {
        @JsonProperty(METADATA_PROPERTY)
        private MetadataConfig metadata;
        public void setMetadata(final MetadataConfig v) { metadata = v; }
        public final MetadataConfig getMetadata() { return metadata; }

        @JsonProperty(HADOOP_PROPERTY)
        private HadoopConfig hadoop;
        public void setHadoop(final HadoopConfig v) { hadoop = v; }
        public final HadoopConfig getHadoop() { return hadoop; }

        public AggregationConfig() { }
    }

    /**
     * MetadataConfig - helper class to hold metadata configuration.
     */
    public static class MetadataConfig {
        @JsonProperty(TYPE_PROPERTY)
        private String type;
        public void setType(final String v) { type = v; }
        public final String getType() { return type; }

        @JsonIgnore
        private Class<?> valueTypeClass;
        public final Class<?> getValueTypeClass() { return valueTypeClass; }

        @JsonProperty(VALUE_TYPE_PROPERTY)
        private String valueType;
        @JsonSetter(VALUE_TYPE_PROPERTY)
        public void setValueType(final String v) throws ClassNotFoundException {
            valueType = v;
            valueTypeClass = Class.forName(valueType);
        }
        @JsonGetter(VALUE_TYPE_PROPERTY)
        public final String getValueType() { return valueType; }

        @JsonProperty(DESCRIPTION_PROPERTY)
        private String description;
        public void setDescription(final String v) { description = v; }
        public final String getDescription() { return description; }

        @JsonProperty(ABBREVIATION_PROPERTY)
        private String abbreviation;
        public void setAbbreviation(final String v) { abbreviation = v; }
        public final String getAbbreviation() { return abbreviation; }

        @JsonProperty(VALID_RANGE_PROPERTY)
        private RangeConfig validRange;
        public void setValidRange(final RangeConfig v) { validRange = v; }
        public final RangeConfig getValidRange() { return validRange; }

        @JsonProperty(BANDS_PROPERTY)
        private BandsConfig bands;
        public void setBands(final BandsConfig v) { bands = v; }
        public final BandsConfig getBands() { return bands; }

        public MetadataConfig() { }
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

        public RangeConfig() { }
    }

    /**
     * BandConfig - individual band configuration.
     */
    public static class BandConfig {
        @JsonProperty(RANGE_PROPERTY)
        private RangeConfig range;
        public void setRange(final RangeConfig v) { range = v; }
        public final RangeConfig getRange() { return range; }

        @JsonProperty(DESCRIPTION_PROPERTY)
        private String description;
        public void setDescription(final String v) { description = v; }
        public final String getDescription() { return description; }

        @JsonProperty(RANK_PROPERTY)
        private Long rank;
        public void setRank(final Long v) { rank = v; }
        public final Long getRank() { return rank; }

        @JsonProperty(ABBREVIATION_PROPERTY)
        private String abbreviation;
        public void setAbbreviation(final String v) { abbreviation = v; }
        public final String getAbbreviation() { return abbreviation; }

        public BandConfig() { }
    }

    /**
     * BandsConfig - helper class to hold bands configuration.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    public static class BandsConfig extends ArrayList<BandConfig> {
        private static final long serialVersionUID = -2686345244528883103L;

        public void setBands(final ArrayList<BandConfig> v) { clear(); addAll(v); }
        public final ArrayList<BandConfig> getBands() { return this; }

        public BandsConfig() { }
    }

    /**
     * HadoopConfig - helper class to hold hadoop configuration.
     */
    public static class HadoopConfig {
        @JsonProperty(MAP_PROPERTY)
        private MapConfig mapper;
        public void setMapper(final MapConfig v) { mapper = v; }
        public final MapConfig getMapper() { return mapper; }

        @JsonProperty(REDUCE_PROPERTY)
        private ReduceConfig reducer;
        public void setReduce(final ReduceConfig v) { reducer = v; }
        public final ReduceConfig getReduce() { return reducer; }

        @JsonProperty(OPTIONS_PROPERTY)
        private Map<String, Object> options;
        public void setOptions(final Map<String, Object> v) { options = v; }
        public final Map<String, Object> getOptions() { return options; }

        public HadoopConfig() { }
    }

    /**
     * MapConfig - helper class to hold mapper configuration.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class MapConfig {

        @JsonIgnore
        private Class<? extends Mapper> mapperClass;
        public final Class<? extends Mapper> getMapperClass() { return mapperClass; }

        @JsonProperty(CLASS_PROPERTY)
        private String mapperType;
        @JsonSetter(CLASS_PROPERTY)
        public void setMapperType(final String v) throws ClassNotFoundException {
            mapperType = v;
            mapperClass = (Class<? extends Mapper>) Class.forName(mapperType);
        }
        @JsonGetter(CLASS_PROPERTY)
        public final String getMapperType() { return mapperType; }

        @JsonProperty(INPUT_PROPERTY)
        private InputConfig input;
        public void setInput(final InputConfig v) { input = v; }
        public final InputConfig getInput() { return input; }

        @JsonProperty(OUTPUT_PROPERTY)
        private OutputConfig output;
        public void setOutput(final OutputConfig v) { output = v; }
        public final OutputConfig getOutput() { return output; }

        public MapConfig() { }
    }

    /**
     * InputConfig - helper class to hold input configuration.
     */
    @SuppressWarnings("unchecked")
    public static class InputConfig {
        @JsonProperty(COLLECTION_PROPERTY)
        private String collection;
        public void setCollection(final String v) { collection = v; }
        public final String getCollection() { return collection; }

        @JsonProperty(KEY_FIELD_PROPERTY)
        private String keyField;
        public void setKeyField(final String v) { keyField = v; }
        public final String getKeyField() { return keyField; }

        @JsonProperty(QUERY_PROPERTY)
        private java.util.Map<String, Object> query;
        public void setQuery(final Map<String, Object> v) { query = v; }
        public final Map<String, Object> getQuery() { return query; }

        @JsonProperty(FIELDS_PROPERTY)
        private java.util.Map<String, Object> fields;
        public void setFields(final Map<String, Object> v) { fields = v; }
        public final Map<String, Object> getFields() { return fields; }

        @JsonIgnore
        private Class<? extends Writable> keyTypeClass;
        public final Class<? extends Writable> getKeyTypeClass() { return keyTypeClass; }

        @JsonProperty(KEY_TYPE_PROPERTY)
        private String keyType;
        @JsonSetter(KEY_TYPE_PROPERTY)
        public void setKeyType(final String v) throws ClassNotFoundException {
            keyType = v;
            keyTypeClass = (Class<? extends Writable>) Class.forName(keyType);
        }
        @JsonGetter(KEY_TYPE_PROPERTY)
        public final String getKeyType() { return keyType; }

        @JsonIgnore
        private Class<? extends Writable> valueTypeClass;
        public final Class<? extends Writable> getValueTypeClass() { return valueTypeClass; }

        @JsonProperty(VALUE_TYPE_PROPERTY)
        private String valueType;
        @JsonSetter(VALUE_TYPE_PROPERTY)
        public void setValueType(final String v) throws ClassNotFoundException {
            valueType = v;
            valueTypeClass = (Class<? extends Writable>) Class.forName(valueType);
        }
        @JsonGetter(VALUE_TYPE_PROPERTY)
        public final String getValueType() { return valueType; }

        @JsonIgnore
        private Class<? extends MongoInputFormat> formatTypeClass;
        public final Class<? extends MongoInputFormat> getFormatTypeClass() { return formatTypeClass; }

        @JsonProperty(FORMAT_TYPE_PROPERTY)
        private String formatType;
        @JsonSetter(FORMAT_TYPE_PROPERTY)
        public void setFormatType(final String v) throws ClassNotFoundException {
            formatType = v;
            formatTypeClass = (Class<? extends MongoInputFormat>) Class.forName(formatType);
        }
        @JsonGetter(FORMAT_TYPE_PROPERTY)
        public final String getFormatType() { return formatType; }

        @JsonProperty(READ_FROM_SECONDARIES_PROPERTY)
        private Boolean readFromSecondaries;
        @JsonSetter(READ_FROM_SECONDARIES_PROPERTY)
        public void setReadFromSecondaries(final Boolean v) { readFromSecondaries = v; }
        @JsonGetter(READ_FROM_SECONDARIES_PROPERTY)
        public final Boolean getReadFromSecondaries() { return readFromSecondaries; }

        public InputConfig() { }
    }

    /**
     * OutputConfig - helper class to hold output configuration.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static class OutputConfig {
        @JsonIgnore
        private Class<? extends Writable> keyTypeClass;
        public final Class<? extends Writable> getKeyTypeClass() { return keyTypeClass; }

        @JsonProperty(KEY_TYPE_PROPERTY)
        private String keyType;
        @JsonSetter(KEY_TYPE_PROPERTY)
        public void setKeyType(final String v) throws ClassNotFoundException {
            keyType = v;
            keyTypeClass = (Class<? extends Writable>) Class.forName(keyType);
        }
        @JsonGetter(KEY_TYPE_PROPERTY)
        public final String getKeyType() { return keyType; }

        @JsonIgnore
        private Class<? extends Writable> valueTypeClass;
        public final Class<? extends Writable> getValueTypeClass() { return valueTypeClass; }

        @JsonProperty(VALUE_TYPE_PROPERTY)
        private String valueType;
        @JsonSetter(VALUE_TYPE_PROPERTY)
        public void setValueType(final String v) throws ClassNotFoundException {
            valueType = v;
            valueTypeClass = (Class<? extends Writable>) Class.forName(valueType);
        }
        @JsonGetter(VALUE_TYPE_PROPERTY)
        public final String getValueType() { return valueType; }

        @JsonIgnore
        private Class<? extends MongoOutputFormat> formatTypeClass;
        public final Class<? extends MongoOutputFormat> getFormatTypeClass() { return formatTypeClass; }

        @JsonProperty(FORMAT_TYPE_PROPERTY)
        private String formatType;
        @JsonSetter(FORMAT_TYPE_PROPERTY)
        public void setFormatType(final String v) throws ClassNotFoundException {
            formatType = v;
            formatTypeClass = (Class<? extends MongoOutputFormat>) Class.forName(formatType);
        }
        @JsonGetter(FORMAT_TYPE_PROPERTY)
        public final String getFormatType() { return formatType; }

        public OutputConfig() { }
    }

    /**
     * ReduceConfig - helper class to hold reducer configuration.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class ReduceConfig {
        @JsonIgnore
        private Class<? extends Reducer> reducerClass;
        public final Class<? extends Reducer> getReducerClass() { return reducerClass; }

        @JsonProperty(CLASS_PROPERTY)
        private String reducerType;
        @JsonSetter(CLASS_PROPERTY)
        public void setReducer(final String v) throws ClassNotFoundException {
            reducerType = v;
            reducerClass = (Class<? extends Reducer>) Class.forName(reducerType);
        }
        @JsonGetter(CLASS_PROPERTY)
        public final String getReducerType() { return reducerType; }

        @JsonProperty(COLLECTION_PROPERTY)
        private String collection;
        public void setCollection(final String v) { collection = v; }
        public final String getCollection() { return collection; }

        @JsonProperty(FIELD_PROPERTY)
        private String field;
        public void setField(final String v) { field = v; }
        public final String getField() { return field; }

        @JsonProperty(ID_MAP_PROPERTY)
        private Map<String, String> idMap;
        public void setIdMap(final Map<String, String> v) { idMap = v; }
        public final Map<String, String> getIdMap() { return idMap; }

        @JsonProperty(MAP_FIELD_PROPERTY)
        private String mapField;
        public void setMapField(final String v) { mapField = v; }
        public final String getMapField() { return mapField; }

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

    /**
     * calculateBands - helper method to extract band information from configuration.
     *
     * @param context
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
    public static MetadataConfig getAggregateMetadata(Configuration config) throws IOException {

        MetadataConfig rval = new MetadataConfig();
        ObjectMapper om = new ObjectMapper();

        String tmp = config.get(JobConfiguration.BANDS_PROPERTY);
        if (tmp != null) {
            BandsConfig cfg = om.readValue(tmp, BandsConfig.class);
            rval.setBands(cfg);
        } else {
            throw new IllegalArgumentException("Invalid configuration found. "
                + "Aggregates must specify the metadata.bands property.");
        }

        if (rval.bands.size() <= 2) {
            throw new IllegalArgumentException("Invalid configuration found. "
                + "Aggregates must specify at least 3 bands, where band[0] specifies "
                + "invalid values, band[1] specifies no value found, and the remaining bands "
                + "map to score range values.");
        }

        tmp = config.get(JobConfiguration.VALID_RANGE_PROPERTY);
        if (tmp != null) {
            rval.validRange = om.readValue(tmp, RangeConfig.class);
        } else {
            throw new IllegalArgumentException("Invalid configuration found. Aggregates must "
                + "specify a metadata.valid_range property.");
        }

        return rval;
    }

    /**
     * Map keys for the configuration file.
     */
    public static enum config_key {
        // CALCULATED_VALUE key
        CALCULATED_VALUE,
            // METADATA key
            METADATA,
                // METADATA values
                TYPE, VALUE_TYPE, DESCRIPTION, ABBREVIATION,
            // HADOOP key
            HADOOP,
                // MAP key
                MAP,
                    // CLASS key
                    CLASS,
                    // INPUT key
                    INPUT,
                        // INPUT values
                        COLLECTION, KEY_FIELD, QUERY, FIELDS, KEY_TYPE, /* VALUE_TYPE, */ FORMAT_TYPE, READ_FROM_SECONDARIES,
                    // OUTPUT key
                    OUTPUT,
                        // OUTPUT values
                        /* KEY_TYPE, VALUE_TYPE, FORMAT_TYPE */
                // REDUCE key
                REDUCE,
                    // REDUCE values
                    /* CLASS, COLLECTION, */ FIELD, ID_MAP, MAP_FIELD,
                // OPTIONS key
                OPTIONS,
        // AGGREGATION key
        AGGREGATION,
            // METADATA key
            /* METADATA, */
                // METADATA values
                /* TYPE, DESCRIPTION, ABBREVIATION, */
                // VALID_RANGE key
                VALID_RANGE,
                    // VALID_RANGE values
                    MIN, MAX,
                // BANDS key
                BANDS,
                    // BANDS values
                    RANK, /* DESCRIPTION, */
                        // RANGE key
                        RANGE,
                            // RANGE values
                            /* MIN, MAX, */
            // HADOOP key
            /* HADOOP, */
                // MAP key
                /* MAP, */
                    // CLASS key
                    /* CLASS, */
                    // INPUT key
                    /* INPUT, */
                        // INPUT values
                        /* COLLECTION, KEY_FIELD, QUERY, FIELDS, KEY_TYPE, VALUE_TYPE, FORMAT_TYPE, READ_FROM_SECONDARIES, */
                    // OUTPUT key
                    /* OUTPUT, */
                        // OUTPUT values
                        /* KEY_TYPE, VALUE_TYPE, FORMAT_TYPE */
                // REDUCE key
                /* REDUCE, */
                    // REDUCE values
                    /* CLASS, COLLECTION, FIELD, */
        SCHEDULE,
            // ScheduleConfig values
            EVENT, WAITING_PERIOD, COMMAND, ARGUMENTS, RETRY_ON_FAILURE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static config_key parseValue(String s) {
            return config_key.valueOf(s.toUpperCase());
        }
    };

    public static final String CALCULATED_VALUE_PROPERTY = "calculated_value";
    public static final String METADATA_PROPERTY = "metadata";
    public static final String TYPE_PROPERTY = "type";
    public static final String VALUE_TYPE_PROPERTY = "value_type";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String ABBREVIATION_PROPERTY = "abbreviation";
    public static final String HADOOP_PROPERTY = "hadoop";
    public static final String MAP_PROPERTY = "map";
    public static final String CLASS_PROPERTY = "class";
    public static final String INPUT_PROPERTY = "input";
    public static final String COLLECTION_PROPERTY = "collection";
    public static final String KEY_FIELD_PROPERTY = "key_field";
    public static final String QUERY_PROPERTY = "query";
    public static final String FIELDS_PROPERTY = "fields";
    public static final String KEY_TYPE_PROPERTY = "key_type";
    public static final String FORMAT_TYPE_PROPERTY = "format_type";
    public static final String READ_FROM_SECONDARIES_PROPERTY = "read_from_secondaries";
    public static final String OUTPUT_PROPERTY = "output";
    public static final String REDUCE_PROPERTY = "reduce";
    public static final String FIELD_PROPERTY = "field";
    public static final String OPTIONS_PROPERTY = "options";
    public static final String AGGREGATION_PROPERTY = "aggregation";
    public static final String VALID_RANGE_PROPERTY = "valid_range";
    public static final String MIN_PROPERTY = "min";
    public static final String MAX_PROPERTY = "max";
    public static final String BANDS_PROPERTY = "bands";
    public static final String RANK_PROPERTY = "rank";
    public static final String RANGE_PROPERTY = "range";
    public static final String SCHEDULE_PROPERTY = "schedule";
    public static final String EVENT_PROPERTY = "event";
    public static final String WAITING_PERIOD_PROPERTY = "waiting_period";
    public static final String COMMAND_PROPERTY = "command";
    public static final String ARGUMENTS_PROPERTY = "arguments";
    public static final String RETRY_ON_FAILURE_PROPERTY = "retry_on_failure";
    public static final String ID_MAP_PROPERTY = "id_map";
    public static final String MAP_FIELD_PROPERTY = "map_field";

    /**
     * Placeholder values that are substituted with their real values by the top level M/R job.
     */
    public static final String ID_PLACEHOLDER = "@ID@";
    public static final String TENANT_ID_PLACEHOLDER = "@TENANT_ID@";

    /**
     * For assessments, the score type to use when calculating values and aggregates.
     */
    public static final String ASSESSMENT_SCORE_TYPE = "ASSESSMENT_SCORE_TYPE";
}

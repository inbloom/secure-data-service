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

package org.slc.sli.stamper.mapreduce.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.stamper.mapreduce.io.JobConfiguration;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.AggregationConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.BandConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.BandsConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.ConfigSections;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.HadoopConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.InputConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.MapConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.MetadataConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.OutputConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.RangeConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.ReduceConfig;
import org.slc.sli.stamper.mapreduce.io.JobConfiguration.ScheduleConfig;
import org.slc.sli.stamper.mapreduce.io.MongoAggFormatter;
import org.slc.sli.stamper.mapreduce.io.MongoIdInputFormat;
import org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.stamper.mapreduce.reduce.Highest;

/**
 * ConfigurationTest
 */
public class ConfigurationTest {

    String multipleMapperConf = null;
    ObjectMapper om = new ObjectMapper();

    @Test
    public void testCalculatedValueConfig() throws Exception {

        Configuration initialConf = new Configuration();
        initialConf.set("@ID@", "123-456-789-1011-1213-1415");
        JobConf conf = org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(initialConf, "CalculatedValue.json");

        assertEquals(conf.getMapOutputKeyClass(), IdFieldEmittableKey.class);
        assertEquals(conf.getMapOutputValueClass(), BSONWritable.class);
        assertEquals(MongoConfigUtil.getInputFormat(conf), MongoIdInputFormat.class);
        assertEquals(MongoConfigUtil.getOutputFormat(conf), MongoAggFormatter.class);
        assertEquals(MongoConfigUtil.getReducer(conf), Highest.class);

        DBObject obj = MongoConfigUtil.getQuery(conf);
        assertEquals("123-456-789-1011-1213-1415", obj.get("body.assessmentId"));
        assertEquals("Scale score", obj.get("body.scoreResults.assessmentReportingMethod"));
    }

    @Test
    public void testAggregateConfig() throws Exception {
        ConfigSections sections = JobConfiguration.readResource("Aggregate.json");

        AggregationConfig agg = sections.getAggregation();
        assertNotNull(agg);

        MetadataConfig meta = agg.getMetadata();
        assertNotNull(meta);
        RangeConfig validRange = meta.getValidRange();
        assertNotNull(validRange);
        assertEquals(validRange.getMin(), 0.0D, 0.01D);
        assertEquals(validRange.getMax(), 100.0D, 0.01D);
        BandsConfig bandsConfig = meta.getBands();
        assertNotNull(bandsConfig);
        assertNotNull(bandsConfig.getBands());
        ArrayList<BandConfig> bands = bandsConfig.getBands();
        assertEquals(bands.size(), 5);

        BandConfig config = bands.get(0);
        assertEquals(config.getRank().longValue(), -1L);
        assertEquals(config.getDescription(), "invalid score");
        assertNull(config.getRange());

        config = bands.get(1);
        assertEquals(config.getRank().longValue(), 0L);
        assertEquals(config.getDescription(), "no score");
        assertNull(config.getRange());

        config = bands.get(2);
        assertEquals(config.getRank().longValue(), 1L);
        assertEquals(config.getDescription(), "below standard");
        RangeConfig range = config.getRange();
        assertNotNull(range);
        assertEquals(range.getMin(), 0.0D, 0.01D);
        assertEquals(range.getMax(), 70.0D, 0.01D);

        config = bands.get(3);
        assertEquals(config.getRank().longValue(), 2L);
        assertEquals(config.getDescription(), "at standard");
        range = config.getRange();
        assertNotNull(range);
        assertEquals(range.getMin(), 71.0D, 0.01D);
        assertEquals(range.getMax(), 85.0D, 0.01D);

        config = bands.get(4);
        assertEquals(config.getRank().longValue(), 3L);
        assertEquals(config.getDescription(), "above standard");
        range = config.getRange();
        assertNotNull(range);
        assertEquals(range.getMin(), 86.0D, 0.01D);
        assertEquals(range.getMax(), 100.0D, 0.01D);

        assertEquals(meta.getAbbreviation(), "HE-AGG-SMA-7");
        assertEquals(meta.getType(), "count");
        assertNull(meta.getValueType());
        assertNull(meta.getValueTypeClass());

        HadoopConfig hadoop = agg.getHadoop();
        assertNotNull(hadoop);
        MapConfig map = hadoop.getMapper();
        assertNotNull(map);
        assertEquals(map.getMapperClass(), IDMapper.class);
        InputConfig in = map.getInput();
        assertNotNull(in);
        assertEquals(in.getCollection(), "sli.student");
        assertEquals(in.getFormatTypeClass(), MongoIdInputFormat.class);
        assertEquals(in.getKeyField(), "_id");
        assertEquals(in.getKeyTypeClass(), IdFieldEmittableKey.class);
        assertEquals(in.getReadFromSecondaries(), true);
        assertEquals(in.getValueTypeClass(), BSONWritable.class);
        assertNotNull(in.getFields());
        Map<String, Object> fields = in.getFields();
        assertEquals(fields.size(), 1);
        assertTrue(fields.containsKey("calculatedValues.assessments.@ID@.HighestEver.ScaleScore"));
        assertEquals(fields.get("calculatedValues.assessments.@ID@.HighestEver.ScaleScore"), 1);

        Map<String, Object> query = in.getQuery();
        assertEquals(query.size(), 0);

        OutputConfig out = map.getOutput();
        assertNotNull(out);
        assertEquals(out.getFormatTypeClass(), MongoAggFormatter.class);
        assertEquals(out.getKeyTypeClass(), IdFieldEmittableKey.class);
        assertEquals(out.getValueTypeClass(), BSONWritable.class);

        ReduceConfig reduce = hadoop.getReduce();
        assertNotNull(reduce);
        assertEquals(reduce.getCollection(), "sli.school");
        assertEquals(reduce.getField(), "aggregates.assessments.@ID@.ScaleScore");
        assertEquals(reduce.getReducerClass(), Highest.class);


        Map<String, Object> options = hadoop.getOptions();
        assertNotNull(options);
        assertTrue(options.containsKey("skipTests"));
        assertEquals(options.get("skipTests"), true);
    }

    @Test
    public void testScheduleConfig() throws Exception {
        ConfigSections sections = JobConfiguration.readResource("Schedule.json");
        ScheduleConfig schedule = sections.getSchedule();
        assertNotNull(schedule);
        assertEquals(schedule.getEvent(), "create student | create SAA");
        assertEquals(schedule.getWaitingPeriod().longValue(), 600L);
        assertEquals(schedule.getCommand(), "hadoop");
        assertEquals(schedule.getRetryOnFailure(), false);
        assertEquals(schedule.getArguments(), "jar HighestEver.jar StateMath_conf.json -fs hdfs://localhost:9001 -jt http://localhost:9000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCollection() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "NoCollection.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoFields() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "NoFields.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoKeyField() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "NoKeyField.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoQuery() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "NoQUery.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAMapper() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "NotAMapper.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownMapper() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "UnknownMapper.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingClasses() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "MissingClasses.json");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingRootKey() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "MissingRootKey.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigFile() throws Exception {
        Configuration emptyConf = new Configuration();
        org.slc.sli.stamper.mapreduce.map.ConfigurableMapReduceJob.parseMapper(emptyConf, "NotJson.json");

    }

    @Test
    public void testChainedMapperEntry() {
        JobConfiguration.config_key e = JobConfiguration.config_key.READ_FROM_SECONDARIES;
        assertEquals(e.toString(), "read_from_secondaries");

        e = JobConfiguration.config_key.parseValue("Read_From_Secondaries");
        assertEquals(e, JobConfiguration.config_key.READ_FROM_SECONDARIES);
    }
}

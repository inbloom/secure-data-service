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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ConfigSections;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.CutPointConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.MapConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.MetadataConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.RangeConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ReduceConfig;
import org.slc.sli.aggregation.mapreduce.io.JobConfiguration.ScheduleConfig;

/**
 * ConfigurationTest
 */
public class ConfigurationTest {

    String multipleMapperConf = null;
    ObjectMapper om = new ObjectMapper();

    @Test
    public void testCalculatedValueConfig() throws Exception {
        ConfigSections sections = JobConfiguration.readResource("CalculatedValue.json");

        MetadataConfig meta = sections.getMetadata();
        assertNotNull(meta);

        MapConfig map = sections.getMapper();
        assertNotNull(map);
        assertEquals(map.getMapper(), JobConfiguration.mapper.IDMapper.toString());
        assertEquals(map.getCollection(), "sli.studentAssessmentAssociation");
        assertNotNull(map.getFields());
        Map<String, Object> fields = map.getFields();
        assertEquals(fields.size(), 1);
        assertTrue(fields.containsKey("body.scoreResults.result"));

        Map<String, Object> query = map.getQuery();
        assertEquals(query.size(), 4);

        ReduceConfig reduce = sections.getReduce();
        assertNotNull(reduce);
        assertEquals(reduce.getCollection(), "sli.student");
        assertEquals(reduce.getField(), "calculatedValues.assessments.State Math for Grade 5.Highest");
    }

    @Test
    public void testAggregateConfig() throws Exception {
        ConfigSections sections = JobConfiguration.readResource("Aggregate.json");

        MetadataConfig meta = sections.getMetadata();
        assertNotNull(meta);
        ArrayList<CutPointConfig> cutPoints = meta.getCutPoints();
        assertNotNull(cutPoints);
        assertEquals(cutPoints.size(), 5);

        CutPointConfig config = cutPoints.get(0);
        assertEquals(config.getRank().longValue(), -1L);
        assertEquals(config.getDescription(), "invalid score");
        assertNull(config.getRange());

        config = cutPoints.get(1);
        assertEquals(config.getRank().longValue(), 0L);
        assertEquals(config.getDescription(), "no score");
        assertNull(config.getRange());

        config = cutPoints.get(2);
        assertEquals(config.getRank().longValue(), 1L);
        assertEquals(config.getDescription(), "below standard");
        RangeConfig range = config.getRange();
        assertNotNull(range);
        assertEquals(range.getMin(), 0.0D, 0.01D);
        assertEquals(range.getMax(), 70.0D, 0.01D);

        config = cutPoints.get(3);
        assertEquals(config.getRank().longValue(), 2L);
        assertEquals(config.getDescription(), "at standard");
        range = config.getRange();
        assertNotNull(range);
        assertEquals(range.getMin(), 71.0D, 0.01D);
        assertEquals(range.getMax(), 85.0D, 0.01D);

        config = cutPoints.get(4);
        assertEquals(config.getRank().longValue(), 3L);
        assertEquals(config.getDescription(), "exceeds standard");
        range = config.getRange();
        assertNotNull(range);
        assertEquals(range.getMin(), 86.0D, 0.01D);
        assertEquals(range.getMax(), 100.0D, 0.01D);


        MapConfig map = sections.getMapper();
        assertNotNull(map);
        assertEquals(map.getMapper(), JobConfiguration.mapper.DoubleMapper.toString());
        assertEquals(map.getCollection(), "sli.student");
        assertNotNull(map.getFields());
        Map<String, Object> fields = map.getFields();
        assertEquals(fields.size(), 1);
        assertTrue(fields.containsKey("calculatedValues.assessments.State Math for Grade 5.Highest"));
        assertEquals(fields.get("calculatedValues.assessments.State Math for Grade 5.Highest"), 1);

        Map<String, Object> query = map.getQuery();
        assertEquals(query.size(), 1);

        ReduceConfig reduce = sections.getReduce();
        assertNotNull(reduce);
        assertEquals(reduce.getCollection(), "sli.educationOrganization");
        assertEquals(reduce.getField(), "aggregates.assessments.State Math for Grade 5.Highest");
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
        ConfigurableMapReduceJob.parse(emptyConf, "NoCollection.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoFields() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "NoFields.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoKeyField() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "NoKeyField.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoQuery() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "NoQUery.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAMapper() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "NotAMapper.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownMapper() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "UnknownMapper.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingClasses() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "MissingClasses.json");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingRootKey() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "MissingRootKey.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigFile() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableMapReduceJob.parse(emptyConf, "NotJson.json");

    }
}

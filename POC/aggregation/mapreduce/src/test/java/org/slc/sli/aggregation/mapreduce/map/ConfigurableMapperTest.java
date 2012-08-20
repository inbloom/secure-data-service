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

import static org.junit.Assert.assertEquals;

import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.aggregation.mapreduce.io.JobConfiguration;
import org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter;
import org.slc.sli.aggregation.mapreduce.io.MongoIdInputFormat;
import org.slc.sli.aggregation.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.aggregation.mapreduce.reduce.Highest;

/**
 * ConfigurableMapperTest
 */
public class ConfigurableMapperTest {

    String multipleMapperConf = null;
    ObjectMapper om = new ObjectMapper();

    @Test
    public void testCalculatedValueConfig() throws Exception {

        Configuration initialConf = new Configuration();
        initialConf.set("@ID@", "123-456-789-1011-1213-1415");
        JobConf conf = ConfigurableCalculatedValue.parseMapper(initialConf, "CalculatedValue.json");

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
    public void testDerivedValueConfig() throws Exception {
        assertEquals("Not implemented yet", "");
    }

    @Test
    public void testScheduleConfig() throws Exception {
        assertEquals("Not implemented yet", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCollection() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "NoCollection.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoFields() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "NoFields.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoKeyField() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "NoKeyField.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoQuery() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "NoQUery.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAMapper() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "NotAMapper.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownMapper() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "UnknownMapper.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingClasses() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "MissingClasses.json");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingRootKey() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "MissingRootKey.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigFile() throws Exception {
        Configuration emptyConf = new Configuration();
        ConfigurableCalculatedValue.parseMapper(emptyConf, "NotJson.json");

    }

    @Test
    public void testChainedMapperEntry() {
        JobConfiguration.config_key e = JobConfiguration.config_key.READ_FROM_SECONDARIES;
        assertEquals(e.toString(), "read_from_secondaries");

        e = JobConfiguration.config_key.parseValue("Read_From_Secondaries");
        assertEquals(e, JobConfiguration.config_key.READ_FROM_SECONDARIES);
    }
}

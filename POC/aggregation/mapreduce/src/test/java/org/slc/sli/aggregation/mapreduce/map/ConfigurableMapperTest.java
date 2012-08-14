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
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.bson.BSONObject;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.hadoop.util.MongoConfigUtil;

/**
 * ConfigurableMapperTest
 */
public class ConfigurableMapperTest {
    private DB db = mock(DB.class);
    
    @Test
    public void testSingleMapperJobConf() throws Exception {
        
        Map<String, Object> input = readInput("SingleMapper.json");
        
        DBCollection coll = mock(DBCollection.class);
        when(db.getCollection("assessment")).thenReturn(coll);
        when(coll.findOne(new BasicDBObject("body.assessmentIdentificationCode.ID", "Grade 7 2011 State Math"),
                new BasicDBObject("_id", "1"))).thenReturn(new BasicDBObject("_id", "42"));
        ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder(db);
        Job job = mapper.makeJob(new JobConf(), input);
        
        assertEquals(IDMapper.class, job.getMapperClass());
        assertEquals(TenantAndIdEmittableKey.class, job.getMapOutputKeyClass());
        assertEquals(BSONObject.class, job.getMapOutputValueClass());
        
        String query = MongoConfigUtil.getQuery(job.getConfiguration()).toString();
        assertEquals(query, "{ \"body.assessmentId\" : \"42\"}");
    }
    
    private Map<String, Object> readInput(String resource) throws IOException, JsonParseException, JsonMappingException {
        InputStream s = getClass().getClassLoader().getResourceAsStream(resource);
        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> input = objectMapper.readValue(s, Map.class);
        return input;
    }
    
    // @Test(expected = IllegalArgumentException.class)
    // public void testNoCollection() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("NoCollection.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testNoFields() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("NoFields.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testNoKeyField() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("NoKeyField.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testNoQuery() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("NoQuery.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testNotAMapper() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("NotAMapper.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testUnknownMapper() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("UnknownMapper.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testMissingClasses() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("MissingClasses.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testMissingRootKey() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("MissingRootKey.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set("Bad", chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testInvalidConfigFile() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("NotJson.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set(ConfigurableMapperBuilder.CHAIN_CONF, chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testNoChainConf() throws Exception {
    //
    // InputStream s = getClass().getClassLoader().getResourceAsStream("ChainedMapper.json");
    // String chainedMapperConf = IOUtils.toString(s, "UTF-8");
    //
    // JobConf conf = new JobConf();
    // conf.set("some_other_key", chainedMapperConf);
    //
    // ConfigurableMapperBuilder mapper = new ConfigurableMapperBuilder();
    // mapper.configure(conf);
    // }
    
    @Test
    public void testChainedMapperEntry() {
        ConfigurableMapperBuilder.MapperEntry e = ConfigurableMapperBuilder.MapperEntry.READ_FROM_SECONDARIES;
        assertEquals(e.toString(), "read_from_secondaries");
        
        e = ConfigurableMapperBuilder.MapperEntry.parseValue("Read_From_Secondaries");
        assertEquals(e, ConfigurableMapperBuilder.MapperEntry.READ_FROM_SECONDARIES);
    }
}

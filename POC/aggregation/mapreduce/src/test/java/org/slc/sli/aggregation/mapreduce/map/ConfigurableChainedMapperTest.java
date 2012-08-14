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
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.Stringifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.bson.BSONObject;
import org.junit.Test;

import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * ConfigurableChainedMapperTest
 */
public class ConfigurableChainedMapperTest {

    String multipleMapperConf = null;

    @Test
    public void testSingleMapperJobConf() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("SingleMapper.json");
        String singleMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, singleMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);

        assertEquals(conf.getClass("chain.mapper.mapper.class.0", Object.class), IDMapper.class);
        assertEquals(conf.getMapOutputKeyClass(), TenantAndIdEmittableKey.class);
        assertEquals(conf.getMapOutputValueClass(), BSONObject.class);

        String tmp = conf.get("chain.mapper.mapper.config.0");
        assertNotNull(tmp);

        Stringifier<JobConf> stringifier = new DefaultStringifier<JobConf>(conf, JobConf.class);
        JobConf mapperConf = stringifier.fromString(tmp);
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class), Text.class);
        assertEquals(mapperConf.getClass("chain.mapper.input.value.class", Object.class),
            BSONObject.class);

        String query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query,
            "{ \"body.assessmentIdentificationCode.ID\" : \"Grade 7 2011 State Math\"}");
    }

    @Test
    public void testChainnedMapperJobConf() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("ChainedMapper.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);

        assertEquals(conf.getMapOutputKeyClass(), TenantAndIdEmittableKey.class);
        assertEquals(conf.getMapOutputValueClass(), BSONObject.class);

        assertEquals(conf.getClass("chain.mapper.mapper.class.0", Object.class), IDMapper.class);
        String tmp = conf.get("chain.mapper.mapper.config.0");
        assertNotNull(tmp);

        Stringifier<JobConf> stringifier = new DefaultStringifier<JobConf>(conf, JobConf.class);
        JobConf mapperConf = stringifier.fromString(tmp);
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class),
            TenantAndIdEmittableKey.class);

        String query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query,
            "{ \"body.assessmentIdentificationCode.ID\" : \"Grade 7 2011 State Math\"}");

        assertEquals(conf.getClass("chain.mapper.mapper.class.1", Object.class), IDMapper.class);
        tmp = conf.get("chain.mapper.mapper.config.1");
        assertNotNull(tmp);

        mapperConf = stringifier.fromString(tmp);
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class),
            TenantAndIdEmittableKey.class);
        assertEquals(mapperConf.getClass("chain.mapper.input.value.class", Object.class),
            BSONObject.class);

        query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query, "{ \"body.assessmentId\" : \"getId()\"}");

        assertEquals(conf.getClass("chain.mapper.mapper.class.2", Object.class), IDMapper.class);
        tmp = conf.get("chain.mapper.mapper.config.2");
        assertNotNull(tmp);

        mapperConf = stringifier.fromString(tmp);
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class),
            TenantAndIdEmittableKey.class);
        assertEquals(mapperConf.getClass("chain.mapper.input.value.class", Object.class),
            BSONObject.class);

        query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query, "{ \"_id\" : \"getId()\"}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCollection() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("NoCollection.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoFields() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("NoFields.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoKeyField() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("NoKeyField.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoQuery() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("NoQuery.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAMapper() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("NotAMapper.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownMapper() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("UnknownMapper.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingClasses() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("MissingClasses.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingRootKey() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("MissingRootKey.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set("Bad", chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfigFile() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("NotJson.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoChainConf() throws Exception {

        InputStream s = getClass().getClassLoader().getResourceAsStream("ChainedMapper.json");
        String chainedMapperConf = IOUtils.toString(s, "UTF-8");

        JobConf conf = new JobConf();
        conf.set("some_other_key", chainedMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);
    }

    @Test
    public void testChainedMapperEntry() {
        ConfigurableChainedMapper.mapper_entry e =
            ConfigurableChainedMapper.mapper_entry.READ_FROM_SECONDARIES;
        assertEquals(e.toString(), "read_from_secondaries");

        e = ConfigurableChainedMapper.mapper_entry.parseValue("Read_From_Secondaries");
        assertEquals(e, ConfigurableChainedMapper.mapper_entry.READ_FROM_SECONDARIES);
    }
}

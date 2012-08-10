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

import com.mongodb.hadoop.util.MongoConfigUtil;

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

    static final String singleMapperConf =  "{\"map_chain\" : [ {\n"
        + "\"map_class\" : \"org.slc.sli.aggregation.mapreduce.map.IDMapper\",\n"
        + "\"input_collection\" : \"sli.assessment\",\n"
        + "\"input_key_field\" : \"_id\",\n"
        + "\"input_key_type\" : \"org.apache.hadoop.io.Text\",\n"
        + "\"input_value_type\" : \"org.bson.BSONObject\",\n"
        + "\"read_from_secondaries\" : true,\n"
        + "\"output_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"output_value_type\" : \"org.bson.BSONObject\",\n"
        + "\"by_value\" : false,\n"
        + "\"query\" : \"{ \\\"body.assessmentIdentificationCode.ID\\\" : \\\"Grade 7 2011 State Math\\\" }\",\n"
        + "\"fields\" : \"{ \\\"_id\\\" : 1 }\",\n"
        + "\"hadoop_options\" : \"[ {\\\"biz\\\":\\\"bar\\\"} ]\""
        + " } ] }\n";

    static final String chainedMapperConf =  "{\"map_chain\" : [\n"
    	+ "{\n"
        + "\"map_class\" : \"org.slc.sli.aggregation.mapreduce.map.IDMapper\",\n"
        + "\"input_collection\" : \"sli.assessment\",\n"
        + "\"input_key_field\" : \"_id\",\n"
        + "\"input_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"input_value_type\" : \"org.bson.BSONObject\",\n"
        + "\"read_from_secondaries\" : true,\n"
        + "\"by_value\" : false,\n"
        + "\"query\" : \"{ \\\"body.assessmentIdentificationCode.ID\\\" : \\\"Grade 7 2011 State Math\\\" }\",\n"
        + "\"fields\" : \"{ \\\"metaData.tenantId\\\" : 1, \\\"_id\\\" : 1 }\",\n"
        + "\"output_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"output_value_type\" : \"org.bson.BSONObject\"\n"
        + "}, "

        + "{\n"
        + "\"map_class\" : \"org.slc.sli.aggregation.mapreduce.map.IDMapper\",\n"
        + "\"input_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"input_value_type\" : \"org.bson.BSONObject\",\n"
        + "\"input_collection\" : \"sli.studentAssessmentAssociation\",\n"
        + "\"input_key_field\" : \"body.assessmentId\",\n"
        + "\"read_from_secondaries\" : true,\n"
        + "\"by_value\" : false,\n"
        + "\"query\" : \"{ \\\"body.assessmentId\\\" : \\\"$INPUT_KEY.getId()\\\" }\",\n"
        + "\"fields\" : \"{ \\\"metaData.tenantId\\\" : 1, \\\"body.studentId\\\" : 1 }\",\n"
        + "\"output_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"output_value_type\" : \"org.bson.BSONObject\"\n"
        + " }, {\n"
        + "\"map_class\" : \"org.slc.sli.aggregation.mapreduce.map.IDMapper\",\n"
        + "\"input_key_field\" : \"_id\",\n"
        + "\"input_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"input_value_type\" : \"org.bson.BSONObject\",\n"
        + "\"input_collection\" : \"sli.student\",\n"
        + "\"read_from_secondaries\" : true,\n"
        + "\"by_value\" : false,\n"
        + "\"query\" : \"{ \\\"_id\\\" : \\\"$INPUT_KEY.getId()\\\" }\",\n"
        + "\"fields\" : \"{ \\\"metaData.tenantId\\\" : 1, \\\"_id\\\" : 1, \\\"body.gender\\\" : 1 }\",\n"
        + "\"hadoop_options\" : \"[ {\\\"biz\\\":\\\"bar\\\"} ]\",\n"
        + "\"output_key_type\" : \"org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey\",\n"
        + "\"output_value_type\" : \"org.bson.BSONObject\"\n"
        + "} ] }\n";

    String multipleMapperConf = null;

    @Test
    public void testSingleMapperJobConf() throws Exception {

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
        assertEquals(mapperConf.getClass("chain.mapper.input.value.class", Object.class), BSONObject.class);

        String query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query, "{ \"body.assessmentIdentificationCode.ID\" : \"Grade 7 2011 State Math\"}");
    }

    @Test
    public void testChainnedMapperJobConf() throws Exception {

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
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class), TenantAndIdEmittableKey.class);

        String query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query, "{ \"body.assessmentIdentificationCode.ID\" : \"Grade 7 2011 State Math\"}");

        assertEquals(conf.getClass("chain.mapper.mapper.class.1", Object.class), IDMapper.class);
        tmp = conf.get("chain.mapper.mapper.config.1");
        assertNotNull(tmp);

        mapperConf = stringifier.fromString(tmp);
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class), TenantAndIdEmittableKey.class);
        assertEquals(mapperConf.getClass("chain.mapper.input.value.class", Object.class), BSONObject.class);

        query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query, "{ \"body.assessmentId\" : \"$INPUT_KEY.getId()\"}");

        assertEquals(conf.getClass("chain.mapper.mapper.class.2", Object.class), IDMapper.class);
        tmp = conf.get("chain.mapper.mapper.config.2");
        assertNotNull(tmp);

        mapperConf = stringifier.fromString(tmp);
        assertEquals(mapperConf.getClass("chain.mapper.input.key.class", Object.class), TenantAndIdEmittableKey.class);
        assertEquals(mapperConf.getClass("chain.mapper.input.value.class", Object.class), BSONObject.class);

        query = MongoConfigUtil.getQuery(mapperConf).toString();
        assertEquals(query, "{ \"_id\" : \"$INPUT_KEY.getId()\"}");

    }
}

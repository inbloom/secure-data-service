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

import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;

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
        + "\"query\" : \"{\\\"body.assessmentIdentificationCode.ID\\\" : \\\"Grade 7 2011 State Math\\\" }\",\n"
        + "\"fields\" : \"{ \\\"_id\\\" : 1 }\",\n"
        + "\"hadoop_options\" : \"[ {\\\"biz\\\":\\\"bar\\\"} ]\""
        + " } ] }\n";

    String multipleMapperConf = null;

    @Test
    public void testSingleMapperJobConf() {

        JobConf conf = new JobConf();
        conf.set(ConfigurableChainedMapper.CHAIN_CONF, singleMapperConf);

        ConfigurableChainedMapper mapper = new ConfigurableChainedMapper();
        mapper.configure(conf);

    }
}

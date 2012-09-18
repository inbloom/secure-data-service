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

package org.slc.sli.dal.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ElasticSearchRepositoryTest {

    //@Resource(name = "mongoEntityRepository")
    //private Repository<Entity> repository;

    @Test
    public void testConvertJsonToSearchHit() throws Exception {

        String json = "{\"_index\":\"midgar\",\"_type\":\"course\",\"_id\":\"id\",\"_score\":1.0," +
        		"\"fields\":{\"body\":{\"subjectArea\":\"Mathematics\",\"courseTitle\":\"6th Grade Math\"}," +
                "\"metaData\":{\"tenantId\":\"Midgar\",\"teacherContext\":[\"2012di\",\"2012eq\"]," +
        		"\"edOrgs\":[\"2012rn\"],\"externalId\":\"6th Grade Math\"}}}";

        // create json node
        ObjectMapper mapper = new ObjectMapper();
        JsonNode hitNode = mapper.readTree(json);
        //System.out.println(hitNode);

        // call method
        Entity hit = ElasticSearchRepository.Converter.convertJsonToSearchHitEntity(hitNode);

        // check result
        assertNotNull(hit);
        assertEquals(hit.getEntityId(), "id");
        assertEquals(hit.getBody().get("subjectArea"), "Mathematics");
        assertEquals(hit.getMetaData().get("tenantId"), "Midgar");

    }

    @Test
    public void testGetQuery() throws Exception {

        // create transport client, neutral query
        TransportClient esClient = new TransportClient();
        NeutralQuery query = new NeutralQuery();
        query.setLimit(100);
        query.setOffset(0);
        NeutralCriteria crit1 = new NeutralCriteria("query=matt");
        query.addCriteria(crit1);
        NeutralCriteria crit11 = new NeutralCriteria("temp=temptemp");
        query.addCriteria(crit11);
        NeutralQuery orQuery = new NeutralQuery();
        NeutralCriteria crit2 = new NeutralCriteria("test=1");
        orQuery.addCriteria(crit2);
        query.addOrQuery(orQuery);

        // make the getQuery call
        SearchRequestBuilder srb = ElasticSearchRepository.Converter.getQuery(esClient, query, "tenant");
        String srbStr = srb.toString();
        //System.out.println(srbStr);

        // check results
        ObjectMapper mapper = new ObjectMapper();
        JsonNode srbNode = mapper.readTree(srbStr);
        assertEquals(srbNode.get("from").getIntValue(), 0);
        assertEquals(srbNode.get("size").getIntValue(), 100);
        assertEquals(srbNode.get("query").get("bool").get("must").get("query_string").get("query").getTextValue(), "+matt*");
        assertEquals(srbNode.get("filter").get("bool").get("must").get("terms").get("temp").getElements().next().asText(), "temptemp");
        assertEquals(srbNode.get("filter").get("bool").get("should").get("terms").get("test").getElements().next().asText(), "1");
        assertEquals(srbNode.get("fields").getElements().next().asText(), "id");
    }


}

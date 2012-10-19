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
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.ElasticSearchRepository.EntityConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ElasticSearchRepositoryTest {

    ElasticSearchQueryConverter converter;

    @Test
    public void testConvertJsonToSearchHit() throws Exception {

        String json = "{\"_index\" : \"midgar\"," +
                      "\"_type\" : \"student\"," +
                      "\"_id\" : \"someid\"," +
                      "\"_score\" : 1.0," +
                      "\"_source\" : {\"otherName\":[]," +
                      "               \"name\":{\"middleName\":\"Daniella\",\"lastSurname\":\"Ortiz\",\"firstName\":\"Carmen\"}," +
                      "               \"context\":{}}}";

        // create json node
        ObjectMapper mapper = new ObjectMapper();
        JsonNode hitNode = mapper.readTree(json);
        //System.out.println(hitNode);

        // call method
        Entity hit = EntityConverter.convertJsonToSearchHitEntity(hitNode);

        // check result
        assertNotNull(hit);
        assertEquals(hit.getEntityId(), "someid");
        Map<String, Object> name = (Map<String, Object>) hit.getBody().get("name");
        assertEquals(name.get("firstName"), "Carmen");
        assertNull(hit.getBody().get("context"));
        assertNull(hit.getMetaData());

    }

    @Test
    public void testGetQuery() throws Exception {

        // create transport client, neutral query
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
        QueryBuilder srb = converter.getQuery(query);
        String srbStr = srb.toString();
        //System.out.println(srbStr);

        // check results
        ObjectMapper mapper = new ObjectMapper();
        JsonNode srbNode = mapper.readTree(srbStr);
        assertEquals(srbNode.get("from").getIntValue(), 0);
        assertEquals(srbNode.get("size").getIntValue(), 100);
        assertEquals(srbNode.get("query").get("bool").get("must").get("query_string").get("query").getTextValue(), "matt*");
        assertEquals(srbNode.get("filter").get("bool").get("must").get("terms").get("temp").getElements().next().asText(), "temptemp");
        assertEquals(srbNode.get("filter").get("bool").get("should").get("terms").get("test").getElements().next().asText(), "1");
    }

    @Test
    public void testGetQueryExactMatch() throws Exception {

        // create transport client, neutral query
        NeutralQuery query = new NeutralQuery();
        query.setLimit(100);
        query.setOffset(0);
        NeutralCriteria crit1 = new NeutralCriteria("query=ma");
        query.addCriteria(crit1);

        // make the getQuery call
        QueryBuilder srb = converter.getQuery(query);
        String srbStr = srb.toString();
        //System.out.println(srbStr);

        // check results
        ObjectMapper mapper = new ObjectMapper();
        JsonNode srbNode = mapper.readTree(srbStr);
        assertEquals(srbNode.get("from").getIntValue(), 0);
        assertEquals(srbNode.get("size").getIntValue(), 100);
        assertEquals(srbNode.get("query").get("bool").get("must").get("query_string").get("query").getTextValue(), "ma");
    }

}

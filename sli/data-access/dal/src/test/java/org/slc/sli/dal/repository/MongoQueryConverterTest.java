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

import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.NeutralCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoQueryConverterTest {

    @Autowired
    private MongoQueryConverter mongoQueryConverter; //class under test
    
    @Test
    public void testKeyPrefixing() {
        
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("metadata.x", "=", "1");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("metadata.x", "=", "1", false);
        NeutralCriteria neutralCriteria3 = new NeutralCriteria("_id", "=", "1");
        NeutralCriteria neutralCriteria4 = new NeutralCriteria("metadata._id", "=", "1");

        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria1), "body.metadata.x");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria2), "metadata.x");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria3), "_id");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria4), "body.metadata._id");
    }

    @Test
    public void testAddCriteria() {
        Query query = new Query();
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("eventDate", ">=", "2011-09-08");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("eventDate", "<=", "2012-04-08");

        List<NeutralCriteria> list = new ArrayList<NeutralCriteria>();
        list.add(neutralCriteria1);
        list.add(neutralCriteria2);

        Map<String, List<NeutralCriteria>> map = new HashMap<String, List<NeutralCriteria>>();
        map.put("eventDate", list);

        query = mongoQueryConverter.addCriteria(query, map);

        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertTrue("Should not be null", obj.containsField("body.eventDate"));

        DBObject criteria = (DBObject) obj.get("body.eventDate");
        assertNotNull("Should not be null", criteria.get("$gte"));
        assertNotNull("Should not be null", criteria.get("$lte"));
    }

    @Test
    public void testNullAddCriteria() {
        Query query = new Query();

        query = mongoQueryConverter.addCriteria(query, null);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertEquals("Should match", 0, obj.keySet().size());

        assertNull("Should be null", mongoQueryConverter.addCriteria(null, null));
        assertNull("Should be null", mongoQueryConverter.addCriteria(null,
                new HashMap<String, List<NeutralCriteria>>()));

    }

    @Test
    public void testIncludeFieldConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setIncludeFields("populationServed,uniqueSectionCode");

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getFieldsObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", 1, obj.get("body.populationServed"));
        assertEquals("Should match", 1, obj.get("body.uniqueSectionCode"));
    }

    @Test
    public void testExcludeFieldConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setExcludeFields("populationServed,uniqueSectionCode");

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getFieldsObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", 0, obj.get("body.populationServed"));
        assertEquals("Should match", 0, obj.get("body.uniqueSectionCode"));
    }

    @Test
    public void testOffsetAndLimitConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(10);
        neutralQuery.setLimit(100);

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        assertEquals("Should match", 10, query.getSkip());
        assertEquals("Should match", 100, query.getLimit());
    }

    @Test
    public void testSortConvert() {
        //test ascending
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setSortBy("populationServed");
        neutralQuery.setSortOrder(NeutralQuery.SortOrder.ascending);
        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getSortObject();
        assertEquals("Should match", 1, obj.get("body.populationServed"));

        //test descending
        neutralQuery = new NeutralQuery();
        neutralQuery.setSortBy("populationServed");
        neutralQuery.setSortOrder(NeutralQuery.SortOrder.descending);
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getSortObject();
        assertEquals("Should match", -1, obj.get("body.populationServed"));

        //test null sort by
        neutralQuery = new NeutralQuery();
        neutralQuery.setSortBy(null);
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        assertNull("Should be null", query.getSortObject());

        //test null sort order
        neutralQuery = new NeutralQuery();
        neutralQuery.setSortBy("populationServed");
        neutralQuery.setSortOrder(null);
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getSortObject();
        assertEquals("Should match", 1, obj.get("body.populationServed"));
    }

    @Test(expected = QueryParseException.class)
    public void testPIISort() throws QueryParseException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setSortBy("name.firstName");
        neutralQuery.setSortOrder(NeutralQuery.SortOrder.ascending);

        mongoQueryConverter.convert("student", neutralQuery);
    }

    @Test(expected = QueryParseException.class)
    public void testPIISearchGreaterThan() throws QueryParseException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("name.firstName", ">", "somevalue"));

        mongoQueryConverter.convert("student", neutralQuery);
    }

    @Test(expected = QueryParseException.class)
    public void testPIISearchLessThan() throws QueryParseException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("name.firstName", "<", "somevalue"));

        mongoQueryConverter.convert("student", neutralQuery);
    }

    @Test(expected = QueryParseException.class)
    public void testPIISearchNotEqual() throws QueryParseException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("name.firstName", "~", "somevalue"));

        mongoQueryConverter.convert("student", neutralQuery);
    }

    @Test
    public void testPIISearchEquals() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("name.firstName", "=", "somevalue"));

        Query query = mongoQueryConverter.convert("student", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", "ENCRYPTED_STRING:somevalue", obj.get("body.name.firstName"));
        assertEquals("Should match", "somevalue", neutralQuery.getCriteria().get(0).getValue());
    }

    @Test
    public void testFieldsConvert() {
        //test equals
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", "=", "Regular Students"));
        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", "Regular Students", obj.get("body.populationServed"));

        //test greater than equals
        neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", ">=", "Regular Students"));
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        DBObject obj1 = (DBObject) obj.get("body.populationServed");
        assertEquals("Should match", "Regular Students", obj1.get("$gte"));

        //test greater than equals
        neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", "<=", "Regular Students"));
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        obj1 = (DBObject) obj.get("body.populationServed");
        assertEquals("Should match", "Regular Students", obj1.get("$lte"));

        //test in
        List<String> list = new ArrayList<String>();
        list.add("Regular Students");
        neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", NeutralCriteria.CRITERIA_IN, list));
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        obj1 = (DBObject) obj.get("body.populationServed");
        assertNotNull(obj1.get("$in"));
    }

    @Test(expected = QueryParseException.class)
    public void testInNotList() throws QueryParseException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", NeutralCriteria.CRITERIA_IN, "Regular Students"));

        mongoQueryConverter.convert("section", neutralQuery);
    }

    @Test
    public void testOrConvert() {
        NeutralQuery neutralQuery1 = new NeutralQuery();
        neutralQuery1.addCriteria(new NeutralCriteria("populationServed", "=", "Regular Students"));

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("uniqueSectionCode", "=", "some value"));
        neutralQuery.addOrQuery(neutralQuery1);

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        assertNotNull("Should not be null", obj.get("$or"));
    }


}

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


package org.slc.sli.dal.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;

import org.bson.types.BasicBSONList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoQueryConverterTest {

    @Autowired
    private MongoQueryConverter mongoQueryConverter; //class under test

    /**
     * Use of a regular expression against an integer (any non-string field) is not permitted at this time.
     */
    @Test(expected = QueryParseException.class)
    public void testRegexForInt() {
        convertToRegexMongoQueryString("section", "sequenceOfCourse", "1");
    }

    /**
     * Use of a regular expression against a double (any non-string field) is not permitted at this time.
     */
    @Test(expected = QueryParseException.class)
    public void testRegexForDouble() {
        convertToRegexMongoQueryString("reportCard", "gpaGivenGradingPeriod", "1");
    }

    /**
     * Use of a regular expression against a boolean (any non-string field) is not permitted at this time.
     */
    @Test(expected = QueryParseException.class)
    public void testRegexForBoolean() {
        convertToRegexMongoQueryString("studentSectionAssociation", "homeroomIndicator", "true");
    }

    /**
     * Internal structures that hold other fields (such as an array) do not allow regular expressions.
     */
    @Test(expected = QueryParseException.class)
    public void testRegexForInternalArray() {
        convertToRegexMongoQueryString("student", "address", "foo");
    }

    /**
     * Internal structures that hold other fields (such as a map) do not allow regular expressions.
     */
    @Test(expected = QueryParseException.class)
    public void testRegexForInternalMap() {
        convertToRegexMongoQueryString("learningStandard", "learningStandardId", "foo");
    }

    /**
     * Regular expressions against String (text) based fields should be permitted to become regex mongo queries.
     */
    @Test
    public void testRegexForString() {

        String entityType = "educationOrganization";
        String field = "nameOfInstitution";
        String value = "whatever";

        String regexMongoQueryString = convertToRegexMongoQueryString(entityType, field, value);
        String desiredRegex = "{ \"body." + field + "\" : { \"$regex\" : \"" + value + "\"}}";

        assertTrue(regexMongoQueryString.contains(desiredRegex));
    }

    /**
     * Takes in an entity type, field and value, and uses the mongo query converter to create a Query
     * that regex's against that field, and then returns that query as a String.
     *
     * This method will throw exceptions if the field is an array or map, OR if the field being
     * regexed is not of type String.
     *
     * @param entityType
     * @param field
     * @param value
     * @return
     */
    private String convertToRegexMongoQueryString(String entityType, String field, String value) throws QueryParseException {

        NeutralCriteria neutralCriteria = new NeutralCriteria(field, "=~", value);
        NeutralQuery neutralQuery = new NeutralQuery(neutralCriteria);
        Query query = mongoQueryConverter.convert(entityType, neutralQuery); //this is what might throw it

        return query.getQueryObject().toString();
    }

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
    public void testMergeCriteria() {
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("eventDate", ">=", "2011-09-08");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("eventDate", "<=", "2012-04-08");

        List<NeutralCriteria> list = new ArrayList<NeutralCriteria>();
        list.add(neutralCriteria1);
        list.add(neutralCriteria2);

        Map<String, List<NeutralCriteria>> map = new HashMap<String, List<NeutralCriteria>>();
        map.put("eventDate", list);

        List<Criteria> criteriaMerged = mongoQueryConverter.mergeCriteria(map);

        assertNotNull("Should not be null", criteriaMerged);
        assertNotNull("Should not be null", criteriaMerged.get(0));
        DBObject obj = criteriaMerged.get(0).getCriteriaObject();
        assertTrue("Should not be null", obj.containsField("body.eventDate"));

        DBObject criteria = (DBObject) obj.get("body.eventDate");
        assertNotNull("Should not be null", criteria.get("$gte"));
        assertNotNull("Should not be null", criteria.get("$lte"));
    }

    @Test
    public void testNullMergeCriteria() {
        List<Criteria> criteriaMerged = mongoQueryConverter.mergeCriteria(null);
        assertNotNull("Should not be null", criteriaMerged);
        assertEquals("Should match", 0, criteriaMerged.size());
    }

    @Test
    public void testIncludeFieldConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setIncludeFieldString("populationServed,uniqueSectionCode");

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getFieldsObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", 1, obj.get("body.populationServed"));
        assertEquals("Should match", 1, obj.get("body.uniqueSectionCode"));
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
        list.add("Irregular Students");
        neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", NeutralCriteria.CRITERIA_IN, list));
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        obj1 = (DBObject) obj.get("body.populationServed");
        assertNotNull(obj1.get("$in"));

        //test exists
        neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", NeutralCriteria.CRITERIA_EXISTS, true));
        query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        obj1 = (DBObject) obj.get("body.populationServed");
        assertNotNull(obj1.get("$exists"));
    }

    @Test(expected = QueryParseException.class)
    public void testInNotList() throws QueryParseException {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("populationServed", NeutralCriteria.CRITERIA_IN, "Regular Students"));

        mongoQueryConverter.convert("section", neutralQuery);
    }


    /**
     * Checks the conversion of NeutralQueries (containing ORed criteria)
     * into Mongo-appropriate Query objects.
     *
     * This test uses an example similar to:
     *
     * select *
     *   from student
     *  where economicDisadvantaged = true
     *    and studentUniqueStateId = '000000054')
     *
     */
    @Test
    public void testOrConvert() {
        NeutralQuery mainQuery = new NeutralQuery();

        //not part of the or, so added to the main query
        mainQuery.addCriteria(new NeutralCriteria("economicDisadvantaged=true"));

        //construct a query representing all the criteria in 1 or branch
        NeutralQuery orQuery1 = new NeutralQuery();

        //construct a query representing all the criteria in a second or branch
        NeutralQuery orQuery2 = new NeutralQuery();
        orQuery2.addCriteria(new NeutralCriteria("studentUniqueStateId", "=", "000000054"));

        //add the or queries
        mainQuery.addOrQuery(orQuery1);
        mainQuery.addOrQuery(orQuery2);

        //the converter will convert the NeutralQuery into a mongo Query Object
        Query query = mongoQueryConverter.convert("student", mainQuery);

        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertNotNull("Should not be null", obj);
        assertNotNull("Should not be null", obj.get("$or"));
        assertTrue(((BasicBSONList) obj.get("$or")).size() == 1);
    }

    @Test
    public void testEmptyIncludeFieldConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getFieldsObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", 1, obj.get("body"));
        assertEquals("Should match", 1, obj.get("type"));
        assertEquals("Should match", 1, obj.get("metaData"));
    }

    @Test
    public void testEmbeddedFieldConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setEmbeddedFields(Arrays.asList("studentSectionAssociation", "teacherSectionAssociation"));

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getFieldsObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", 1, obj.get("studentSectionAssociation"));
        assertEquals("Should match", 1, obj.get("teacherSectionAssociation"));
        assertEquals("Should match", 1, obj.get("body"));
        assertEquals("Should match", 1, obj.get("type"));
        assertEquals("Should match", 1, obj.get("metaData"));
    }

    @Test
    public void testNonEmptyIncludeFieldConvert() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setIncludeFieldString("name");

        Query query = mongoQueryConverter.convert("section", neutralQuery);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getFieldsObject();
        assertNotNull("Should not be null", obj);
        assertEquals("Should match", 1, obj.get("body.name"));
        assertEquals("Should match", 1, obj.get("type"));
        assertEquals("Should match", 1, obj.get("metaData"));
        assertNull("Should be null", obj.get("body"));
    }


}

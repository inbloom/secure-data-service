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


package org.slc.sli.ingestion.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

/**
 * JUnits for testing the NeutralRecordRepository class.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/processor-test.xml" })
public class NeutralRecordRepositoryTest {

    @Autowired
    private NeutralRecordRepository repository;

    private MongoTemplate mockedMongoTemplate;

    private int recordId = 1000000;

    @Before
    public void setup() {

        // Setup the mocked Mongo Template.
        mockedMongoTemplate = mock(MongoTemplate.class);
        repository.setTemplate(mockedMongoTemplate);
    }

    @Test
    @Ignore
    public void testCRUDNeutralRecordRepository() {

        // create new student neutral record
        NeutralRecord student = buildTestStudentNeutralRecord();

        // test save
        NeutralRecord saved = repository.create(student, "student");
        String id = saved.getRecordId();
        assertTrue(!id.equals(""));

        // test findAll
        List<NeutralRecord> expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(student);
        when(mockedMongoTemplate.find(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                expectedRecords);
        NeutralQuery neutralQuery1 = new NeutralQuery();
        neutralQuery1.setLimit(20);
        Iterable<NeutralRecord> records = repository.findAll("student", neutralQuery1);
        assertNotNull(records);
        NeutralRecord found = records.iterator().next();
        assertEquals(found.getAttributes().get("birthDate"), student.getAttributes().get("birthDate"));
        assertEquals((found.getAttributes()).get("firstName"), "Jane");
        assertEquals((found.getAttributes()).get("lastName"), "Doe");

        // test find by id
        when(mockedMongoTemplate.findById(any(Object.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                student);
        NeutralRecord foundOne = repository.findById("student", saved.getRecordId());
        assertNotNull(foundOne);
        assertEquals(foundOne.getAttributes().get("birthDate"), student.getAttributes().get("birthDate"));
        assertEquals((found.getAttributes()).get("firstName"), "Jane");

        // test find by field
        NeutralQuery neutralQuery2 = new NeutralQuery();
        neutralQuery2.addCriteria(new NeutralCriteria("firstName", "=", "Jane"));
        neutralQuery2.setLimit(20);
        Iterable<NeutralRecord> searchResults = repository.findAll("student", neutralQuery2);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getAttributes().get("firstName"), "Jane");
        searchResults = repository.findAll("student", neutralQuery2);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getAttributes().get("firstName"), "Jane");

        // test find by query
        NeutralQuery neutralQuery3 = new NeutralQuery();
        neutralQuery3.addCriteria(new NeutralCriteria("body.firstName", "=", "Jane", false));
        when(mockedMongoTemplate.find(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                expectedRecords);
        searchResults = repository.findAll("student", neutralQuery3);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getAttributes().get("firstName"), "Jane");

        NeutralQuery neutralQuery4 = new NeutralQuery();
        neutralQuery4.addCriteria(new NeutralCriteria("body.birthDate", "<", "2011-10-01", false));
        searchResults = repository.findAll("student", neutralQuery4);
        assertTrue(searchResults.iterator().hasNext());
        searchResults = repository.findAll("student", new NeutralQuery());
        assertTrue(searchResults.iterator().hasNext());

        // test update
        found.setAttributeField("firstName", "Mandy");
        WriteResult goodResult = mock(WriteResult.class);
        when(goodResult.getN()).thenReturn(1);
        when(
                mockedMongoTemplate.updateFirst(Mockito.any(Query.class), Mockito.any(Update.class),
                        Mockito.eq("student"))).thenReturn(goodResult);
        assertTrue(repository.update("student", found, false));
        records = repository.findAll("student", neutralQuery1);
        assertNotNull(records);
        NeutralRecord updated = records.iterator().next();
        assertEquals(updated.getAttributes().get("firstName"), "Mandy");

        // test delete by id
        NeutralRecord student2Body = buildTestStudentNeutralRecord();
        NeutralRecord student2 = repository.create(student2Body, "student");
        records = repository.findAll("student", neutralQuery1);
        assertNotNull(records.iterator().next());
        when(
                mockedMongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(student2);
        repository.delete("student", student2.getRecordId());
        when(
                mockedMongoTemplate.findById(Mockito.any(Object.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(null);
        NeutralRecord zombieStudent = repository.findById("student", student2.getRecordId());
        assertNull(zombieStudent);
        WriteResult badResult = mock(WriteResult.class);
        when(badResult.getN()).thenReturn(0);
        when(
                mockedMongoTemplate.updateFirst(Mockito.any(Query.class), Mockito.any(Update.class),
                        Mockito.eq("student"))).thenReturn(badResult);
        assertFalse(repository.update("student", student2, false));
        when(
                mockedMongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(null);
        assertFalse(repository.delete("student", student2.getRecordId()));

        // test deleteAll by neutral record type
        repository.deleteAll("teacher", null);
        when(mockedMongoTemplate.find(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student")))
                .thenReturn(new LinkedList<NeutralRecord>());
        records = repository.findAll("student", neutralQuery1);
        assertFalse(records.iterator().hasNext());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testSort() {

        // clean up the existing student data
        repository.deleteAll("student", null);

        // create new student neutral record
        NeutralRecord body1 = buildTestStudentNeutralRecord();
        NeutralRecord body2 = buildTestStudentNeutralRecord();
        NeutralRecord body3 = buildTestStudentNeutralRecord();
        NeutralRecord body4 = buildTestStudentNeutralRecord();

        body1.setAttributeField("firstName", "Austin");
        body2.setAttributeField("firstName", "Jane");
        body3.setAttributeField("firstName", "Mary");
        body4.setAttributeField("firstName", "Suzy");

        body1.setAttributeField("performanceLevels", new String[] { "1" });
        body2.setAttributeField("performanceLevels", new String[] { "2" });
        body3.setAttributeField("performanceLevels", new String[] { "3" });
        body4.setAttributeField("performanceLevels", new String[] { "4" });

        // save records
        repository.create(body1, "student");
        repository.create(body2, "student");
        repository.create(body3, "student");
        repository.create(body4, "student");

        // sort records by firstName with ascending order
        List<NeutralRecord> expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body1);
        expectedRecords.add(body2);
        expectedRecords.add(body3);
        expectedRecords.add(body4);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setSortBy("firstName");
        when(mockedMongoTemplate.find(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                expectedRecords);
        Iterable<NeutralRecord> records = repository.findAll("student", neutralQuery);
        assertNotNull(records);
        Iterator<NeutralRecord> it = records.iterator();
        assertEquals("Austin", it.next().getAttributes().get("firstName"));
        assertEquals("Jane", it.next().getAttributes().get("firstName"));
        assertEquals("Mary", it.next().getAttributes().get("firstName"));
        assertEquals("Suzy", it.next().getAttributes().get("firstName"));

        // sort records by firstName with descending order
        expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body4);
        expectedRecords.add(body3);
        expectedRecords.add(body2);
        expectedRecords.add(body1);
        when(mockedMongoTemplate.find(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                expectedRecords);
        neutralQuery.setSortOrder(NeutralQuery.SortOrder.descending);
        records = repository.findAll("student", neutralQuery);
        assertNotNull(records);
        it = records.iterator();
        assertEquals("Suzy", it.next().getAttributes().get("firstName"));
        assertEquals("Mary", it.next().getAttributes().get("firstName"));
        assertEquals("Jane", it.next().getAttributes().get("firstName"));
        assertEquals("Austin", it.next().getAttributes().get("firstName"));

        // sort records by performanceLevels which is an array with ascending order
        List<String> performanceLevels1 = new LinkedList<String>();
        List<String> performanceLevels2 = new LinkedList<String>();
        List<String> performanceLevels3 = new LinkedList<String>();
        List<String> performanceLevels4 = new LinkedList<String>();
        performanceLevels1.add("1");
        performanceLevels2.add("2");
        performanceLevels3.add("3");
        performanceLevels4.add("4");
        body1.setAttributeField("performanceLevels", performanceLevels1);
        body2.setAttributeField("performanceLevels", performanceLevels2);
        body3.setAttributeField("performanceLevels", performanceLevels3);
        body4.setAttributeField("performanceLevels", performanceLevels4);
        expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body1);
        expectedRecords.add(body2);
        expectedRecords.add(body3);
        expectedRecords.add(body4);
        NeutralQuery neutralQuery2 = new NeutralQuery();
        neutralQuery2.setSortBy("performanceLevels");
        when(mockedMongoTemplate.find(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                expectedRecords);
        records = repository.findAll("student", neutralQuery2);
        assertNotNull(records);
        it = records.iterator();
        assertEquals("1", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("4", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));

        // sort records by performanceLevels which is an array with descending order
        neutralQuery2.setSortOrder(NeutralQuery.SortOrder.descending);
        expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body4);
        expectedRecords.add(body3);
        expectedRecords.add(body2);
        expectedRecords.add(body1);
        when(mockedMongoTemplate.find(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                expectedRecords);
        records = repository.findAll("student", neutralQuery2);
        assertNotNull(records);
        it = records.iterator();
        assertEquals("4", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("1", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
    }

    private NeutralRecord buildTestStudentNeutralRecord() {

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", String.valueOf(++recordId));
        body.put("firstName", "Jane");
        body.put("lastName", "Doe");
        // Date birthDate = new Timestamp(23234000);
        body.put("birthDate", "2000-01-01");
        body.put("cityOfBirth", "Chicago");
        body.put("CountyOfBirth", "US");
        body.put("dateEnteredUs", "2011-01-01");
        body.put("displacementStatus", "some");
        body.put("economicDisadvantaged", true);
        body.put("generationCodeSuffix", "Z");
        body.put("hispanicLatinoEthnicity", true);
        body.put("limitedEnglishProficiency", "Yes");
        body.put("maidenName", "Smith");
        body.put("middleName", "Patricia");
        body.put("multipleBirthStatus", true);
        body.put("oldEthnicity", "AMERICAN_INDIAN_OR_ALASKAN_NATIVE");
        body.put("personalInformationVerification", "verified");
        body.put("personalTitlePrefix", "Miss");
        body.put("profileThumbnail", "doej23.png");
        body.put("schoolFoodServicesEligibility", "REDUCED_PRICE");
        body.put("sex", "Female");
        body.put("stateOfBirthAbbreviation", "IL");
        body.put("studentSchoolId", "DOE-JANE-222");

        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordId("1a-2b-3c-4d-5e");
        neutralRecord.setRecordType("student");
        neutralRecord.setAttributes(body);
        return neutralRecord;
    }

    @Test
    @Ignore
    public void testFindIdsByQuery() {
        repository.deleteAll("student", null);

        // create new student neutral record
        repository.create(buildTestStudentNeutralRecord(), "student");
        repository.create(buildTestStudentNeutralRecord(), "student");
        repository.create(buildTestStudentNeutralRecord(), "student");
        repository.create(buildTestStudentNeutralRecord(), "student");
        repository.create(buildTestStudentNeutralRecord(), "student");

        List<NeutralRecord> expectedRecords = new LinkedList<NeutralRecord>();
        for (int rec = 1; rec <= 5; rec++) {
            expectedRecords.add(buildTestStudentNeutralRecord());
        }
        when(mockedMongoTemplate.find(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student")))
                .thenReturn(expectedRecords);
        Iterable<String> ids = repository.findAllIds("student", new NeutralQuery());
        List<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
        }

        assertEquals(5, idList.size());
    }

    @Test
    public void testInsertRetry() {
        TenantContext.setTenantId("SLIUnitTest");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", 123);
        body.put("firstName", "Jane");
        body.put("lastName", "Doe");
        body.put("studentSchoolId", "LNAME-FNAME-000");
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordId("testInsert");
        neutralRecord.setRecordType("student");
        neutralRecord.setAttributes(body);

        when(mockedMongoTemplate.findOne(any(Query.class), eq(NeutralRecord.class), eq("student"))).thenReturn(
                neutralRecord);

        repository.insertWithRetries(neutralRecord, 2);

        Query mongoQuery = new Query(new Criteria("studentSchoolId=LNAME-FNAME-000"));

        assertEquals(neutralRecord, repository.findOne("student", mongoQuery));
    }

    @Test
    public void testInsertAllRetry() {
        TenantContext.setTenantId("SLIUnitTest");
        String collectionName = "student";

        List<NeutralRecord> batchOfRecords = new ArrayList<NeutralRecord>();
        batchOfRecords.add(buildTestStudentNeutralRecord());
        batchOfRecords.add(buildTestStudentNeutralRecord());

        DBCollection collection = Mockito.mock(DBCollection.class);
        when(collection.count(any(DBObject.class))).thenReturn(2L);
        when(mockedMongoTemplate.getCollection(eq(collectionName))).thenReturn(collection);

        repository.insertAllWithRetries(batchOfRecords, collectionName, 3);

        Query mongoQuery = new Query();

        assertEquals(2, repository.countByQuery(collectionName, mongoQuery));
    }

    @Test
    public void testInsertWithError() {

        NeutralRecordRepository mockRepo = Mockito.spy(repository);
        NeutralRecord record = buildTestStudentNeutralRecord();
        int noOfRetries = 5;

        Mockito.doThrow(new MongoException("Test Exception")).when(mockRepo).insert(record);

        try {
            mockRepo.insertWithRetries(record, noOfRetries);
        } catch (MongoException ex) {
            assertEquals(ex.getMessage(), "Test Exception");
        }

        Mockito.verify(mockRepo, Mockito.times(noOfRetries)).insert(record);
    }

    @Test
    public void testInsertAllWithError() {

        NeutralRecordRepository mockRepo = Mockito.spy(repository);
        String collectionName = "student";
        List<NeutralRecord> batchOfRecords = new ArrayList<NeutralRecord>();
        batchOfRecords.add(buildTestStudentNeutralRecord());
        batchOfRecords.add(buildTestStudentNeutralRecord());

        int noOfRetries = 5;

        Mockito.doThrow(new DataAccessResourceFailureException("Test Exception")).when(mockRepo).insertAll(batchOfRecords, collectionName);

        try {
            mockRepo.insertAllWithRetries(batchOfRecords, collectionName, noOfRetries);
        } catch (DataAccessResourceFailureException ex) {
            assertEquals(ex.getMessage(), "Test Exception");
        }

        Mockito.verify(mockRepo, Mockito.times(noOfRetries)).insertAll(batchOfRecords, collectionName);
    }

    @Test
    public void testRetry() {
        NeutralRecordRepository mockRepo = Mockito.spy(repository);
        NeutralRecord record = buildTestStudentNeutralRecord();
        int noOfRetries = 5;

        Mockito.doThrow(new UncategorizedMongoDbException("Operation Failed", new MongoException("Test Exception"))).doReturn(record).when(mockRepo).insert(record);
        mockRepo.insertWithRetries(record, noOfRetries);

        Mockito.verify(mockRepo, Mockito.times(2)).insert(record);
    }
}

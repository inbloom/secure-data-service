package org.slc.sli.ingestion.dal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import com.mongodb.WriteResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * JUnits for testing the NeutralRecordRepository class.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
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
    public void testCRUDNeutralRecordRepository() {

        // create new student neutral record
        NeutralRecord student = buildTestStudentNeutralRecord();

        // test save
        NeutralRecord saved = repository.create(student);
        String id = saved.getLocalId().toString();
        assertTrue(!id.equals(""));

        // test findAll
        List<NeutralRecord> expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(student);
        when(mockedMongoTemplate.find(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student")))
                .thenReturn(expectedRecords);
        Iterable<NeutralRecord> records = repository.findAll("student", 0, 20);
        assertNotNull(records);
        NeutralRecord found = records.iterator().next();
        assertEquals(found.getAttributes().get("birthDate"), student.getAttributes().get("birthDate"));
        assertEquals((found.getAttributes()).get("firstName"), "Jane");
        assertEquals((found.getAttributes()).get("lastName"), "Doe");

        // test find by id
        when(
                mockedMongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(student);
        NeutralRecord foundOne = repository.findByLocalId("student", saved.getLocalId().toString());
        assertNotNull(foundOne);
        assertEquals(foundOne.getAttributes().get("birthDate"), student.getAttributes().get("birthDate"));
        assertEquals((found.getAttributes()).get("firstName"), "Jane");

        // test find by field
        Map<String, String> searchFields = new HashMap<String, String>();
        searchFields.put("firstName", "Jane");
        Iterable<NeutralRecord> searchResults = repository.findByFields("student", searchFields, 0, 20);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getAttributes().get("firstName"), "Jane");
        searchResults = repository.findByFields("student", searchFields);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getAttributes().get("firstName"), "Jane");

        // test find by query
        Query query = new Query();
        query.addCriteria(Criteria.where("body.firstName").is("Jane"));
        when(mockedMongoTemplate.find(query, NeutralRecord.class, "student")).thenReturn(expectedRecords);
        searchResults = repository.findByQuery("student", query, 0, 20);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getAttributes().get("firstName"), "Jane");
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("body.birthDate").lt("2011-10-01"));
        searchResults = repository.findByQuery("student", query1, 0, 20);
        assertTrue(searchResults.iterator().hasNext());
        query = null;
        searchResults = repository.findByQuery("student", query, 0, 20);
        assertTrue(searchResults.iterator().hasNext());

        // test update
        found.getAttributes().put("firstName", "Mandy");
        WriteResult goodResult = mock(WriteResult.class);
        when(goodResult.getN()).thenReturn(1);
        when(
                mockedMongoTemplate.updateFirst(Mockito.any(Query.class), Mockito.any(Update.class),
                        Mockito.eq("student"))).thenReturn(goodResult);
        assertTrue(repository.update(found));
        records = repository.findAll("student", 0, 20);
        assertNotNull(records);
        NeutralRecord updated = records.iterator().next();
        assertEquals(updated.getAttributes().get("firstName"), "Mandy");

        // test delete by id
        NeutralRecord student2Body = buildTestStudentNeutralRecord();
        NeutralRecord student2 = repository.create(student2Body);
        records = repository.findAll("student", 0, 20);
        assertNotNull(records.iterator().next());
        when(
                mockedMongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(student2);
        repository.deleteByLocalId("student", student2.getLocalId().toString());
        when(
                mockedMongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(null);
        NeutralRecord zombieStudent = repository.findByLocalId("student", student2.getLocalId().toString());
        assertNull(zombieStudent);
        WriteResult badResult = mock(WriteResult.class);
        when(badResult.getN()).thenReturn(0);
        when(
                mockedMongoTemplate.updateFirst(Mockito.any(Query.class), Mockito.any(Update.class),
                        Mockito.eq("student"))).thenReturn(badResult);
        assertFalse(repository.update(student2));
        when(
                mockedMongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class),
                        Mockito.eq("student"))).thenReturn(null);
        assertFalse(repository.deleteByLocalId("student", student2.getLocalId().toString()));

        // test deleteAll by neutral record type
        repository.deleteAll("teacher");
        when(mockedMongoTemplate.find(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student")))
                .thenReturn(new LinkedList<NeutralRecord>());
        records = repository.findAll("student", 0, 20);
        assertFalse(records.iterator().hasNext());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSort() {

        // clean up the existing student data
        repository.deleteAll("student");

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
        repository.create(body1);
        repository.create(body2);
        repository.create(body3);
        repository.create(body4);

        // sort records by firstName with ascending order
        Query query = new Query();
        query.sort().on("body.firstName", Order.ASCENDING);
        List<NeutralRecord> expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body1);
        expectedRecords.add(body2);
        expectedRecords.add(body3);
        expectedRecords.add(body4);
        when(mockedMongoTemplate.find(query, NeutralRecord.class, "student")).thenReturn(expectedRecords);
        Iterable<NeutralRecord> records = repository.findByQuery("student", query, 0, 100);
        assertNotNull(records);
        Iterator<NeutralRecord> it = records.iterator();
        assertEquals("Austin", it.next().getAttributes().get("firstName"));
        assertEquals("Jane", it.next().getAttributes().get("firstName"));
        assertEquals("Mary", it.next().getAttributes().get("firstName"));
        assertEquals("Suzy", it.next().getAttributes().get("firstName"));

        // sort records by firstName with descending order
        query = new Query();
        query.sort().on("body.firstName", Order.DESCENDING);
        expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body4);
        expectedRecords.add(body3);
        expectedRecords.add(body2);
        expectedRecords.add(body1);
        when(mockedMongoTemplate.find(query, NeutralRecord.class, "student")).thenReturn(expectedRecords);
        records = repository.findByQuery("student", query, 0, 100);
        assertNotNull(records);
        it = records.iterator();
        assertEquals("Suzy", it.next().getAttributes().get("firstName"));
        assertEquals("Mary", it.next().getAttributes().get("firstName"));
        assertEquals("Jane", it.next().getAttributes().get("firstName"));
        assertEquals("Austin", it.next().getAttributes().get("firstName"));

        // sort records by performanceLevels which is an array with ascending order
        query = new Query();
        query.sort().on("body.performanceLevels", Order.ASCENDING);
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
        when(mockedMongoTemplate.find(query, NeutralRecord.class, "student")).thenReturn(expectedRecords);
        records = repository.findByQuery("student", query, 0, 100);
        assertNotNull(records);
        it = records.iterator();
        assertEquals("1", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));
        assertEquals("4", ((List<String>) (it.next().getAttributes().get("performanceLevels"))).get(0));

        // sort records by performanceLevels which is an array with descending order
        query = new Query();
        query.sort().on("body.performanceLevels", Order.DESCENDING);
        expectedRecords = new LinkedList<NeutralRecord>();
        expectedRecords.add(body4);
        expectedRecords.add(body3);
        expectedRecords.add(body2);
        expectedRecords.add(body1);
        when(mockedMongoTemplate.find(query, NeutralRecord.class, "student")).thenReturn(expectedRecords);
        records = repository.findByQuery("student", query, 0, 100);
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
        neutralRecord.setLocalId(body.get("studentUniqueStateId"));
        neutralRecord.setRecordType("student");
        neutralRecord.setAttributes(body);
        return neutralRecord;
    }

    @Test
    public void testFindIdsByQuery() {
        repository.deleteAll("student");

        // create new student neutral record
        repository.create(buildTestStudentNeutralRecord());
        repository.create(buildTestStudentNeutralRecord());
        repository.create(buildTestStudentNeutralRecord());
        repository.create(buildTestStudentNeutralRecord());
        repository.create(buildTestStudentNeutralRecord());

        List<NeutralRecord> expectedRecords = new LinkedList<NeutralRecord>();
        for (int rec = 1; rec <= 5; rec++) {
            expectedRecords.add(buildTestStudentNeutralRecord());
        }
        when(mockedMongoTemplate.find(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student")))
                .thenReturn(expectedRecords);
        Iterable<String> ids = repository.findIdsByQuery("student", null, 0, 100);
        List<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
        }

        assertEquals(5, idList.size());
    }

    @Test
    public void testFindByLocalId() {
        repository.deleteAll("student");

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
        repository.create(body1);
        repository.create(body2);
        repository.create(body3);
        repository.create(body4);

        // Search saved records by local ID.
        String id1 = body1.getLocalId().toString();
        when(mockedMongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student"))).thenReturn(body1);
        NeutralRecord found1 = repository.findByLocalId("student", id1);
        assertEquals("Austin", found1.getAttributes().get("firstName").toString());

        String id3 = body3.getLocalId().toString();
        when(mockedMongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(NeutralRecord.class), Mockito.eq("student"))).thenReturn(body3);
        NeutralRecord found3 = repository.findByLocalId("student", id3);
        assertEquals("Mary", found3.getAttributes().get("firstName").toString());
    }

}

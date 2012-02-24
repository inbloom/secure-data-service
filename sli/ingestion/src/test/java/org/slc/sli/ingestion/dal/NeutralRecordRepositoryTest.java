package org.slc.sli.ingestion.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;

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

    @Test
    public void testCRUDNeutralRecordRepository() {

        // clean up the existing student data
        repository.deleteAll("student");

        // create new student neutral record
        NeutralRecord student = buildTestStudentNeutralRecord();

        // test save
        NeutralRecord saved = repository.create("student", student);
        String id = saved.getLocalId().toString();
        assertTrue(!id.equals(""));

        // test findAll
        Iterable<NeutralRecord> records = repository.findAll("student", 0, 20);
        assertNotNull(records);
        NeutralRecord found = records.iterator().next();
        assertEquals(found.getAttributes().get("birthDate"), student.getAttributes().get("birthDate"));
        assertEquals((found.getAttributes()).get("firstName"), "Jane");
        assertEquals((found.getAttributes()).get("lastName"), "Doe");

        // test find by id
        NeutralRecord foundOne = repository.find("student", saved.getLocalId().toString());
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
        assertTrue(repository.update("student", found));
        records = repository.findAll("student", 0, 20);
        assertNotNull(records);
        NeutralRecord updated = records.iterator().next();
        assertEquals(updated.getAttributes().get("firstName"), "Mandy");

        // test delete by id
        NeutralRecord student2Body = buildTestStudentNeutralRecord();
        NeutralRecord student2 = repository.create("student", student2Body);
        records = repository.findAll("student", 0, 20);
        assertNotNull(records.iterator().next());
        repository.delete("student", student2.getLocalId().toString());
        NeutralRecord zombieStudent = repository.find("student", student2.getLocalId().toString());
        assertNull(zombieStudent);
        assertFalse(repository.update("student", student2));
        assertFalse(repository.delete("student", student2.getLocalId().toString()));

        // test deleteAll by neutral record type
        repository.deleteAll("student");
        records = repository.findAll("student", 0, 20);
        assertFalse(records.iterator().hasNext());
    }

    @Test
    public void testSort() {

        // clean up the existing student data
        repository.deleteAll("student");

        // create new student neutral record
        NeutralRecord body1 = buildTestStudentNeutralRecord();
        NeutralRecord body2 = buildTestStudentNeutralRecord();
        NeutralRecord body3 = buildTestStudentNeutralRecord();
        NeutralRecord body4 = buildTestStudentNeutralRecord();

        body1.setLocalId("1000001");
        body2.setLocalId("1000002");
        body3.setLocalId("1000003");
        body4.setLocalId("1000004");

        body1.setAttributeField("studentUniqueStateId", "1000001");
        body2.setAttributeField("studentUniqueStateId", "1000002");
        body3.setAttributeField("studentUniqueStateId", "1000003");
        body4.setAttributeField("studentUniqueStateId", "1000004");

        body1.setAttributeField("firstName", "Austin");
        body2.setAttributeField("firstName", "Jane");
        body3.setAttributeField("firstName", "Mary");
        body4.setAttributeField("firstName", "Suzy");

        body1.setAttributeField("performanceLevels", new String[] { "1" });
        body2.setAttributeField("performanceLevels", new String[] { "2" });
        body3.setAttributeField("performanceLevels", new String[] { "3" });
        body4.setAttributeField("performanceLevels", new String[] { "4" });

        // save records
        repository.create("student", body1);
        repository.create("student", body2);
        repository.create("student", body3);
        repository.create("student", body4);

        // sort records by firstName with ascending order
        Query query = new Query();
        query.sort().on("body.firstName", Order.ASCENDING);
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
        body.put("studentUniqueStateId", "1000000");
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
        neutralRecord.setLocalId("1000000");
        neutralRecord.setRecordType("student");
        neutralRecord.setAttributes(body);
        return neutralRecord;
    }

    @Test
    public void testFindIdsByQuery() {
        repository.deleteAll("student");
        repository.create("student", buildTestStudentNeutralRecord());
        repository.create("student", buildTestStudentNeutralRecord());
        repository.create("student", buildTestStudentNeutralRecord());
        repository.create("student", buildTestStudentNeutralRecord());
        repository.create("student", buildTestStudentNeutralRecord());

        Iterable<String> ids = repository.findIdsByQuery("student", null, 0, 100);
        List<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
        }

        assertEquals(5, idList.size());
    }
}

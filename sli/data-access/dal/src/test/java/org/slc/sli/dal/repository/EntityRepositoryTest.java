package org.slc.sli.dal.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityRepositoryTest {
    
    @Autowired
    private EntityRepository repository;
    
    @Test
    public void testCRUDEntityRepository() {
        
        // clean up the existing student data
        repository.deleteAll("student");
        
        // create new student entity
        Map<String, Object> student = buildTestStudentEntity();
        
        // test save
        Entity saved = repository.create("student", student);
        String id = saved.getEntityId();
        assertTrue(!id.equals(""));
        
        // test findAll
        Iterable<Entity> entities = repository.findAll("student", 0, 20);
        assertNotNull(entities);
        Entity found = entities.iterator().next();
        assertEquals(found.getBody().get("birthDate"), student.get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");
        assertEquals((found.getBody()).get("lastName"), "Doe");
        
        // test find by id
        Entity foundOne = repository.find("student", saved.getEntityId());
        assertNotNull(foundOne);
        assertEquals(foundOne.getBody().get("birthDate"), student.get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");
        
        // test find by field
        Map<String, String> searchFields = new HashMap<String, String>();
        searchFields.put("firstName", "Jane");
        Iterable<Entity> searchResults = repository.findByFields("student", searchFields, 0, 20);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getBody().get("firstName"), "Jane");
        searchResults = repository.findByFields("student", searchFields);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getBody().get("firstName"), "Jane");
        
        // test find by query
        Query query = new Query();
        query.addCriteria(Criteria.where("body.firstName").is("Jane"));
        searchResults = repository.findByQuery("student", query, 0, 20);
        assertNotNull(searchResults);
        assertEquals(searchResults.iterator().next().getBody().get("firstName"), "Jane");
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("body.birthDate").lt("2011-10-01"));
        searchResults = repository.findByQuery("student", query1, 0, 20);
        assertTrue(searchResults.iterator().hasNext());
        query = null;
        searchResults = repository.findByQuery("student", query, 0, 20);
        assertTrue(searchResults.iterator().hasNext());
        
        // test match by query object
        Query query2 = new Query(Criteria.where("body.firstName").is("Jane"));
        assertTrue(repository.matchQuery("student", id, query2));
        query2 = null;
        assertTrue(!repository.matchQuery("student", id, query2));
        
        // test update
        found.getBody().put("firstName", "Mandy");
        assertTrue(repository.update("student", found));
        entities = repository.findAll("student", 0, 20);
        assertNotNull(entities);
        Entity updated = entities.iterator().next();
        assertEquals(updated.getBody().get("firstName"), "Mandy");
        
        // test delete by id
        Map<String, Object> student2Body = buildTestStudentEntity();
        Entity student2 = repository.create("student", student2Body);
        entities = repository.findAll("student", 0, 20);
        assertNotNull(entities.iterator().next());
        repository.delete("student", student2.getEntityId());
        Entity zombieStudent = repository.find("student", student2.getEntityId());
        assertNull(zombieStudent);
        assertFalse(repository.update("student", student2));
        assertFalse(repository.delete("student", student2.getEntityId()));
        
        // test deleteAll by entity type
        repository.deleteAll("student");
        entities = repository.findAll("student", 0, 20);
        assertFalse(entities.iterator().hasNext());
    }
    
    // @Test
    // public void testValidation() {
    // Map<String, Object> badBody = buildTestStudentEntity();
    // badBody.put("bad-entity", "true");
    // try {
    // repository.create("student", badBody);
    // fail("Should have thrown a validation exception");
    // } catch (EntityValidationException e) {
    // //received correct exception
    // assertEquals("student", e.getEntityType());
    // }
    // Entity saved = repository.create("student", buildTestStudentEntity());
    // String id = saved.getEntityId();
    // saved.getBody().put("bad-entity", "true");
    // try {
    // repository.update("student", saved);
    // fail("Should have thrown a validation exception");
    // } catch (EntityValidationException e) {
    // //received correct exception
    // assertEquals("student", e.getEntityType());
    // }
    // Map<String, String> badFields = new HashMap<String, String>();
    // badFields.put("bad-entity", "true");
    // Iterable<Entity> badEntities = repository.findByFields("student", badFields, 0, 100);
    // assertTrue(!badEntities.iterator().hasNext());
    // repository.delete("student", id);
    // }
    
    private Map<String, Object> buildTestStudentEntity() {
        
        Map<String, Object> body = new HashMap<String, Object>();
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
        return body;
    }
    
    @Test
    public void testTimestamps() {
        
        // clean up the existing student data
        repository.deleteAll("student");
        
        // create new student entity
        Map<String, Object> student = buildTestStudentEntity();
        
        // test save
        Entity saved = repository.create("student", student);
        
        DateTime created = new DateTime(saved.getMetaData().get(EntityMetadataKey.CREATED.toString()));
        DateTime updated = new DateTime(saved.getMetaData().get(EntityMetadataKey.UPDATED.toString()));
        
        assertEquals(created, updated);
        
        saved.getBody().put("cityOfBirth", "Evanston");
        
        repository.update("student", saved);
        
        updated = new DateTime(saved.getMetaData().get(EntityMetadataKey.UPDATED.toString()));
        
        assertTrue( updated.isAfter( created ) );
        
    }
}

package org.slc.sli.dal.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        Entity student = buildTestStudentEntity();

        // test save
        Entity saved = repository.create(student);
        String id = saved.getId();
        assertTrue(!id.equals(""));

        // test findAll
        Iterable<Entity> entities = repository.findAll("student", 0, 20);
        assertNotNull(entities);
        Entity found = entities.iterator().next();
        assertEquals(found.getBody().get("birthDate"), student.getBody().get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");
        assertEquals((found.getBody()).get("lastName"), "Doe");

        // test find by id
        Entity foundOne = repository.find("student", saved.getId());
        assertNotNull(foundOne);
        assertEquals(foundOne.getBody().get("birthDate"), student.getBody().get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");
        
        // test update
        found.getBody().put("firstName", "Mandy");
        repository.update(found);
        entities = repository.findAll("student", 0, 20);
        assertNotNull(entities);
        Entity updated = entities.iterator().next();
        assertEquals(updated.getBody().get("firstName"), "Mandy");

        // test delete by id
        Entity student2 = buildTestStudentEntity();
        student2 = repository.create(student2);
        entities = repository.findAll("student", 0, 20);
        student = entities.iterator().next();
        assertNotNull(entities.iterator().next());
        repository.delete("student", student2.getId());
        student2 = repository.find("student", student2.getId());
        assertNull(student2);

        // test deleteAll by entity type
        repository.deleteAll("student");
        entities = repository.findAll("student", 0, 20);
        assertFalse(entities.iterator().hasNext());

    }

    private Entity buildTestStudentEntity() {

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("firstName", "Jane");
        body.put("lastName", "Doe");
        Date birthDate = new Timestamp(23234000);
        body.put("birthDate", birthDate);
        body.put("cityOfBirth", "Chicago");
        body.put("CountyOfBirth", "US");
        body.put("dateEnteredUs", birthDate);
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
        Entity student = new MongoEntity("student", null, body, null);
        return student;
    }

}

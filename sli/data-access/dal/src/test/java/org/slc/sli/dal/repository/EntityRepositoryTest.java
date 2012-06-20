package org.slc.sli.dal.repository;

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

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityRepositoryTest {

    @Resource(name = "mongoEntityRepository")
    private Repository<Entity> repository;

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
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(20);
        Iterable<Entity> entities = repository.findAll("student", neutralQuery);
        assertNotNull(entities);
        Entity found = entities.iterator().next();
        assertEquals(found.getBody().get("birthDate"), student.get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");
        assertEquals((found.getBody()).get("lastName"), "Doe");

        // test find by id
        Entity foundOne = repository.findById("student", saved.getEntityId());
        assertNotNull(foundOne);
        assertEquals(foundOne.getBody().get("birthDate"), student.get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");

        // test update
        found.getBody().put("firstName", "Mandy");
        assertTrue(repository.update("student", found));
        entities = repository.findAll("student", neutralQuery);
        assertNotNull(entities);
        Entity updated = entities.iterator().next();
        assertEquals(updated.getBody().get("firstName"), "Mandy");

        // test delete by id
        Map<String, Object> student2Body = buildTestStudentEntity();
        Entity student2 = repository.create("student", student2Body);
        entities = repository.findAll("student", neutralQuery);
        assertNotNull(entities.iterator().next());
        repository.delete("student", student2.getEntityId());
        Entity zombieStudent = repository.findById("student", student2.getEntityId());
        assertNull(zombieStudent);

        assertFalse(repository.delete("student", student2.getEntityId()));

        // test deleteAll by entity type
        repository.deleteAll("student");
        entities = repository.findAll("student", neutralQuery);
        assertFalse(entities.iterator().hasNext());
    }

    @Test
    public void testNeedsId() {

        Entity e = new MongoEntity("student", buildTestStudentEntity());
        assertFalse(repository.update("student", e));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSort() {

        // clean up the existing student data
        repository.deleteAll("student");

        // create new student entity
        Map<String, Object> body1 = buildTestStudentEntity();
        Map<String, Object> body2 = buildTestStudentEntity();
        Map<String, Object> body3 = buildTestStudentEntity();
        Map<String, Object> body4 = buildTestStudentEntity();

        body1.put("firstName", "Austin");
        body2.put("firstName", "Jane");
        body3.put("firstName", "Mary");
        body4.put("firstName", "Suzy");

        body1.put("performanceLevels", new String[] { "1" });
        body2.put("performanceLevels", new String[] { "2" });
        body3.put("performanceLevels", new String[] { "3" });
        body4.put("performanceLevels", new String[] { "4" });

        // save entities
        repository.create("student", body1);
        repository.create("student", body2);
        repository.create("student", body3);
        repository.create("student", body4);

        // sort entities by firstName with ascending order
        NeutralQuery sortQuery1 = new NeutralQuery();
        sortQuery1.setSortBy("firstName");
        sortQuery1.setSortOrder(NeutralQuery.SortOrder.ascending);
        sortQuery1.setOffset(0);
        sortQuery1.setLimit(100);

        Iterable<Entity> entities = repository.findAll("student", sortQuery1);
        assertNotNull(entities);
        Iterator<Entity> it = entities.iterator();
        assertEquals("Austin", it.next().getBody().get("firstName"));
        assertEquals("Jane", it.next().getBody().get("firstName"));
        assertEquals("Mary", it.next().getBody().get("firstName"));
        assertEquals("Suzy", it.next().getBody().get("firstName"));

        // sort entities by firstName with descending order
        NeutralQuery sortQuery2 = new NeutralQuery();
        sortQuery2.setSortBy("firstName");
        sortQuery2.setSortOrder(NeutralQuery.SortOrder.descending);
        sortQuery2.setOffset(0);
        sortQuery2.setLimit(100);
        entities = repository.findAll("student", sortQuery2);
        assertNotNull(entities);
        it = entities.iterator();
        assertEquals("Suzy", it.next().getBody().get("firstName"));
        assertEquals("Mary", it.next().getBody().get("firstName"));
        assertEquals("Jane", it.next().getBody().get("firstName"));
        assertEquals("Austin", it.next().getBody().get("firstName"));

        // sort entities by performanceLevels which is an array with ascending order
        NeutralQuery sortQuery3 = new NeutralQuery();
        sortQuery3.setSortBy("performanceLevels");
        sortQuery3.setSortOrder(NeutralQuery.SortOrder.ascending);
        sortQuery3.setOffset(0);
        sortQuery3.setLimit(100);
        entities = repository.findAll("student", sortQuery3);
        assertNotNull(entities);
        it = entities.iterator();
        assertEquals("1", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("4", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));

        // sort entities by performanceLevels which is an array with descending order
        NeutralQuery sortQuery4 = new NeutralQuery();
        sortQuery4.setSortBy("performanceLevels");
        sortQuery4.setSortOrder(NeutralQuery.SortOrder.descending);
        sortQuery4.setOffset(0);
        sortQuery4.setLimit(100);
        entities = repository.findAll("student", sortQuery4);
        assertNotNull(entities);
        it = entities.iterator();
        assertEquals("4", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("1", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
    }

    @Test
    public void testCount() {
        TenantContext.setTenantId("SLIUnitTest");
        repository.deleteAll("student");
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        Map<String, Object> oddStudent = buildTestStudentEntity();
        oddStudent.put("cityOfBirth", "Nantucket");
        repository.create("student", oddStudent);
        assertEquals(5, repository.count("student", new NeutralQuery()));
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("cityOfBirth=Nantucket"));
        assertEquals(1, repository.count("student", neutralQuery));
    }

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
    public void testTimestamps() throws Exception {

        // clean up the existing student data
        repository.deleteAll("student");

        // create new student entity
        Map<String, Object> student = buildTestStudentEntity();

        // test save
        Entity saved = repository.create("student", student);

        DateTime created = new DateTime(saved.getMetaData().get(EntityMetadataKey.CREATED.getKey()));
        DateTime updated = new DateTime(saved.getMetaData().get(EntityMetadataKey.UPDATED.getKey()));

        assertEquals(created, updated);

        saved.getBody().put("cityOfBirth", "Evanston");

        // Needs to be here to prevent cases where code execution is so fast, there
        // is no difference between create/update times
        Thread.sleep(2);

        repository.update("student", saved);

        updated = new DateTime(saved.getMetaData().get(EntityMetadataKey.UPDATED.getKey()));

        assertTrue(updated.isAfter(created));

    }

    @Test
    public void testFindIdsByQuery() {
        repository.deleteAll("student");
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(100);

        Iterable<String> ids = repository.findAllIds("student", neutralQuery);
        List<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
        }

        assertEquals(5, idList.size());
    }

    @Test
    public void findOneTest() {
        repository.deleteAll("student");
        Map<String, Object> student = buildTestStudentEntity();
        student.put("firstName", "Jadwiga");

        this.repository.create("student", student);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=Jadwiga"));

        assertNotNull(this.repository.findOne("student", neutralQuery));
    }

    @Test
    public void findOneMultipleMatches() {
        repository.deleteAll("student");
        Map<String, Object> student = buildTestStudentEntity();
        student.put("firstName", "Jadwiga");

        this.repository.create("student", student);
        this.repository.create("student", student);
        this.repository.create("student", student);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=Jadwiga"));

        assertNotNull(this.repository.findOne("student", neutralQuery));
    }

    @Test
    public void findOneTestNegative() {
        repository.deleteAll("student");
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=Jadwiga"));

        assertNull(this.repository.findOne("student", neutralQuery));
    }

}

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


package org.slc.sli.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.service.AssociationService.EntityIdList;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


/**
 * Service layer tests for the API.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EntityServiceLayerTest {

    @Autowired
    private EntityDefinitionStore defs;
    private EntityDefinition studentDef;
    private EntityDefinition schoolDef;
    private AssociationDefinition studentEnrollmentDef;
    private EntityService studentService;
    private EntityService schoolService;
    private AssociationService studentSchoolAssociationService;
    @Autowired
    private Repository<Entity> repo;

    public void setSecurityContextInjector(SecurityContextInjector securityContextInjector) {
        this.securityContextInjector = securityContextInjector;
    }

    @Autowired
    private SecurityContextInjector securityContextInjector;

    @Before
    public void setUp() {
        // inject administrator security context for unit testing
        securityContextInjector.setStaffContext();

        repo.deleteAll("student", null);
        repo.deleteAll("school", null);
        repo.deleteAll("studentSchoolAssociations", null);
        studentDef = defs.lookupByResourceName("students");
        schoolDef = defs.lookupByResourceName("schools");
        studentEnrollmentDef = (AssociationDefinition) defs.lookupByResourceName("studentSchoolAssociations");
        studentService = studentDef.getService();
        schoolService = schoolDef.getService();
        studentSchoolAssociationService = studentEnrollmentDef.getService();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCrudEntity() {
        EntityBody student = new EntityBody();
        student.put("firstName", "Andrew");
        student.put("lastName", "Wiggen");
        String id = studentService.create(student);
        EntityBody retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        student = new EntityBody(student);
        student.put("sex", "Male");
        student.put("otherName", "Ender");
        assertTrue(studentService.update(id, student));
        retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        assertEquals(student.get("sex"), retrievedEntity.get("sex"));
        assertEquals(student.get("otherName"), retrievedEntity.get("otherName"));
        assertFalse(studentService.update(id, student));
        retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        assertEquals(student.get("sex"), retrievedEntity.get("sex"));
        student = new EntityBody(student);
        student.remove("otherName");
        assertTrue(studentService.update(id, student));
        retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        assertEquals(student.get("sex"), retrievedEntity.get("sex"));
        assertEquals(null, retrievedEntity.get("otherName"));
        try {
            studentService.delete(id);
        } catch (EntityNotFoundException e) {
            fail();
        }
        try {
            EntityBody zombie = studentService.get(id);
            fail("should have not found " + zombie);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
        try {
            studentService.delete(id);
            fail("Exception should have been thrown");
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testNoSuchEntity() {
        try {
            studentService.get("NoSuchStudent");
            fail("should have thrown exception");
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
        try {
            EntityBody body = new EntityBody();
            body.put("studentUniqueStateId", "student123");
            studentService.update("NoSuchStudent", body);
            fail("should have thrown exception");
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testMultipleEntities() {
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        EntityBody student2 = new EntityBody();
        student2.put("firstName", "Petra");
        student2.put("lastName", "Arkanian");
        EntityBody student3 = new EntityBody();
        student3.put("firstName", "Andrew");
        student3.put("lastName", "Wiggen");
        EntityBody student4 = new EntityBody();
        student4.put("firstName", "Julian");
        student4.put("lastName", "Delphiki");
        String id1 = studentService.create(student1);
        String id2 = studentService.create(student2);
        String id3 = studentService.create(student3);
        String id4 = studentService.create(student4);
        EntityBody retrievedStudent1 = studentService.get(id1);
        assertEquals(student1.get("firstName"), retrievedStudent1.get("firstName"));
        assertEquals(student1.get("lastName"), retrievedStudent1.get("lastName"));
        EntityBody retrievedStudent2 = studentService.get(id2);
        assertEquals(student2.get("firstName"), retrievedStudent2.get("firstName"));
        assertEquals(student2.get("lastName"), retrievedStudent2.get("lastName"));
        EntityBody retrievedStudent3 = studentService.get(id3);
        assertEquals(student3.get("firstName"), retrievedStudent3.get("firstName"));
        assertEquals(student3.get("lastName"), retrievedStudent3.get("lastName"));
        EntityBody retrievedStudent4 = studentService.get(id4);
        assertEquals(student4.get("firstName"), retrievedStudent4.get("firstName"));
        assertEquals(student4.get("lastName"), retrievedStudent4.get("lastName"));
        assertEquals(Arrays.asList(retrievedStudent1, retrievedStudent2, retrievedStudent3, retrievedStudent4),
                studentService.get(Arrays.asList(id1, id2, id3, id4)));
        NeutralQuery zeroToTwoQuery = new NeutralQuery();
        zeroToTwoQuery.setOffset(0);
        zeroToTwoQuery.setLimit(2);
        List<String> firstSet = iterableToList(studentService.listIds(zeroToTwoQuery));
        assertEquals(2, firstSet.size());
        NeutralQuery twoToFourQuery = new NeutralQuery();
        twoToFourQuery.setOffset(2);
        twoToFourQuery.setLimit(2);
        List<String> secondSet = iterableToList(studentService.listIds(twoToFourQuery));
        assertEquals(2, secondSet.size());
        Set<String> wholeSet = new HashSet<String>();
        wholeSet.addAll(firstSet);
        wholeSet.addAll(secondSet);
        assertEquals(new HashSet<String>(Arrays.asList(id1, id2, id3, id4)), wholeSet);
        studentService.delete(id1);
        studentService.delete(id2);
        studentService.delete(id3);
        studentService.delete(id4);
        NeutralQuery zeroToFourQuery = new NeutralQuery();
        zeroToFourQuery.setOffset(0);
        zeroToFourQuery.setLimit(4);
        assertEquals(new ArrayList<EntityBody>(), studentService.list(zeroToFourQuery));
    }

    @Test
    public void testLinkedResources() {
        assertTrue(defs.getLinked(studentDef).contains(studentEnrollmentDef));
    }

    @Test
    public void testAssociations() {
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("name.firstName", "Bonzo");
        student1.put("name", createMap("middleName", "1"));
        student1.put("lastName", "Madrid");
        student1.put("studentUniqueStateId", "0");
        EntityBody student2 = new EntityBody();
        student2.put("firstName", "Petra");
        student2.put("lastName", "Arkanian");
        student2.put("name", createMap("middleName", "2"));
        student2.put("studentUniqueStateId", "1");
        EntityBody student3 = new EntityBody();
        student3.put("firstName", "Andrew");
        student3.put("lastName", "Wiggen");
        student3.put("name", createMap("middleName", "3"));
        student3.put("studentUniqueStateId", "3");
        EntityBody student4 = new EntityBody();
        student4.put("firstName", "Julian");
        student4.put("lastName", "Delphiki");
        student4.put("name", createMap("middleName", "4"));
        student4.put("studentUniqueStateId", "4");
        String id1 = studentService.create(student1);
        String id2 = studentService.create(student2);
        String id3 = studentService.create(student3);
        String id4 = studentService.create(student4);
        final EntityBody school = new EntityBody();
        school.put("name", "Battle School");
        school.put("nameOfInstitution", "Battle School");
        String schoolId = schoolService.create(school);
        EntityBody assoc1 = new EntityBody();
        assoc1.put("schoolId", schoolId);
        assoc1.put("studentId", id1);
        assoc1.put("startDate", (new Date()).getTime());
        assoc1.put("entryGradeLevel", "First grade");
        String assocId1 = studentSchoolAssociationService.create(assoc1);
        EntityBody retrievedAssoc1 = studentSchoolAssociationService.get(assocId1);
        assertEquals(retrievedAssoc1.get("schoolId"), assoc1.get("schoolId"));
        assertEquals(retrievedAssoc1.get("studentId"), assoc1.get("studentId"));
        assertEquals(retrievedAssoc1.get("startDate"), assoc1.get("startDate"));
        EntityBody assoc2 = new EntityBody();
        assoc2.put("schoolId", schoolId);
        assoc2.put("studentId", id2);
        assoc2.put("startDate", (new Date()).getTime());
        assoc2.put("entryGradeLevel", "Second grade");
        String assocId2 = studentSchoolAssociationService.create(assoc2);
        EntityBody retrievedAssoc2 = studentSchoolAssociationService.get(assocId2);
        assertEquals(retrievedAssoc2.get("schoolId"), assoc2.get("schoolId"));
        assertEquals(retrievedAssoc2.get("studentId"), assoc2.get("studentId"));
        assertEquals(retrievedAssoc2.get("startDate"), assoc2.get("startDate"));
        EntityBody assoc3 = new EntityBody();
        assoc3.put("schoolId", schoolId);
        assoc3.put("studentId", id3);
        assoc3.put("startDate", (new Date()).getTime());
        assoc3.put("entryGradeLevel", "Third grade");
        String assocId3 = studentSchoolAssociationService.create(assoc3);
        EntityBody retrievedAssoc3 = studentSchoolAssociationService.get(assocId3);
        assertEquals(retrievedAssoc3.get("schoolId"), assoc3.get("schoolId"));
        assertEquals(retrievedAssoc3.get("studentId"), assoc3.get("studentId"));
        assertEquals(retrievedAssoc3.get("startDate"), assoc3.get("startDate"));
        EntityBody assoc4 = new EntityBody();
        assoc4.put("schoolId", schoolId);
        assoc4.put("studentId", id4);
        assoc4.put("startDate", (new Date()).getTime());
        assoc4.put("entryGradeLevel", "Fourth grade");
        String assocId4 = studentSchoolAssociationService.create(assoc4);
        EntityBody retrievedAssoc4 = studentSchoolAssociationService.get(assocId4);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(4);
        NeutralQuery neutralQueryA = new NeutralQuery(neutralQuery);
        NeutralQuery neutralQueryB = new NeutralQuery(neutralQuery);
        NeutralQuery neutralQueryC = new NeutralQuery(neutralQuery);
        NeutralQuery neutralQueryD = new NeutralQuery(neutralQuery);
        assertEquals(retrievedAssoc4.get("schoolId"), assoc4.get("schoolId"));
        assertEquals(retrievedAssoc4.get("studentId"), assoc4.get("studentId"));
        assertEquals(retrievedAssoc4.get("startDate"), assoc4.get("startDate"));
        assertEquals(Arrays.asList(retrievedAssoc1, retrievedAssoc2, retrievedAssoc3, retrievedAssoc4),
                studentSchoolAssociationService.get(Arrays.asList(assocId1, assocId2, assocId3, assocId4)));
        assertEquals(Arrays.asList(assocId1), studentSchoolAssociationService.getAssociationsWith(id1, neutralQueryA));
        assertEquals(Arrays.asList(assocId2), studentSchoolAssociationService.getAssociationsWith(id2, neutralQueryB));
        assertEquals(Arrays.asList(assocId3), studentSchoolAssociationService.getAssociationsWith(id3, neutralQueryC));
        assertEquals(Arrays.asList(assocId4), studentSchoolAssociationService.getAssociationsWith(id4, neutralQueryD));
        assertEquals(Arrays.asList(assocId1, assocId2, assocId3, assocId4),
                studentSchoolAssociationService.getAssociationsTo(schoolId, neutralQuery));

        // test query fields
        NeutralQuery neutralQuery1 = new NeutralQuery();
        neutralQuery1.setLimit(4);
        neutralQuery1.addCriteria(new NeutralCriteria("entryGradeLevel", "=", "First grade"));
        assertEquals(Arrays.asList(assocId1), studentSchoolAssociationService.getAssociationsWith(id1, neutralQuery1));
        NeutralQuery neutralQuery2 = new NeutralQuery();
        neutralQuery2.setLimit(4);
        neutralQuery2.addCriteria(new NeutralCriteria("entryGradeLevel", "=", "Second grade"));
        assertFalse(studentSchoolAssociationService.getAssociationsWith(id1, neutralQuery2).iterator().hasNext());

        NeutralQuery neutralQuery3 = new NeutralQuery();
        neutralQuery3.setLimit(4);
        neutralQuery3.addCriteria(new NeutralCriteria("entryGradeLevel", "=", "First grade"));
        assertEquals(Arrays.asList(assocId1),
                studentSchoolAssociationService.getAssociationsTo(schoolId, neutralQuery3));
        NeutralQuery neutralQuery4 = new NeutralQuery();
        neutralQuery4.setLimit(4);
        neutralQuery4.addCriteria(new NeutralCriteria("entryGradeLevel", "=", "Fifth grade"));
        assertFalse(studentSchoolAssociationService.getAssociationsTo(schoolId, neutralQuery4).iterator().hasNext());

        NeutralQuery neutralQuery5 = new NeutralQuery();
        neutralQuery5.setLimit(4);
        neutralQuery5.addCriteria(new NeutralCriteria("nameOfInstitution", "=", "Battle School"));
        EntityIdList idList1 = studentSchoolAssociationService.getAssociatedEntitiesWith(id1, neutralQuery5);
        assertEquals(Arrays.asList(schoolId), iterableToList(idList1));
        assertEquals(1, idList1.getTotalCount());
        NeutralQuery neutralQuery6 = new NeutralQuery();
        neutralQuery6.setLimit(4);
        neutralQuery6.addCriteria(new NeutralCriteria("nameOfInstitution", "=", "new Battle School"));
        assertFalse(studentSchoolAssociationService.getAssociatedEntitiesWith(id1, neutralQuery6).iterator().hasNext());

        NeutralQuery neutralQuery7 = new NeutralQuery();
        neutralQuery7.setLimit(4);
        neutralQuery7.addCriteria(new NeutralCriteria("studentUniqueStateId", "=", "0"));
        EntityIdList idList2 = studentSchoolAssociationService.getAssociatedEntitiesTo(schoolId, neutralQuery7);
        assertEquals(Arrays.asList(id1), iterableToList(idList2));
        assertEquals(4, idList2.getTotalCount());
        NeutralQuery neutralQuery8 = new NeutralQuery();
        neutralQuery8.setLimit(4);
        neutralQuery8.addCriteria(new NeutralCriteria("name.firstName", "=", "non exist"));
        assertFalse(studentSchoolAssociationService.getAssociatedEntitiesTo(schoolId, neutralQuery8).iterator()
                .hasNext());

        // test sorting
        NeutralQuery neutralQuery9 = new NeutralQuery();
        neutralQuery9.setLimit(4);
        neutralQuery9.setSortBy("entryGradeLevel");
        neutralQuery9.setSortOrder(NeutralQuery.SortOrder.ascending);
        assertEquals(Arrays.asList(assocId1, assocId4, assocId2, assocId3),
                studentSchoolAssociationService.getAssociationsTo(schoolId, neutralQuery9));

        NeutralQuery neutralQuery10 = new NeutralQuery();
        neutralQuery10.setLimit(4);
        neutralQuery10.setSortBy("studentUniqueStateId");
        neutralQuery10.setSortOrder(NeutralQuery.SortOrder.descending);
        EntityIdList idList3 = studentSchoolAssociationService.getAssociatedEntitiesTo(schoolId, neutralQuery10);
        assertEquals(Arrays.asList(id4, id3, id2, id1), iterableToList(idList3));
        assertEquals(4, idList3.getTotalCount());

        studentService.delete(id1);
        studentService.delete(id2);
        studentService.delete(id3);
        studentService.delete(id4);
        schoolService.delete(schoolId);
    }

    @Test
    public void testCustomEntities() {
        CallingApplicationInfoProvider clientInfo = Mockito.mock(CallingApplicationInfoProvider.class);
        Mockito.when(clientInfo.getClientId()).thenReturn("TEST_CLIENT");
        ((BasicService) studentService).setClientInfo(clientInfo);

        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("name.firstName", "Bonzo");
        student1.put("name", createMap("middleName", "1"));
        student1.put("lastName", "Madrid");
        student1.put("studentUniqueStateId", "0");
        String studentId = studentService.create(student1);

        EntityBody customData = new EntityBody();
        customData.put("key1", "A");
        customData.put("key2", "B");
        studentService.createOrUpdateCustom(studentId, customData);

        EntityBody entity = studentService.getCustom(studentId);
        assertEquals(customData, entity);

        studentService.deleteCustom(studentId);

        entity = studentService.getCustom(studentId);
        assertEquals(null, entity);

        studentService.createOrUpdateCustom(studentId, customData);
        entity = studentService.getCustom(studentId);
        assertEquals(customData, entity);

        studentService.delete(studentId);

        try {
            entity = studentService.getCustom(studentId);
            fail("expected EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            assertEquals(studentId, e.getId());
        }
    }

    private <T> List<T> iterableToList(Iterable<T> itr) {
        List<T> result = new ArrayList<T>();
        for (T item : itr) {
            result.add(item);
        }
        return result;
    }

    @SuppressWarnings("unused")
    private Map<String, String> setupTestDeleteWithAssoc() {
        Map<String, String> ids = new HashMap<String, String>();
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        String studentId = studentService.create(student1);

        EntityBody student2 = new EntityBody();
        student1.put("firstName", "Jane");
        student1.put("lastName", "Doe");
        String student2Id = studentService.create(student2);

        EntityBody school = new EntityBody();
        school.put("name", "Battle School");
        String schoolId = schoolService.create(school);

        EntityBody assoc = new EntityBody();
        assoc.put("schoolId", schoolId);
        assoc.put("studentId", studentId);
        assoc.put("startDate", (new Date()).getTime());
        String assocId = studentSchoolAssociationService.create(assoc);

        EntityBody assoc2 = new EntityBody();
        assoc2.put("schoolId", schoolId);
        assoc2.put("studentId", student2Id);
        assoc2.put("startDate", (new Date()).getTime());
        String assoc2Id = studentSchoolAssociationService.create(assoc2);

        ids.put("studentId", studentId);
        ids.put("schoolId", schoolId);
        ids.put("assocId", assocId);

        ids.put("student2Id", student2Id);
        ids.put("assoc2Id", assoc2Id);

        return ids;
    }

    private Map<String, Object> createMap(String key, Object value) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        return map;
    }
}

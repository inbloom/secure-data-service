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

package org.slc.sli.api.selectors.doc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSelectorDocumentTest {

    @Autowired
    DefaultSelectorDocument defaultSelectorDocument;

    private ModelProvider provider;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private MockRepo repo;

    static final String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    private Entity student1;
    private Entity student2;
    private Entity section1;
    private Entity section2;
    private Entity section3;

    @Before
    public void setup() {
        provider = new ModelProvider(TEST_XMI_LOC);

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        student1 = repo.create("student", createStudentEntity("1234"));
        student2 = repo.create("student", createStudentEntity("5678"));

        Entity session1 = repo.create("session", createSessionEntity("Fall 2011"));
        Entity session2 = repo.create("session", createSessionEntity("Spring 2011"));
        Entity session3 = repo.create("session", createSessionEntity("Fall 2012"));

        section1 = repo.create("section", createSectionEntity("Math 1", session1.getEntityId()));
        section2 = repo.create("section", createSectionEntity("English 1", session2.getEntityId()));
        section3 = repo.create("section", createSectionEntity("English 2", session3.getEntityId()));

        repo.create("studentSectionAssociation", createStudentSectionAssociationEntity(student1.getEntityId(),
                section1.getEntityId()));

        repo.create("studentSectionAssociation", createStudentSectionAssociationEntity(student2.getEntityId(),
                section2.getEntityId()));

        repo.create("studentSectionAssociation", createStudentSectionAssociationEntity(student1.getEntityId(),
                section3.getEntityId()));
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testComplexSelectorQueryPlan() {

        List<String> ids = new ArrayList<String>();
        ids.add(student1.getEntityId());
        ids.add(student2.getEntityId());

        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));

        List<EntityBody> results = defaultSelectorDocument.aggregate(createQueryPlan("Student",
                getSelectorQueryPlan()), constraint);
        assertNotNull("Should not be null", results);
        assertEquals("Should match", 2, results.size());

        EntityBody student = results.get(0);
        String studentId = (String) student.get("id");
        assertTrue("Should be true", student.containsKey("studentSectionAssociations"));
        assertTrue("Should be true", student.containsKey("name"));
        assertEquals("Should match", 4, student.keySet().size());

        @SuppressWarnings("unchecked")
        List<EntityBody> studentSectionAssociationList = (List<EntityBody>) student.get("studentSectionAssociations");
        assertEquals("Should match", 2, studentSectionAssociationList.size());

        EntityBody studentSectionAssociation = studentSectionAssociationList.get(0);
        String sectionId = (String) studentSectionAssociation.get("sectionId");
        assertEquals("Should match", studentId, studentSectionAssociation.get("studentId"));
        assertTrue("Should be true", studentSectionAssociation.containsKey("sections"));
        assertTrue("Should be true", studentSectionAssociation.containsKey("sectionId"));
        assertEquals("Should match", 5, studentSectionAssociation.keySet().size());

        @SuppressWarnings("unchecked")
        List<EntityBody> sectionList = (List<EntityBody>) studentSectionAssociation.get("sections");
        assertEquals("Should match", 1, sectionList.size());

        EntityBody section = sectionList.get(0);
        assertEquals("Should match", sectionId, section.get("id"));
        assertTrue("Should be true", section.containsKey("sectionName"));
        assertEquals("Should match", 3, section.keySet().size());
    }

    @Test
    public void testDirectReferenceQueryPlan() {
        List<String> ids = new ArrayList<String>();
        ids.add(section1.getEntityId());
        ids.add(section2.getEntityId());

        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));

        List<EntityBody> results = defaultSelectorDocument.aggregate(createQueryPlan("Section",
                getDirectRefQueryPlan()), constraint);

        assertNotNull("Should not be null", results);
        assertEquals("Should match", 2, results.size());

        EntityBody section = results.get(0);
        assertTrue("Should be true", section.containsKey("sessions"));

        @SuppressWarnings("unchecked")
        List<EntityBody> sessionList = (List<EntityBody>) section.get("sessions");
        assertEquals("Should match", 1, sessionList.size());

        EntityBody session = sessionList.get(0);
        assertTrue("Should be true", session.containsKey("sessionName"));
    }

    @Test
    public void testAssociationSkipQueryPlan() {
        defaultSelectorDocument.setModelProvider(provider);

        List<String> ids = new ArrayList<String>();
        ids.add(student1.getEntityId());
        ids.add(student2.getEntityId());

        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));

        List<EntityBody> results = defaultSelectorDocument.aggregate(createQueryPlan("Student",
                getAssociationSkipPlan()), constraint);

        assertNotNull("Should not be null", results);
        assertEquals("Should match", 2, results.size());

        EntityBody student = results.get(0);
        assertTrue("Should be true", student.containsKey("sections"));

        @SuppressWarnings("unchecked")
        List<EntityBody> sectionsList = (List<EntityBody>) student.get("sections");
        assertEquals("Should match", 2, sectionsList.size());
    }

    @Test
    public void testFilterFields() {
        SelectorQueryPlan plan = new SelectorQueryPlan();
        List<EntityBody> results = createResults();

        plan.getIncludeFields().add("field1");
        List<EntityBody> out = defaultSelectorDocument.filterFields(results, plan);

        assertEquals("Should match", 1, out.size());

        EntityBody body = out.get(0);
        assertTrue("Should be true", body.containsKey("field1"));
        assertFalse("Should be false", body.containsKey("field2"));
        assertFalse("Should be false", body.containsKey("field3"));

        plan.getIncludeFields().clear();
        plan.getIncludeFields().add("field1");
        plan.getIncludeFields().add("field2");
        out = defaultSelectorDocument.filterFields(results, plan);

        assertEquals("Should match", 1, out.size());

        body = out.get(0);
        assertTrue("Should be true", body.containsKey("field1"));
        assertTrue("Should be true", body.containsKey("field2"));
        assertFalse("Should be false", body.containsKey("field3"));

        plan.getIncludeFields().clear();
        plan.getExcludeFields().add("field1");
        out = defaultSelectorDocument.filterFields(results, plan);

        assertEquals("Should match", 1, out.size());

        body = out.get(0);
        assertFalse("Should be false", body.containsKey("field1"));
        assertFalse("Should be false", body.containsKey("field2"));
        assertFalse("Should be false", body.containsKey("field3"));

        plan.getIncludeFields().clear();
        plan.getExcludeFields().clear();
        plan.getExcludeFields().add("field1");
        plan.getIncludeFields().add("field1");
        out = defaultSelectorDocument.filterFields(results, plan);

        assertEquals("Should match", 1, out.size());

        body = out.get(0);
        assertFalse("Should be false", body.containsKey("field1"));
        assertFalse("Should be false", body.containsKey("field2"));
        assertFalse("Should be false", body.containsKey("field3"));

        plan.getIncludeFields().clear();
        plan.getExcludeFields().clear();
        out = defaultSelectorDocument.filterFields(results, plan);

        assertEquals("Should match", 1, out.size());

        body = out.get(0);
        assertFalse("Should be false", body.containsKey("field1"));
        assertFalse("Should be false", body.containsKey("field2"));
        assertFalse("Should be false", body.containsKey("field3"));
    }

    @Test
    public void testIncludeXSDSelector() {
        List<String> ids = new ArrayList<String>();
        ids.add(student1.getEntityId());

        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));

        List<EntityBody> results = defaultSelectorDocument.aggregate(createQueryPlan("Student",
                getIncludeXSDPlan()), constraint);

        assertNotNull("Should not be null", results);
        assertEquals("Should match", 1, results.size());

        EntityBody body = results.get(0);
        assertEquals("Should match", 3, body.keySet().size());
        assertTrue("Should be true", body.containsKey("name"));
    }

    @Test
    public void testEmptySelector() {
        List<String> ids = new ArrayList<String>();
        ids.add(student1.getEntityId());

        NeutralQuery constraint = new NeutralQuery();
        constraint.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));

        List<EntityBody> results = defaultSelectorDocument.aggregate(createQueryPlan("Student",
                getEmptyPlan()), constraint);

        assertNotNull("Should not be null", results);
        assertNotNull("Should not be null", results);
        assertEquals("Should match", 1, results.size());

        EntityBody body = results.get(0);
        assertEquals("Should match", 2, body.keySet().size());
    }

    @Test
    public void testGetEmbeddedEntities() {
        ClassType classType = mock(ClassType.class);
        when(classType.getName()).thenReturn("StudentSectionAssociation");
        List<EntityBody> embeddedEntities = defaultSelectorDocument.getEmbeddedEntities(createEmbeddedEntity(), classType);
        assertEquals(embeddedEntities.size(), 1);
    }

    @Test
    public void testAddChildTypesToQuery()  {
        ClassType parentType = mock(ClassType.class);
        when(parentType.getName()).thenReturn("Section");
        ClassType childType = mock(ClassType.class);
        when(childType.getName()).thenReturn("StudentSectionAssociation");
        ClassType nonChildType = mock(ClassType.class);
        when(nonChildType.getName()).thenReturn("StudentParentAssociation");

        SelectorQuery childQuery = new SelectorQuery();
        childQuery.put(childType, new SelectorQueryPlan());
        List<Object> childQueryPlans = new ArrayList<Object>();
        childQueryPlans.add(childQuery);

        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.setChildQueryPlans(childQueryPlans);

        NeutralQuery query = new NeutralQuery();
        defaultSelectorDocument.addChildTypesToQuery(parentType, plan, query);
        assertEquals("Should match", 1, query.getEmbeddedFields().size());
        assertEquals("Should match", "studentSectionAssociation", query.getEmbeddedFields().get(0));

        query = new NeutralQuery();
        defaultSelectorDocument.addChildTypesToQuery(parentType, new SelectorQueryPlan(), query);
        assertEquals("Should match", 0, query.getEmbeddedFields().size());

        childQuery = new SelectorQuery();
        childQuery.put(nonChildType, new SelectorQueryPlan());
        childQueryPlans = new ArrayList<Object>();
        childQueryPlans.add(childQuery);

        plan = new SelectorQueryPlan();
        plan.setChildQueryPlans(childQueryPlans);

        query = new NeutralQuery();
        defaultSelectorDocument.addChildTypesToQuery(parentType, new SelectorQueryPlan(), query);
        assertEquals("Should match", 0, query.getEmbeddedFields().size());
    }

    private List<EntityBody> createResults() {
        List<EntityBody> results = new ArrayList<EntityBody>();
        EntityBody body = new EntityBody();

        body.put("field1", "value1");
        body.put("field2", "value2");
        body.put("field3", "value3");

        results.add(body);

        return results;
    }

    private SelectorQuery createQueryPlan(String type, SelectorQueryPlan queryPlan) {
        SelectorQuery result = new SelectorQuery();

        result.put(provider.getClassType(type), queryPlan);

        return result;
    }

    private SelectorQueryPlan getSelectorQueryPlan() {
        SelectorQueryPlan plan = new SelectorQueryPlan();
        NeutralQuery query = new NeutralQuery();

        List<Object> childQueries = getChildQueries();

        plan.setQuery(query);
        plan.getIncludeFields().add("name");
        plan.getChildQueryPlans().addAll(childQueries);

        return plan;
    }

    private List<Object> getChildQueries() {
        List<Object> list = new ArrayList<Object>();

        NeutralQuery query = new NeutralQuery();

        List<Object> childQueries = getLevel3ChildQueries();

        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.setQuery(query);
        plan.getIncludeFields().add("sectionId");
        plan.getChildQueryPlans().addAll(childQueries);

        SelectorQuery map = new SelectorQuery();
        map.put(provider.getClassType("StudentSectionAssociation"), plan);

        list.add(map);

        return list;
    }

    private List<Object> getLevel3ChildQueries() {
        List<Object> list = new ArrayList<Object>();

        NeutralQuery query = new NeutralQuery();

        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.setQuery(query);
        plan.getIncludeFields().add("sectionName");

        SelectorQuery map = new SelectorQuery();
        map.put(provider.getClassType("Section"), plan);

        list.add(map);

        return list;
    }

    private SelectorQueryPlan getDirectRefQueryPlan() {
        SelectorQueryPlan plan = new SelectorQueryPlan();
        NeutralQuery query = new NeutralQuery();

        List<Object> childQueries = getDirectRefChildQueries();

        plan.setQuery(query);
        plan.getChildQueryPlans().addAll(childQueries);

        return plan;
    }

    private List<Object> getDirectRefChildQueries() {
        List<Object> list = new ArrayList<Object>();

        NeutralQuery query = new NeutralQuery();

        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.setQuery(query);
        plan.getIncludeFields().add("sessionName");

        SelectorQuery map = new SelectorQuery();
        map.put(provider.getClassType("Session"), plan);

        list.add(map);

        return list;
    }

    private SelectorQueryPlan getAssociationSkipPlan() {
        SelectorQueryPlan plan = new SelectorQueryPlan();
        NeutralQuery query = new NeutralQuery();

        List<Object> childQueries = getAssociationSkipChildQuery();

        plan.setQuery(query);
        plan.getChildQueryPlans().addAll(childQueries);

        return plan;
    }

    private List<Object> getAssociationSkipChildQuery() {
        List<Object> list = new ArrayList<Object>();

        NeutralQuery query = new NeutralQuery();

        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.setQuery(query);

        SelectorQuery map = new SelectorQuery();
        map.put(provider.getClassType("Section"), plan);

        list.add(map);

        return list;
    }

    private SelectorQueryPlan getIncludeXSDPlan() {
        SelectorQueryPlan plan = new SelectorQueryPlan();
        NeutralQuery query = new NeutralQuery();

        plan.setQuery(query);
        plan.getIncludeFields().add("name");

        return plan;
    }

    private SelectorQueryPlan getEmptyPlan() {
        return new SelectorQueryPlan();
    }

    private EntityBody createStudentEntity(String studentUniqueStateId) {
        EntityBody entity = new EntityBody();
        entity.put("name", "somename");
        entity.put("studentUniqueStateId", studentUniqueStateId);

        return entity;
    }

    private EntityBody createSectionEntity(String sectionName, String sessionId) {
        EntityBody entity = new EntityBody();
        entity.put("field", 1);
        entity.put("sectionName", sectionName);
        entity.put("sessionId", sessionId);

        return entity;
    }

    private EntityBody createStudentSectionAssociationEntity(String studentId, String sectionId) {
        EntityBody entity = new EntityBody();
        entity.put("studentId", studentId);
        entity.put("sectionId", sectionId);

        return entity;
    }

    private EntityBody createSessionEntity(String sessionName) {
        EntityBody entity = new EntityBody();
        entity.put("field", 1);
        entity.put("sessionName", sessionName);

        return entity;
    }
    private List<EntityBody> createEmbeddedEntity() {
        List<EntityBody> entityBodyList = new ArrayList<EntityBody>();
        EntityBody entityBody = new EntityBody();
        EntityBody embeddedBody = new EntityBody();
        List<EntityBody> embeddedList = new ArrayList<EntityBody>();

        embeddedBody.put("type", "studentSectionAssociation");
        embeddedBody.put("id", "SSA_id");
        embeddedList.add(embeddedBody);
        entityBody.put("id", "Entity_id");
        entityBody.put("studentSectionAssociation", embeddedList);

        entityBodyList.add(entityBody);
        return entityBodyList;
    }

}

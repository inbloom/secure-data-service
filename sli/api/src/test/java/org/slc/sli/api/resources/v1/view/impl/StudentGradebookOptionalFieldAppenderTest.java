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


package org.slc.sli.api.resources.v1.view.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentGradebookOptionalFieldAppenderTest {

    @Autowired
    private OptionalFieldAppender studentGradebookOptionalFieldAppender;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private static final String STUDENT_ID = "1234";
    private static final String SECTION_ID = "5555";
    private String gradebookEntryId1;
    private String gradebookEntryId2;

    @Before
    public void setup() {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        repo.create("student", createTestStudentEntityWithSectionAssociation(STUDENT_ID, SECTION_ID));

        gradebookEntryId1 = repo.create("gradebookEntry", createGradebookEntry()).getEntityId();
        gradebookEntryId1 = repo.create("gradebookEntry", createGradebookEntry()).getEntityId();

        repo.create("studentGradebookEntry", createStudentGradebookEntry(STUDENT_ID, SECTION_ID, gradebookEntryId1));
        repo.create("studentGradebookEntry", createStudentGradebookEntry(STUDENT_ID, SECTION_ID, gradebookEntryId1));
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testApplyOptionalField() {
        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(new EntityBody(createTestStudentEntityWithSectionAssociation(STUDENT_ID, SECTION_ID)));

        entities = studentGradebookOptionalFieldAppender.applyOptionalField(entities, null);
        assertEquals("Should be 1", 1, entities.size());

        List<EntityBody> studentGradebookAssociations = (List<EntityBody>) entities.get(0).get("studentGradebookEntries");
        assertEquals("Should match", 2, studentGradebookAssociations.size());
        assertEquals("Should match", STUDENT_ID, studentGradebookAssociations.get(0).get("studentId"));
        assertEquals("Should match", SECTION_ID, studentGradebookAssociations.get(0).get("sectionId"));

        EntityBody body = (EntityBody) ((List<EntityBody>) entities.get(0).get("studentGradebookEntries")).get(0);
        EntityBody gradebookEntry = (EntityBody) body.get("gradebookEntries");
        assertNotNull("Should not be null", gradebookEntry);
        assertEquals("Should match", "Unit Tests", gradebookEntry.get("gradebookEntryType"));
        assertEquals("", gradebookEntry.get("id"), body.get("gradebookEntryId"));
    }

    private Map<String, Object> createTestStudentEntityWithSectionAssociation(String id, String sectionId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("id", id);
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);

        EntityBody body = new EntityBody();
        body.put("studentId", id);
        body.put("sectionId", sectionId);

        List<EntityBody> list = new ArrayList<EntityBody>();
        list.add(body);

        entity.put("studentSectionAssociation", list);

        return entity;
    }

    private Map<String, Object> createStudentGradebookEntry(String studentId, String sectionId,
                                                                   String gradebookEntryId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("sectionId", sectionId);
        entity.put("gradebookEntryId", gradebookEntryId);
        entity.put("letterGradeEarned", "A");
        entity.put("numericGradeEarned", "90");

        return entity;
    }

    private Map<String, Object> createGradebookEntry() {

        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("gradebookEntryType", "Unit Tests");
        entity.put("dateAssigned", "2012-03-22");

        return entity;
    }
}

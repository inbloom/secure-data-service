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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.MockRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentAssessmentOptionalFieldAppenderTest {
    
    @Autowired
    private OptionalFieldAppender studentAssessmentOptionalFieldAppender;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private String assessmentId1;
    private String assessmentId2;
    private static final String STUDENT_ID = "1234";

    @Before
    public void setup() {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        repo.create("student", createTestStudentEntity(STUDENT_ID));

        assessmentId1 = repo.create("assessment", createAssessment()).getEntityId();
        assessmentId2 = repo.create("assessment", createAssessment()).getEntityId();

        repo.create("studentAssessment", createTestStudentAssessment(STUDENT_ID, assessmentId1));
        repo.create("studentAssessment", createTestStudentAssessment(STUDENT_ID, assessmentId2));
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testApplyOptionalField() {
        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(new EntityBody(createTestStudentEntity(STUDENT_ID)));
        
        entities = studentAssessmentOptionalFieldAppender.applyOptionalField(entities, null);
        assertEquals("Should be 1", 1, entities.size());

        List<EntityBody> studentAssessments = (List<EntityBody>) entities.get(0).get("studentAssessments");
        assertEquals("Should match", 2, studentAssessments.size());
        assertEquals("Should match", STUDENT_ID, studentAssessments.get(0).get("studentId"));

        EntityBody body = (EntityBody) ((List<EntityBody>) entities.get(0).get("studentAssessments")).get(0);
        EntityBody assessment = (EntityBody) body.get("assessments");
        assertNotNull("Should not be null", assessment);
        assertEquals("Should match", "Reading", assessment.get("academicSubject"));
        assertEquals("", assessment.get("id"), body.get("assessmentId"));
    }

    private Map<String, Object> createTestStudentEntity(String id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("id", id);
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private Map<String, Object> createTestStudentAssessment(String studentId, String assessmentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("assessmentId", assessmentId);
        entity.put("administrationLanguage", "ENGLISH");
        entity.put("administrationDate", "2011-01-01");
        return entity;
    }

    private Map<String, Object> createAssessment() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("academicSubject", "Reading");
        entity.put("gradeLevelAssessed", "Tenth Grade");
        return entity;
    }
}

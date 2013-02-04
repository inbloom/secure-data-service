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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentTranscriptOptionalFieldAppenderTest {

    @Autowired
    private OptionalFieldAppender studentTranscriptOptionalFieldAppender;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private Entity studentEntity;

    @Before
    public void setup() {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        studentEntity = repo.create("student", createTestStudentEntity());
        String studentId = studentEntity.getEntityId();

        String sessionId1 = repo.create("session", createSession()).getEntityId();
        String sessionId2 = repo.create("session", createSession()).getEntityId();

        String courseId1 = repo.create("course", createCourse()).getEntityId();
        String courseId2 = repo.create("course", createCourse()).getEntityId();

        String courseOfferingId1 = repo.create("courseOffering", createCourseOffering(courseId1)).getEntityId();
        String courseOfferingId12 = repo.create("courseOffering", createCourseOffering(courseId2)).getEntityId();

        String sectionId1 = repo.create("section", createSection(sessionId1, courseOfferingId1)).getEntityId();
        String sectionId2 = repo.create("section", createSection(sessionId2, courseOfferingId12)).getEntityId();

        repo.create("studentSectionAssociation", createStudentSectionAssociation(studentId, sectionId1));
        repo.create("studentSectionAssociation", createStudentSectionAssociation(studentId, sectionId2));

        String sarID = repo.create("studentAcademicRecord", createAcademicRecord(studentId)).getEntityId();
        repo.create("courseTranscript", createCourseTranscript(studentId, courseId1, sarID));
        repo.create("courseTranscript", createCourseTranscript(studentId, courseId2, sarID));

    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testApplyOptionalField() {
        List<EntityBody> entities = new ArrayList<EntityBody>();
        EntityBody body = new EntityBody();
        body.putAll(studentEntity.getBody());
        body.put("id", studentEntity.getEntityId());
        entities.add(body);

        entities = studentTranscriptOptionalFieldAppender.applyOptionalField(entities, null);

        assertEquals("Should match", 1, entities.size());

        EntityBody transcripts = (EntityBody) entities.get(0).get("transcript");
        assertNotNull("Should be not null", transcripts);

        List<EntityBody> studentSectionAssociations = (List<EntityBody>) transcripts.get("studentSectionAssociations");
        assertNotNull("Should be not null", studentSectionAssociations);
        assertEquals("Should match", 2, studentSectionAssociations.size());

        EntityBody section = (EntityBody) studentSectionAssociations.get(0).get("sections");
        assertNotNull("Should not be null", section);
        assertNotNull("Should not be null", section.get("sessions"));
        assertEquals("Should match", "1999", ((EntityBody) section.get("sessions")).get("schoolYear"));
        assertEquals("Should match", section.get("sessionId"), ((EntityBody) section.get("sessions")).get("id"));
        assertNotNull("Should not be null", section.get("courses"));
        assertEquals("Should match", "Math", ((EntityBody) section.get("courses")).get("courseTitle"));
        assertEquals("Should match", "Math A", section.get("sectionname"));

        List<EntityBody> courseTranscripts = (List<EntityBody>) transcripts.get("courseTranscripts");
        assertNotNull("Should not be null", courseTranscripts);
        assertEquals("Should match", 2, courseTranscripts.size());
        assertEquals("Should match", "A", courseTranscripts.get(0).get("letterGradeEarned"));
    }

    private Map<String, Object> createTestStudentEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private Map<String, Object> createStudentSectionAssociation(String studentId, String sectionId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("sectionId", sectionId);

        return entity;
    }

    private Map<String, Object> createSection(String sessionId, String courseOfferingId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sessionId", sessionId);
        entity.put("courseOfferingId", courseOfferingId);
        entity.put("sectionname", "Math A");

        return entity;
    }

    private Map<String, Object> createCourseTranscript(String studentId, String courseId, String sarID) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("courseId", courseId);
        entity.put("letterGradeEarned", "A");
        entity.put("studentAcademicRecordId", sarID);

        return entity;
    }

    private Map<String, Object> createSession() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("schoolYear", "1999");

        return entity;
    }

    private Map<String, Object> createCourse() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("courseTitle", "Math");

        return entity;
    }

    private Map<String, Object> createCourseOffering(String courseId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("courseId", courseId);

        return entity;
    }

    private Map<String, Object> createAcademicRecord(String studentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        return entity;
    }
}

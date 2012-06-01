package org.slc.sli.api.resources.v1.view.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
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

        String sectionId1 = repo.create("section", createSection(sessionId1, courseId1)).getEntityId();
        String sectionId2 = repo.create("section", createSection(sessionId2, courseId2)).getEntityId();

        repo.create("studentSectionAssociation", createStudentSectionAssociation(studentId, sectionId1));
        repo.create("studentSectionAssociation", createStudentSectionAssociation(studentId, sectionId2));

        String sarID = repo.create("studentAcademicRecord", createAcademicRecord(studentId)).getEntityId();
        repo.create("studentTranscriptAssociation", createStudentTranscript(studentId, courseId1, sarID));
        repo.create("studentTranscriptAssociation", createStudentTranscript(studentId, courseId2, sarID));

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
        assertEquals("Should match", section.get("courseId"), ((EntityBody) section.get("courses")).get("id"));
        assertEquals("Should match", "Math A", section.get("sectionname"));

        List<EntityBody> studentTranscriptAssociations = (List<EntityBody>) transcripts.get("courseTranscripts");
        assertNotNull("Should not be null", studentTranscriptAssociations);
        assertEquals("Should match", 2, studentTranscriptAssociations.size());
        assertEquals("Should match", "A", studentTranscriptAssociations.get(0).get("letterGradeEarned"));
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

    private Map<String, Object> createSection(String sessionId, String courseId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sessionId", sessionId);
        entity.put("courseId", courseId);
        entity.put("sectionname", "Math A");

        return entity;
    }

    private Map<String, Object> createStudentTranscript(String studentId, String courseId, String sarID) {
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

    private Map<String, Object> createAcademicRecord(String studentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        return entity;
    }
}

package org.slc.sli.api.resources.v1.view.impl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
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
public class StudentAttendanceOptionalFieldAppenderTest {
    private static final String COLLECTION = "tempCollection";
    private static final String STUDENT_ID = "1234";
    
    @Autowired
    private StudentAttendanceOptionalFieldAppender studentAttendanceOptionalFieldAppender;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private Entity studentEntity;

    @Before
    public void setup() {
        String beginDate1 = "2011-10-20", beginDate2 = "2008-09-10";

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        studentEntity = repo.create("student", createTestStudentEntity());
        String studentId = studentEntity.getEntityId();

        String sessionId1 = repo.create("session", createSession(beginDate1)).getEntityId();
        String sessionId2 = repo.create("session", createSession(beginDate2)).getEntityId();

        String sectionId1 = repo.create("section", createSection(sessionId1)).getEntityId();
        String sectionId2 = repo.create("section", createSection(sessionId2)).getEntityId();

        Entity studentSectionAssociation1 = repo.create("studentSectionAssociation", createStudentSectionAssociation(studentId, sectionId1));
        Entity studentSectionAssociation2 = repo.create("studentSectionAssociation", createStudentSectionAssociation(studentId, sectionId2));

        EntityBody body1 = new EntityBody();
        body1.putAll(studentSectionAssociation1.getBody());
        body1.put("id", studentSectionAssociation1.getEntityId());

        EntityBody body2 = new EntityBody();
        body2.putAll(studentSectionAssociation2.getBody());
        body2.put("id", studentSectionAssociation2.getEntityId());

        List<EntityBody> list = new ArrayList<EntityBody>();
        list.add(body1);
        list.add(body2);

        studentEntity.getBody().put("studentSectionAssociation", list);

        repo.create("attendance", createAttendance(studentId, "2012-03-10"));
        repo.create("attendance", createAttendance(studentId, "2011-11-12"));
        repo.create("attendance", createAttendance(studentId, "2009-08-07"));
        repo.create("attendance", createAttendance(studentId, "2011-12-12"));
        repo.create("attendance", createAttendance(studentId, "2006-12-12"));
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

        entities = studentAttendanceOptionalFieldAppender.applyOptionalField(entities);

        assertEquals("Should be 1", 1, entities.size());
        assertNotNull("Should not be null", entities.get(0).get("attendances"));

        List<EntityBody> attendances = (List<EntityBody>) ((EntityBody) entities.get(0).get("attendances")).get("attendances");
        assertEquals("Should match", 4, attendances.size());

        for (EntityBody attendance : attendances) {
            if (attendance.get("eventDate").equals("2006-12-12")) {
                fail("Should not include this event");
                break;
            }
        }

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

    private Map<String, Object> createSection(String sessionId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sessionId", sessionId);

        return entity;
    }

    private Map<String, Object> createSession(String beginDate) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("schoolYear", "1999");
        entity.put("beginDate", beginDate);

        return entity;
    }

    private Map<String, Object> createAttendance(String studentId, String eventDate) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("eventDate", eventDate);

        return entity;
    }
}

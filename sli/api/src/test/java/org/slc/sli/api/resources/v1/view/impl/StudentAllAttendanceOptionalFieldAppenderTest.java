package org.slc.sli.api.resources.v1.view.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentAllAttendanceOptionalFieldAppenderTest {
    private static final String COLLECTION = "tempCollection";
    private static final String STUDENT_ID = "1234";
    
    @Autowired
    private StudentAllAttendanceOptionalFieldAppender studentAllAttendanceOptionalFieldAppender;

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private Entity student1Entity;
    private Entity student2Entity;

    @Before
    public void setup() {
        repo.deleteAll();

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        //create the student entity
        student1Entity = repo.create("student", createTestStudentEntity());
        student2Entity = repo.create("student", createTestStudentEntity());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupMockForApplyOptionalField() {

        repo.create("attendance", createAttendance(student1Entity.getEntityId(), "2012-03-10"));
        repo.create("attendance", createAttendance(student1Entity.getEntityId(), "2011-11-12"));
        repo.create("attendance", createAttendance(student1Entity.getEntityId(), "2009-08-07"));
        repo.create("attendance", createAttendance(student1Entity.getEntityId(), "2011-12-12"));
        repo.create("attendance", createAttendance(student2Entity.getEntityId(), "2011-11-01"));
        repo.create("attendance", createAttendance("randomId", "2006-12-01"));
    }

    @Test
    public void testApplyOptionalField() {
        setupMockForApplyOptionalField();

        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(makeEntityBody(student1Entity));
        entities.add(makeEntityBody(student2Entity));

        entities = studentAllAttendanceOptionalFieldAppender.applyOptionalField(entities, null);

        assertEquals("Should be 2", 2, entities.size());
        assertNotNull("Should not be null", entities.get(0).get("attendances"));

        List<EntityBody> attendances = (List<EntityBody>) ((EntityBody) entities.get(0).get("attendances")).get("attendances");
        assertEquals("Should match", 4, attendances.size());

        for (EntityBody attendance : attendances) {
            if (attendance.get("eventDate").equals("2006-12-12") || attendance.get("eventDate").equals("2006-11-10")) {
                fail("Should not include this event");
                break;
            }
        }

        attendances = (List<EntityBody>) ((EntityBody) entities.get(1).get("attendances")).get("attendances");
        assertEquals("Should match", 1, attendances.size());
    }

    private EntityBody makeEntityBody(Entity entity) {
        EntityBody body = new EntityBody();
        body.putAll(entity.getBody());
        body.put("id", entity.getEntityId());

        return body;
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

    private Map<String, Object> createSession(String beginDate, String schoolYear) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("schoolYear", schoolYear);
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

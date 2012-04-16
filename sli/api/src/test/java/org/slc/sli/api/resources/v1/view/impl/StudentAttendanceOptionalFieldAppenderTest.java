package org.slc.sli.api.resources.v1.view.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
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

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private Entity studentEntity;

    @Before
    public void setup() {
        repo.deleteAll();

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        //create the student entity
        studentEntity = repo.create("student", createTestStudentEntity());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupMockForApplyOptionalField() {
        String beginDate1 = "2011-10-20", beginDate2 = "2008-09-10",
                beginDate3 = "2010-11-12", beginDate4 = "2007-01-01";

        Entity session1 = repo.create("session", createSession(beginDate1, "1999-2000"));
        Entity session2 = repo.create("session", createSession(beginDate2, "1999-2000"));
        Entity session3 = repo.create("session", createSession(beginDate3, "1999-2000"));
        Entity session4 = repo.create("session", createSession(beginDate4, "1998-1999"));

        String sectionId1 = repo.create("section", createSection(session1.getEntityId())).getEntityId();
        String sectionId2 = repo.create("section", createSection(session2.getEntityId())).getEntityId();
        String sectionId3 = repo.create("section", createSection(session3.getEntityId())).getEntityId();
        String sectionId4 = repo.create("section", createSection(session4.getEntityId())).getEntityId();

        Entity studentSectionAssociation1 = repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId1));
        Entity studentSectionAssociation2 = repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId2));
        repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId3));
        repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId4));

        List<EntityBody> list = new ArrayList<EntityBody>();
        list.add(makeEntityBody(studentSectionAssociation1));
        list.add(makeEntityBody(studentSectionAssociation2));

        studentEntity.getBody().put("studentSectionAssociation", list);

        repo.create("attendance", createAttendance(studentEntity.getEntityId(), "2012-03-10"));
        repo.create("attendance", createAttendance(studentEntity.getEntityId(), "2011-11-12"));
        repo.create("attendance", createAttendance(studentEntity.getEntityId(), "2009-08-07"));
        repo.create("attendance", createAttendance(studentEntity.getEntityId(), "2011-12-12"));
        repo.create("attendance", createAttendance(studentEntity.getEntityId(), "2006-12-12"));
    }

    @Test
    public void testApplyOptionalField() {
        setupMockForApplyOptionalField();

        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(makeEntityBody(studentEntity));

        entities = studentAttendanceOptionalFieldAppender.applyOptionalField(entities, null);

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

    @Test
    public void testGetBeginDate() throws ParseException {
        String beginDate1 = "2011-10-20", beginDate2 = "2008-09-10",
                beginDate3 = "2010-11-12", beginDate4 = "2007-01-01";

        Entity selectedSession = repo.create("session", createSession(beginDate1, "1999-2000"));
        Entity session2 = repo.create("session", createSession(beginDate2, "1999-2000"));
        Entity session3 = repo.create("session", createSession(beginDate3, "1999-2000"));
        Entity session4 = repo.create("session", createSession(beginDate4, "1998-1999"));

        String sectionId1 = repo.create("section", createSection(selectedSession.getEntityId())).getEntityId();
        String sectionId2 = repo.create("section", createSection(session2.getEntityId())).getEntityId();
        String sectionId3 = repo.create("section", createSection(session3.getEntityId())).getEntityId();
        String sectionId4 = repo.create("section", createSection(session4.getEntityId())).getEntityId();

        repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId1));
        repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId2));
        repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId3));
        repo.create("studentSectionAssociation",
                createStudentSectionAssociation(studentEntity.getEntityId(), sectionId4));

        List<String> studentIds = new ArrayList<String>();
        studentIds.add(studentEntity.getEntityId());

        Date date = studentAttendanceOptionalFieldAppender.getBeginDate(studentIds, makeEntityBody(selectedSession), 1);

        assertNotNull("Should not be null", date);
        assertEquals("Should match", beginDate2, formatter.format(date));
    }

    @Test
    public void testGetSchoolYears() {
        String currentSchoolYear = "1999-2000";

        List<String> schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears(currentSchoolYear, 1);
        assertEquals("Should match", 1, schoolYears.size());
        assertEquals("Should match", "1999-2000", schoolYears.get(0));

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears(currentSchoolYear, 2);
        assertEquals("Should match", 2, schoolYears.size());
        assertEquals("Should match", "1999-2000", schoolYears.get(0));
        assertEquals("Should match", "1998-1999", schoolYears.get(1));

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears(currentSchoolYear, 3);
        assertEquals("Should match", 3, schoolYears.size());
        assertEquals("Should match", "1999-2000", schoolYears.get(0));
        assertEquals("Should match", "1998-1999", schoolYears.get(1));
        assertEquals("Should match", "1997-1998", schoolYears.get(2));

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears(currentSchoolYear, -1);
        assertEquals("Should match", 1, schoolYears.size());
        assertEquals("Should match", "1999-2000", schoolYears.get(0));

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears("year1-year2", 1);
        assertEquals("Should match", 1, schoolYears.size());
        assertEquals("Should match", "year1-year2", schoolYears.get(0));

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears("year1-year2", 2);
        assertEquals("Should match", 1, schoolYears.size());
        assertEquals("Should match", "year1-year2", schoolYears.get(0));

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears("", 2);
        assertEquals("Should match", 0, schoolYears.size());

        schoolYears = studentAttendanceOptionalFieldAppender.getSchoolYears(null, 2);
        assertEquals("Should match", 0, schoolYears.size());
    }

    @Test
    public void testGetYearSuffix() {
        assertEquals("Should match", 1, studentAttendanceOptionalFieldAppender.getYearSuffix("1"));
        assertEquals("Should match", 2, studentAttendanceOptionalFieldAppender.getYearSuffix("2"));
        assertEquals("Should match", 3, studentAttendanceOptionalFieldAppender.getYearSuffix("3"));
        assertEquals("Should match", 4, studentAttendanceOptionalFieldAppender.getYearSuffix("4"));
        assertEquals("Should match", 4, studentAttendanceOptionalFieldAppender.getYearSuffix("100"));
        assertEquals("Should match", 0, studentAttendanceOptionalFieldAppender.getYearSuffix("not a number"));
        assertEquals("Should match", 2, studentAttendanceOptionalFieldAppender.getYearSuffix("-2"));
        assertEquals("Should match", 4, studentAttendanceOptionalFieldAppender.getYearSuffix("-100"));
        assertEquals("Should match", 0, studentAttendanceOptionalFieldAppender.getYearSuffix(null));
        assertEquals("Should match", 0, studentAttendanceOptionalFieldAppender.getYearSuffix(""));
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

    private Map<String, Object> createSchool(String name) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("name", name);

        return entity;
    }

    private Map<String, Object> createSchoolSessionAssociation(String schoolId, String sessionId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("schoolId", schoolId);
        entity.put("sessionId", sessionId);

        return entity;
    }
}

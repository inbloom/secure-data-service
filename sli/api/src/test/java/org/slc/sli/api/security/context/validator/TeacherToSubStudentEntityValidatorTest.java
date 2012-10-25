package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TeacherToSubStudentEntityValidatorTest {

    @Autowired
    private TeacherToSubStudentEntityValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    private PagingRepositoryDelegate<Entity> mockRepo;
    private TeacherToStudentValidator teacherToStudentValidator;
    private Set<String> attendances;
    private Set<String> studentIds;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        attendances = new HashSet<String>();
        studentIds = new HashSet<String>();
        mockRepo = Mockito.mock(PagingRepositoryDelegate.class);
        teacherToStudentValidator = Mockito.mock(TeacherToStudentValidator.class);

        String user = "fake teacher";
        String fullName = "Fake Teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("teacher");
        Mockito.when(entity.getEntityId()).thenReturn("1");
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, "111");

        Map<String, Object> attendance1 = buildAttendanceForStudent("student123", "school123");
        Entity attendanceEntity1 = new MongoEntity("attendance", attendance1);
        attendances.add(attendanceEntity1.getEntityId());
        studentIds.add("student123");

        Mockito.when(mockRepo.findAll(Mockito.eq(EntityNames.ATTENDANCE), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(attendanceEntity1));

        validator.setRepo(mockRepo);
        validator.setTeacherToStudentValidator(teacherToStudentValidator);
    }

    @After
    public void tearDown() {
        mockRepo = null;
        teacherToStudentValidator = null;
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidateTeacherToSubEntities() throws Exception {
        assertTrue(validator.canValidate(EntityNames.ATTENDANCE));
    }

    @Test
    public void testCanValidateTeacherToCourseTranscript() throws Exception {
        assertTrue(validator.canValidate(EntityNames.COURSE_TRANSCRIPT));
    }

    @Test
    public void testCanValidateTeacherToDisciplineAction() throws Exception {
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_ACTION));
    }

    @Test
    public void testCanValidateTeacherToStudentAcademicRecord() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT_ACADEMIC_RECORD));
    }

    @Test
    public void testCanValidateTeacherToStudentAssessment() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION));
    }

    @Test
    public void testCanValidateTeacherToStudentDisciplineIncident() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION));
    }

    @Test
    public void testCanValidateTeacherToStudentGradebookEntry() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT_GRADEBOOK_ENTRY));
    }

    @Test
    public void testCanNotValidateOtherEntities() throws Exception {
        assertFalse(validator.canValidate(EntityNames.STUDENT));
    }

    @Test
    public void testCanGetAccessToAttendance() throws Exception {
        Mockito.when(teacherToStudentValidator.validate("attendance", studentIds)).thenReturn(true);
        assertTrue(validator.validate(attendances, "attendance"));
    }

    @Test
    public void testCanNotGetAccessToAttendance() throws Exception {
        Mockito.when(teacherToStudentValidator.validate("attendance", studentIds)).thenReturn(false);
        assertFalse(validator.validate(attendances, "attendance"));
    }

    private Map<String, Object> buildAttendanceForStudent(String studentId, String schoolId) {
        Map<String, Object> attendance = new HashMap<String, Object>();
        attendance.put("studentId", studentId);
        attendance.put("schoolId", schoolId);
        List<Map<String, Object>> schoolYearAttendance = new ArrayList<Map<String, Object>>();
        Map<String, Object> onlyYear = new HashMap<String, Object>();
        onlyYear.put("schoolYear", "2011-2012");
        onlyYear.put("attendanceEvent", new ArrayList<Map<String, Object>>());
        attendance.put("schoolYearAttendance", schoolYearAttendance);
        return attendance;
    }
}

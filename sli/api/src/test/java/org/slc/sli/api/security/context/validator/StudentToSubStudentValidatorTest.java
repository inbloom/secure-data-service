package org.slc.sli.api.security.context.validator;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentToSubStudentValidatorTest extends TestCase {

    @Autowired
    private StudentToSubStudentValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private PagingRepositoryDelegate<Entity> mockRepo;

    Entity student1;
    Entity student2;
    Entity attendance1;
    Entity attendance2;
    Entity studentAcademicRecord1;
    Entity studentAcademicRecord2;
    
    

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        student1 = helper.generateStudent();
        student2 = helper.generateStudent();
        Entity school = helper.generateSchoolEdOrg(null);

        attendance1 = helper.generateAttendance(student1.getEntityId(), school.getEntityId());
        attendance2 = helper.generateAttendance(student2.getEntityId(), school.getEntityId());
        studentAcademicRecord1 = helper.generateStudentAcademicRecord(student1.getEntityId());
        studentAcademicRecord2 = helper.generateStudentAcademicRecord(student2.getEntityId());        
        
    }

    @Test
    public void testCanValidate() {
        injector.setStudentContext(student1);
        assertTrue(validator.canValidate(EntityNames.ATTENDANCE, true));
        assertTrue(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_ASSESSMENT, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_ASSESSMENT, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_GRADEBOOK_ENTRY, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_GRADEBOOK_ENTRY, false));
        assertTrue(validator.canValidate(EntityNames.GRADE, true));
        assertTrue(validator.canValidate(EntityNames.GRADE, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_ACADEMIC_RECORD, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_ACADEMIC_RECORD, false));
        assertTrue(validator.canValidate(EntityNames.REPORT_CARD, true));
        assertTrue(validator.canValidate(EntityNames.REPORT_CARD, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_SCHOOL_ASSOCIATION, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_SCHOOL_ASSOCIATION, false));

        assertFalse(validator.canValidate(EntityNames.STUDENT, false));
        assertFalse(validator.canValidate(EntityNames.PARENT, true));
        assertFalse(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, false));
        assertFalse(validator.canValidate(EntityNames.STAFF, true));
        assertFalse(validator.canValidate(EntityNames.DISCIPLINE_ACTION, false));
        assertFalse(validator.canValidate(EntityNames.GRADUATION_PLAN, true));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, true));
    }

    @Test
    public void testValidateSingleEntity() {
        injector.setStudentContext(student1);
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(attendance1.getEntityId()));
        assertTrue(validator.validate(EntityNames.ATTENDANCE, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList(studentAcademicRecord1.getEntityId()));
        assertTrue(validator.validate(EntityNames.STUDENT_ACADEMIC_RECORD, idsToValidate).containsAll(idsToValidate));

        injector.setStudentContext(student2);
        idsToValidate = new HashSet<String>(Arrays.asList(attendance2.getEntityId()));
        assertTrue(validator.validate(EntityNames.ATTENDANCE, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList(studentAcademicRecord2.getEntityId()));
        assertTrue(validator.validate(EntityNames.STUDENT_ACADEMIC_RECORD, idsToValidate).containsAll(idsToValidate));
    }

    @Test
    public void testValidateNegativeHeterogeneousList() {
        injector.setStudentContext(student1);

        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(attendance1.getEntityId(),attendance2.getEntityId()));
        assertFalse(validator.validate(EntityNames.ATTENDANCE, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList(studentAcademicRecord1.getEntityId(), studentAcademicRecord2.getEntityId()));
        assertFalse(validator.validate(EntityNames.STUDENT_ACADEMIC_RECORD, idsToValidate).containsAll(idsToValidate));
    }

}

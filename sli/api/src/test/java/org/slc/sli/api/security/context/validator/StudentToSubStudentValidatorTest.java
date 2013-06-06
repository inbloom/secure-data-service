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
        injector.setEducatorContext();
        assertFalse(validator.canValidate(EntityNames.STUDENT_ASSESSMENT, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_ASSESSMENT, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_ACADEMIC_RECORD, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_ACADEMIC_RECORD, false));
        //injector.setStudentContext();

    }
}

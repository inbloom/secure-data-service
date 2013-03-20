package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class TeacherToStudentCohortAssociationValidatorTest {
    
    Entity teacher1;
    Entity teacher2;
    Entity cohort1; //assoc with teacher1 and student1
    Entity cohort2; //assoc with teacher2 and student2
    Entity cohort3; //assoc with student1
    Entity staffCohortAssoc1;
    Entity staffCohortAssoc2;
    Entity studentCohortAssoc1;
    Entity studentCohortAssoc2;
    Entity studentCohortAssoc3; //not assoc with student1, but no staff
    
    @Autowired
    private TeacherToStudentCohortAssociationValidator validator;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Before
    public void setUp() {
        teacher1 = helper.generateTeacher();
        teacher2 = helper.generateTeacher();
        String student1 = helper.generateStudentAndStudentSchoolAssociation("student1", "edorg", false);
        String student2 = helper.generateStudentAndStudentSchoolAssociation("student2", "edorg", false);
        cohort1 = helper.generateCohort("edorg");
        cohort2 = helper.generateCohort("edorg");
        cohort3 = helper.generateCohort("edorg");
        staffCohortAssoc1 = helper.generateStaffCohort(teacher1.getEntityId(), cohort1.getEntityId(), false, true);
        staffCohortAssoc2 = helper.generateStaffCohort(teacher2.getEntityId(), cohort2.getEntityId(), false, true);
        studentCohortAssoc1 = helper.generateStudentCohort(student1, cohort1.getEntityId(), false);
        studentCohortAssoc2 = helper.generateStudentCohort(student2, cohort2.getEntityId(), false);
        studentCohortAssoc3 = helper.generateStudentCohort(student1, cohort3.getEntityId(), false);
    }
    
    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake teacher";
        String fullName = "Fake teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }
    
    @Test
    public void testCanValidate() {
        setupCurrentUser(teacher1);
        Assert.assertTrue(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, false));
        Assert.assertTrue(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, true));
        
        injector.setStaffContext();
        Assert.assertFalse(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, false));
        Assert.assertFalse(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, true));
    }
    
    @Test
    public void testValidAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertTrue(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, new HashSet<String>(Arrays.asList(studentCohortAssoc1.getEntityId()))));
    }
    
    @Test
    public void testValidAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertTrue(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, new HashSet<String>(Arrays.asList(studentCohortAssoc2.getEntityId()))));
    }
    
    
    @Test
    public void testInvalidAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, new HashSet<String>(Arrays.asList(studentCohortAssoc2.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, new HashSet<String>(Arrays.asList(studentCohortAssoc3.getEntityId()))));
    }
    
    @Test
    public void testInvalidAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, new HashSet<String>(Arrays.asList(studentCohortAssoc1.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, new HashSet<String>(Arrays.asList(studentCohortAssoc3.getEntityId()))));
    }
    
    @Test
    public void testMulti() {
        setupCurrentUser(teacher2);
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, 
                new HashSet<String>(Arrays.asList(studentCohortAssoc2.getEntityId(), studentCohortAssoc1.getEntityId()))));
    }

}

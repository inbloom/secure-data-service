package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
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
public class TeacherToStudentProgramAssociationValidatorTest {
    
    @Autowired
    private TeacherToStudentProgramAssociationValidator validator;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    Entity studentProgramAssoc1;  //not expired, student record access
    Entity studentProgramAssoc2;  //not expired, no student record access
    Entity studentProgramAssoc3;  //expired, student record access
    Entity studentProgramAssoc4;  //expired, no student record access
    Entity studentProgramAssoc5;  //no assoc
    
    @Before
    public void setUp() {
        helper.setUpTeacherContext();
        Entity program1 = helper.generateProgram();
        Entity program2 = helper.generateProgram();
        Entity program3 = helper.generateProgram();
        Entity program4 = helper.generateProgram();
        Entity program5 = helper.generateProgram();
        studentProgramAssoc1 = helper.generateStudentProgram("student1", program1.getEntityId(), false);
        Entity staffProgramAssoc1 = helper.generateStaffProgram(ValidatorTestHelper.STAFF_ID, program1.getEntityId(), false, true);
        studentProgramAssoc2 = helper.generateStudentProgram("student1", program2.getEntityId(), false);
        Entity staffProgramAssoc2 = helper.generateStaffProgram(ValidatorTestHelper.STAFF_ID, program2.getEntityId(), false, false);
        studentProgramAssoc3 = helper.generateStudentProgram("student1", program3.getEntityId(), false);
        Entity staffProgramAssoc3 = helper.generateStaffProgram(ValidatorTestHelper.STAFF_ID, program3.getEntityId(), true, true);
        studentProgramAssoc4 = helper.generateStudentProgram("student1", program4.getEntityId(), false);
        Entity staffProgramAssoc4 = helper.generateStaffProgram(ValidatorTestHelper.STAFF_ID, program4.getEntityId(), true, false);
        studentProgramAssoc5 = helper.generateStudentProgram("student1", program5.getEntityId(), false);
        
    }
        
    @Test
    public void testCanValidate() {
        Assert.assertTrue(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, false));
        Assert.assertTrue(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, true));
    }
    
    @Test
    public void testValidAccessTeacher() {
        Assert.assertTrue(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new HashSet<String>(Arrays.asList(studentProgramAssoc1.getEntityId()))));
    }
    
    
    @Test
    public void testInvalidAccessTeacher1() {
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new HashSet<String>(Arrays.asList(studentProgramAssoc2.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new HashSet<String>(Arrays.asList(studentProgramAssoc3.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new HashSet<String>(Arrays.asList(studentProgramAssoc4.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new HashSet<String>(Arrays.asList(studentProgramAssoc5.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new HashSet<String>(
                Arrays.asList(studentProgramAssoc1.getEntityId(), studentProgramAssoc3.getEntityId()))));
    }
    

}

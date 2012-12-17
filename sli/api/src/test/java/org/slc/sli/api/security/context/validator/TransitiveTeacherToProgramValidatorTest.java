package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
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
public class TransitiveTeacherToProgramValidatorTest {
    
    Entity teacher1;
    Entity teacher2;
    Entity program1;    //teacher1 - direct access
    Entity program2;    //teacher2 - direct access
    Entity program3;    //teacher2 - through student2
    Entity program4;    //expired program of teacher 1
    Entity program5;    //teacher1, direct access, no studentRecordAccess, which shouldn't matter
    Entity program6;    //teacher1, through student1
    Entity program7;    //teacher1, through student1, expired
    Entity program8;    //teacher1, both direct and expired student access
    
    @Autowired
    private TransitiveTeacherToProgramValidator validator;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Before
    public void setUp() {
        teacher1 = helper.generateTeacher();
        teacher2 = helper.generateTeacher();
        program1 = helper.generateProgram();
        program2 = helper.generateProgram();
        program3 = helper.generateProgram();
        program4 = helper.generateProgram();
        program5 = helper.generateProgram();
        program6 = helper.generateProgram();
        program7 = helper.generateProgram();
        program8 = helper.generateProgram();
        helper.generateStaffProgram(teacher1.getEntityId(), program1.getEntityId(), false, true);
        helper.generateStaffProgram(teacher2.getEntityId(), program2.getEntityId(), false, true);
        helper.generateStaffProgram(teacher1.getEntityId(), program4.getEntityId(), true, true);
        helper.generateStaffProgram(teacher1.getEntityId(), program5.getEntityId(), false, false);
        helper.generateStaffProgram(teacher1.getEntityId(), program8.getEntityId(), false, true);
        String student1 = helper.generateStudentAndStudentSchoolAssociation("student1", "school1", false);
        String student2 = helper.generateStudentAndStudentSchoolAssociation("student1", "school1", false);
        Entity section1 = helper.generateSection("school1");
        Entity section2 = helper.generateSection("school1");
        helper.generateStudentProgram(student2, program3.getEntityId(), false);
        helper.generateStudentProgram(student1, program6.getEntityId(), false);
        helper.generateStudentProgram(student1, program7.getEntityId(), true);
        helper.generateStudentProgram(student1, program8.getEntityId(), true);
        helper.generateTSA(teacher1.getEntityId(), section1.getEntityId(), false);
        helper.generateTSA(teacher2.getEntityId(), section2.getEntityId(), false);
        helper.generateSSA(student2, section2.getEntityId(), false);
        helper.generateSSA(student1, section1.getEntityId(), false);
    }
    
    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }
    
    @Test
    public void testCanValidate() {
        setupCurrentUser(teacher1);
        Assert.assertTrue(validator.canValidate(EntityNames.PROGRAM, true));
        Assert.assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
        
        injector.setStaffContext();
        Assert.assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
        Assert.assertFalse(validator.canValidate(EntityNames.PROGRAM, true));
    }
    
    @Test
    public void testValidDirectAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program1.getEntityId()))));
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program5.getEntityId()))));
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program8.getEntityId()))));
    }
    
    @Test
    public void testValidThroughStudentAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program6.getEntityId()))));
    }
    
    @Test
    public void testExpiredStudentAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program7.getEntityId()))));
    }
    
    @Test
    public void testValidDirectAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program2.getEntityId()))));
    }
    
    @Test
    public void testValidThroughStudentAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program3.getEntityId()))));
    }
    
   @Test
    public void testInvalidAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program2.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program3.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program4.getEntityId()))));
    }
    
    @Test
    public void testInvalidAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program1.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program4.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program5.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program6.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program7.getEntityId()))));
    }
    
    @Test
    public void testMulti() {
        setupCurrentUser(teacher1);
        //both good
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, 
                new HashSet<String>(Arrays.asList(program1.getEntityId(), program6.getEntityId()))));
        
        //both bad
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, 
                new HashSet<String>(Arrays.asList(program3.getEntityId(), program7.getEntityId()))));
        
        //one good, one bad
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, 
                new HashSet<String>(Arrays.asList(program1.getEntityId(), program3.getEntityId()))));

    }

}

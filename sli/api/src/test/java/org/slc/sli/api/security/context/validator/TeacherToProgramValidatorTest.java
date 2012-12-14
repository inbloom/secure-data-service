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
public class TeacherToProgramValidatorTest {
    
    Entity teacher1;
    Entity teacher2;
    Entity program1;    //teacher1
    Entity program2;    //teacher2
    Entity program3;    //expired program of teacher 1
    Entity program4;    //teacher1, no studentRecordAccess, which shouldn't matter
    
    @Autowired
    private TeacherToProgramValidator validator;
    
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
        helper.generateStaffProgram(teacher1.getEntityId(), program1.getEntityId(), false, true);
        helper.generateStaffProgram(teacher2.getEntityId(), program2.getEntityId(), false, true);
        helper.generateStaffProgram(teacher1.getEntityId(), program3.getEntityId(), true, true);
        helper.generateStaffProgram(teacher1.getEntityId(), program4.getEntityId(), false, false);
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
        Assert.assertTrue(validator.canValidate(EntityNames.PROGRAM, false));
        Assert.assertTrue(validator.canValidate(EntityNames.PROGRAM, true));
        
        injector.setStaffContext();
        Assert.assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
        Assert.assertFalse(validator.canValidate(EntityNames.PROGRAM, true));
    }
    
    @Test
    public void testValidAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program1.getEntityId()))));
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program4.getEntityId()))));
    }
    
    @Test
    public void testValidAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertTrue(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program2.getEntityId()))));
    }
    
    @Test
    public void testInvalidAccessTeacher1() {
        setupCurrentUser(teacher1);
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program2.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program3.getEntityId()))));
    }
    
    @Test
    public void testInvalidAccessTeacher2() {
        setupCurrentUser(teacher2);
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program1.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program3.getEntityId()))));
        Assert.assertFalse(validator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(program4.getEntityId()))));
    }

}

package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericToStaffValidatorTest {

    @Autowired
    private GenericToStaffValidator validator;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private SecurityContextInjector injector;

    Entity teacher1 = null;
    Entity teacher2 = null;      
    Entity staff1 = null;
    Entity staff2 = null;  
    
    Entity edOrg = null;
        
    private void setupTeacher() {
        String user = "fake teacher";
        String fullName = "Fake Teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);
        teacher1 = helper.generateTeacher();
        injector.setCustomContext(user, fullName, "MERPREALM", roles, teacher1, "111");
    }
    
    private void setupStaff() {
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        staff1 = helper.generateTeacher();
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff1, "111");
    }
    
    @Before
    public void setup() {
        edOrg = helper.generateEdorgWithParent(null);
        teacher2 = helper.generateTeacher();
        staff2 = helper.generateTeacher();

    }
    
    @Test
    public void testCanValidate() {
        setupTeacher();
        assertTrue(validator.canValidate(EntityNames.STAFF, false));
        
        setupStaff();
        assertTrue(validator.canValidate(EntityNames.STAFF, false));
    }
    
    @Test
    public void testValidateTeacher() {
        setupTeacher();
        assertTrue(validator.validate(EntityNames.STAFF, 
                new HashSet<String>(Arrays.asList(teacher1.getEntityId()))));
    }
    
    @Test
    public void testCannotValidateOtherStaff() {
        setupStaff();
        assertFalse(validator.validate(EntityNames.STAFF, 
                new HashSet<String>(Arrays.asList(staff2.getEntityId()))));
    }
    
    @Test
    public void testCannotValidateOtherTeacher() {
        setupTeacher();
        assertFalse(validator.validate(EntityNames.STAFF, 
                new HashSet<String>(Arrays.asList(teacher2.getEntityId()))));
    }
    
    @Test
    public void testValidateStaff() {
        setupStaff();
        assertTrue(validator.validate(EntityNames.STAFF, 
                new HashSet<String>(Arrays.asList(staff1.getEntityId()))));
    }
    
    
    @Test
    public void testValidateEmpty() {
        assertFalse(validator.validate(EntityNames.STAFF, 
                new HashSet<String>()));
    }
    
    @Test
    public void testSuccess() {
        setupTeacher();
        Assert.assertTrue(validator.validate(EntityNames.TEACHER, Collections.singleton(teacher1.getEntityId())));
    }

    @Test
    public void testWrongId() {
        setupTeacher();
        Assert.assertFalse(validator.validate(EntityNames.TEACHER, Collections.singleton("Hammerhands")));
        Assert.assertFalse(validator.validate(EntityNames.TEACHER, Collections.singleton("Nagas")));
        Assert.assertFalse(validator.validate(EntityNames.TEACHER, Collections.singleton("Phantom Warriors")));
    }

    @Test
    public void testMultipleIds() {
        setupTeacher();
        Assert.assertFalse(validator.validate(EntityNames.TEACHER, new HashSet<String>(Arrays.asList(teacher1.getEntityId(), "Pikemen", "Pegasi"))));
    }
}

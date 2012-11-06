package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
public class StaffToCourseValidatorTest {
    
    @Autowired
    private StaffToCourseValidator validator;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private SecurityContextInjector injector;
    
    Entity staff1 = null;   //associated to LEA
    Entity staff2 = null;   //associated to school1
    Entity staff3 = null;   //associated to school2
    Entity teacher1 = null;
    Entity course1 = null;   //associated to LEA
    Entity course2 = null;   //associated to school1
    Entity course3 = null;   //associated to school2
    
    Entity lea1 = null;
    Entity school1 = null;
    Entity school2 = null;
    
    @Before
    public void setUp() {

        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);
        repo.deleteAll("course", null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);
        
        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        teacher1 = repo.create("teacher", body);
        
        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff3");
        staff3 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        lea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school1 = repo.create("educationOrganization", body);
        
        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);
        
        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);
        
        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school2.getEntityId());
        body.put("staffReference", staff3.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);
        
        body = new HashMap<String, Object>();
        body.put("schoolId", lea1.getEntityId());
        course1 = repo.create("course", body);
        
        body = new HashMap<String, Object>();
        body.put("schoolId", school1.getEntityId());
        course2 = repo.create("course", body);

        body = new HashMap<String, Object>();
        body.put("schoolId", school2.getEntityId());
        course3 = repo.create("course", body);
    }
    
    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }
    
    @Test
    public void testCanValidateAsStaff() {
        setupCurrentUser(staff1);
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE, false));
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }
    
    @Test
    public void testCannotValidateAsTeacher() {
        setupCurrentUser(teacher1);
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.COURSE, false));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.COURSE, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }
    
    @Test
    public void testValidAssociationsForStaff1() {
        setupCurrentUser(staff1);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE, new HashSet<String>(
                Arrays.asList(course1.getEntityId(), course2.getEntityId(), course3.getEntityId()))));
    }
    
    @Test
    public void testValidAssociationsForStaff2() {
        setupCurrentUser(staff2);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
    }
    
    @Test
    public void testInvalidAssociationsForStaff2() {
        setupCurrentUser(staff2);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
    }
    
    @Test
    public void testValidAssociationsForStaff3() {
        setupCurrentUser(staff3);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
    }
    
    @Test
    public void testInvalidAssociationsForStaff3() {
        setupCurrentUser(staff3);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
    }
    
}

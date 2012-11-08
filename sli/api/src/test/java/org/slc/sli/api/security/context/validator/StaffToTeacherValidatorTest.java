package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

/**
 * Unit tests for teacher --> staff context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToTeacherValidatorTest {

    @Autowired
    private StaffToTeacherValidator validator;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    Entity staff1 = null; //associated with lea1
    Entity staff2 = null; //associated with school1
    Entity teacher1 = null; //associated with school1 and school2

    Entity teacher2 = null; //not associated
    Entity teacher3 = null; //associated with school1
    Entity lea1 = null;
    Entity lea2 = null;
    Entity school1 = null;
    Entity school2 = null;

    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }

    @Before
    public void setUp() {
        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        teacher1 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        teacher2 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        teacher3 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        lea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        lea2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea2.getEntityId());
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
        body.put("schoolId", school1.getEntityId());
        body.put("teacherId", teacher1.getEntityId());
        repo.create(EntityNames.TEACHER_SCHOOL_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("schoolId", school2.getEntityId());
        body.put("teacherId", teacher1.getEntityId());
        repo.create(EntityNames.TEACHER_SCHOOL_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("schoolId", school1.getEntityId());
        body.put("teacherId", teacher3.getEntityId());
        repo.create(EntityNames.TEACHER_SCHOOL_ASSOCIATION, body);

    }

    @After
    public void tearDown() {
        repo = null;
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testCanValidateStaffToTeacher() throws Exception {
        setupCurrentUser(staff1);
        assertTrue(validator.canValidate(EntityNames.TEACHER, true));
        assertTrue(validator.canValidate(EntityNames.TEACHER, false));
    }

    @Test
    public void testInvalidTeacherAssociation() {
        setupCurrentUser(staff1);
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(teacher2.getEntityId()))));
    }

    @Test
    public void testValidAssociationThroughSchool() {
        setupCurrentUser(staff2);
        assertTrue(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(teacher1.getEntityId(), teacher3.getEntityId()))));
    }

    @Test
    public void testValidAssociationThroughLEA() {
        setupCurrentUser(staff1);
        assertTrue(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(teacher1.getEntityId(), teacher3.getEntityId()))));
    }

    @Test
    public void testInvalidTeacher() {
        setupCurrentUser(staff1);
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(UUID.randomUUID().toString()))));
    }

    @Test
    public void testNoTeacher() {
        setupCurrentUser(staff1);
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>()));
    }


}

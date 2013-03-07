package org.slc.sli.api.security.context.validator;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tests for staff to courseTranscript validator
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToCourseTranscriptValidatorTest {

    @Autowired
    private StaffToCourseTranscriptValidator validator;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    Entity staff1 = null;
    Entity staff2 = null;

    Entity school1 = null;
    Entity school2 = null;

    Entity student1 = null;
    Entity student2 = null;

    Entity studentSchoolAssociation1 = null;
    Entity studentSchoolAssociation2 = null;

    Entity studentAcademicRecord1 = null;
    Entity studentAcademicRecord2 = null;

    Entity courseTranscript1 = null;
    Entity courseTranscript2 = null;

    @Before
    public void setUp() {

        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);
        repo.deleteAll("section", null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        school1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        school2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school2.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);

        Map<String, Object> schoolDenormalization = new HashMap<String, Object>();
        schoolDenormalization.put("exitWithdrawDate", "2020-12-12");
        schoolDenormalization.put("edOrgs", Arrays.asList(school1.getEntityId()));

        Map<String, Object> denormalization = new HashMap<String, Object>();
        denormalization.put("schools", Arrays.asList(schoolDenormalization));

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "1234");
        body.put("denormalization", denormalization);
        student1 = repo.create(EntityNames.STUDENT, body);

        schoolDenormalization = new HashMap<String, Object>();
        schoolDenormalization.put("exitWithdrawDate", "2020-12-12");
        schoolDenormalization.put("edOrgs", Arrays.asList(school2.getEntityId()));

        denormalization = new HashMap<String, Object>();
        denormalization.put("schools", Arrays.asList(schoolDenormalization));

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "1234");
        body.put("denormalization", denormalization);
        student2 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put("schoolId", school1.getEntityId());
        body.put("studentId", student1.getEntityId());
        body.put("exitWithdrawDate", "2020-12-12");
        studentSchoolAssociation1 = repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("schoolId", school2.getEntityId());
        body.put("studentId", student2.getEntityId());
        body.put("exitWithdrawDate", "2020-12-12");
        studentSchoolAssociation2 = repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("studentId", student1.getEntityId());
        studentAcademicRecord1 = repo.create(EntityNames.STUDENT_ACADEMIC_RECORD, body);

        body = new HashMap<String, Object>();
        body.put("studentId", student2.getEntityId());
        studentAcademicRecord2 = repo.create(EntityNames.STUDENT_ACADEMIC_RECORD, body);

        body = new HashMap<String, Object>();
        body.put("studentAcademicRecordId", studentAcademicRecord1.getEntityId());
        courseTranscript1 = repo.create(EntityNames.COURSE_TRANSCRIPT, body);

        body = new HashMap<String, Object>();
        body.put("studentAcademicRecordId", studentAcademicRecord2.getEntityId());
        courseTranscript2 = repo.create(EntityNames.COURSE_TRANSCRIPT, body);
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
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE_TRANSCRIPT, false));
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE_TRANSCRIPT, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }

    @Test
    public void testValidAssociationsForStaff1() {
        setupCurrentUser(staff1);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript1.getEntityId()))));
    }

    @Test
    public void testValidAssociationsForStaff2() {
        setupCurrentUser(staff2);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript2.getEntityId()))));
    }

    @Test
    public void testInValidAssociationsForStaff1() {
        setupCurrentUser(staff1);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript2.getEntityId()))));
    }

    @Test
    public void testInValidAssociationsForStaff2() {
        setupCurrentUser(staff2);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript1.getEntityId()))));
    }

    @Test
    public void testInvalidAssociations() {
        setupCurrentUser(staff2);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(UUID.randomUUID().toString()))));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>()));
    }

}

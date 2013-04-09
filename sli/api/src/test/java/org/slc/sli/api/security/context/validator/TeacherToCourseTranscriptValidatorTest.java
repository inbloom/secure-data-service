package org.slc.sli.api.security.context.validator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
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
 * Tests for teacher to courseTranscript validator
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TeacherToCourseTranscriptValidatorTest {

    @Autowired
    private TeacherToCourseTranscriptValidator validator;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    ValidatorTestHelper helper;

    Entity teacher1 = null;   //associated to school1

    Entity lea1 = null;
    Entity school1 = null;

    Entity section1 = null; //associated with teacher1
    Entity section2 = null; //no association

    Entity student1 = null;
    Entity student2 = null;

    Entity studentSectionAssociation1 = null;
    Entity studentSectionAssociation2 = null;

    Entity studentAcademicRecord1 = null;
    Entity studentAcademicRecord2 = null;

    Entity courseTranscript1 = null;
    Entity courseTranscript2 = null;

    @Before
    public void setUp() throws Exception {
        helper.resetRepo();

        Map<String, Object> body = new HashMap<String, Object>();
        teacher1 = helper.generateTeacher();

        lea1 = helper.generateEdorgWithParent(null);

        school1 = helper.generateEdorgWithParent(lea1.getEntityId());

        helper.generateTeacherSchool(teacher1.getEntityId(), school1.getEntityId());

        section1 = helper.generateSection("school1");
        helper.generateTSA(teacher1.getEntityId(), section1.getEntityId(), false);

        section2 = helper.generateSection("school1");

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "1234");
        student1 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "5678");
        student2 = repo.create(EntityNames.STUDENT, body);

        studentSectionAssociation1 = helper.generateSSA(student1.getEntityId(), section1.getEntityId(), false);
        studentSectionAssociation2 = helper.generateSSA(student2.getEntityId(), section2.getEntityId(), false);

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
    public void testCanValidateAsTeacher() {
        setupCurrentUser(teacher1);
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE_TRANSCRIPT, false));
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE_TRANSCRIPT, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }

    @Test
    public void testValidAssociations() {
        setupCurrentUser(teacher1);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript1.getEntityId()))));
    }

    @Test
    public void testInvalidAssociations() {
        setupCurrentUser(teacher1);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT,
                new HashSet<String>(Arrays.asList(UUID.randomUUID().toString()))));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT,
                new HashSet<String>()));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.COURSE_TRANSCRIPT,
                new HashSet<String>(Arrays.asList(courseTranscript2.getEntityId()))));
    }
}

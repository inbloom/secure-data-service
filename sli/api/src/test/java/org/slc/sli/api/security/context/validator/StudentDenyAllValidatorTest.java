package org.slc.sli.api.security.context.validator;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentDenyAllValidatorTest extends TestCase {

    @Autowired
    SecurityContextInjector injector;

    @Autowired
    ValidatorTestHelper helper;

    @Autowired
    StudentDenyAllValidator validator;

    @Before
    public void setUp() throws Exception {
        Entity student1 = helper.generateStudent();
        injector.setStudentContext(student1);
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_ACTION, true));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_ACTION, false));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, true));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, false));

        assertFalse(validator.canValidate(EntityNames.STUDENT, true));
        assertFalse(validator.canValidate(EntityNames.STAFF, false));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_GRADEBOOK_ENTRY, false));
    }

    @Test
    public void testValidate() {
        assertFalse(validator.validate(null, null));
        assertFalse(validator.validate(new String(), new HashSet<String>()));
    }
}

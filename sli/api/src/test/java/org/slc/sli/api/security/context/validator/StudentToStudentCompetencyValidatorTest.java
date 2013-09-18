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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentToStudentCompetencyValidatorTest extends TestCase {

    @Autowired
    StudentToStudentCompetencyValidator validator;

    @Autowired
    SecurityContextInjector injector;

    @Autowired
    ValidatorTestHelper helper;

    private Entity competency1;
    private Entity competency2;

    @Before
    public void setup() {
        Entity student1 = helper.generateStudent();
        Entity student2 = helper.generateStudent();

        Entity section1 = helper.generateSection("");
        Entity section2 = helper.generateSection("");

        Entity ssa1 = helper.generateSSA(student1.getEntityId(), section1.getEntityId(), false);
        Entity ssa2 = helper.generateSSA(student2.getEntityId(), section2.getEntityId(), false);

        competency1 = helper.generateStudentCompetency(ssa1.getEntityId(),"");
        competency2 = helper.generateStudentCompetency(ssa2.getEntityId(),"");

        injector.setStudentContext(student1);
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY, false));

        assertFalse(validator.canValidate(EntityNames.GRADEBOOK_ENTRY, true));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, true));
    }

    @Test
    public void testSingleValidate() {
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(competency1.getEntityId()));
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY, idsToValidate).containsAll(idsToValidate));
    }

    @Test
    public void testNegativeHeterogeneousValidate() {
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(competency1.getEntityId(),competency2.getEntityId()));
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY, idsToValidate).containsAll(idsToValidate));
    }
}

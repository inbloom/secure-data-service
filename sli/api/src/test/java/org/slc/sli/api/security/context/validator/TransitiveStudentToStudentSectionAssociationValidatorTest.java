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
public class TransitiveStudentToStudentSectionAssociationValidatorTest extends TestCase {

    @Autowired
    TransitiveStudentToStudentSectionAssociationValidator validator;

    @Autowired
    ValidatorTestHelper helper;

    @Autowired
    SecurityContextInjector injector;

    private Entity student1;
    private Entity student2;

    private Entity assoc1Current;
    private Entity assoc1Past;
    private Entity assoc2;

    @Before
    public void setUp() {
        student1 = helper.generateStudent();
        student2 = helper.generateStudent();
        Entity school = helper.generateSchoolEdOrg(null);
        Entity section1 = helper.generateSection(school.getEntityId());
        Entity section2 = helper.generateSection(school.getEntityId());
        assoc1Current = helper.generateSSA(student1.getEntityId(), section1.getEntityId(), false);
        assoc1Past = helper.generateSSA(student1.getEntityId(), section1.getEntityId(), true);
        assoc2 = helper.generateSSA(student2.getEntityId(), section2.getEntityId(), false);

        injector.setStudentContext(student1);
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STUDENT_SECTION_ASSOCIATION, true));

        assertFalse(validator.canValidate(EntityNames.STUDENT_SECTION_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_SCHOOL_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
        assertFalse(validator.canValidate(EntityNames.ASSESSMENT, true));
        assertFalse(validator.canValidate(EntityNames.GRADEBOOK_ENTRY, true));
        assertFalse(validator.canValidate(EntityNames.COHORT, true));
        assertFalse(validator.canValidate(EntityNames.STAFF_COHORT_ASSOCIATION, false));
    }

    @Test
    public void testPositiveValidate() {
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(assoc1Current.getEntityId(), assoc1Past.getEntityId()));
        assertTrue(validator.validate(EntityNames.STUDENT_SECTION_ASSOCIATION, idsToValidate).containsAll(idsToValidate));
    }

    @Test
    public void testHeterogeneousValidate() {
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(assoc1Current.getEntityId(), assoc2.getEntityId()));
        assertFalse(validator.validate(EntityNames.STUDENT_SECTION_ASSOCIATION, idsToValidate).containsAll(idsToValidate));
    }
}

package org.slc.sli.api.security.context.validator;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentToStudentAssociationValidatorTest extends TestCase {

    @Autowired
    StudentToStudentAssociationValidator validator;

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
        student1 = Mockito.mock(Entity.class);
        student2 = Mockito.mock(Entity.class);

        Entity student1Entity = helper.generateStudent();
        Entity student2Entity = helper.generateStudent();
        Entity school = helper.generateSchoolEdOrg(null);

        Entity cohort1 = helper.generateCohort(school.getEntityId());
        Entity cohort2 = helper.generateCohort(school.getEntityId());
        assoc1Current = helper.generateStudentCohort(student1Entity.getEntityId(), cohort1.getEntityId(), false);
        assoc1Past = helper.generateStudentCohort(student1Entity.getEntityId(), cohort1.getEntityId(), true);
        assoc2 = helper.generateStudentCohort(student2Entity.getEntityId(), cohort2.getEntityId(), false);

        List<Entity> embeddedDocs = Arrays.asList(assoc1Current, assoc1Past);
        Map<String, List<Entity>> superDocMap = new HashMap<String , List<Entity>>();
        superDocMap.put(EntityNames.STUDENT_COHORT_ASSOCIATION, embeddedDocs);
        Mockito.when(student1.getBody()).thenReturn(student1Entity.getBody());
        Mockito.when(student1.getType()).thenReturn("student");
        Mockito.when(student2.getBody()).thenReturn(student2Entity.getBody());
        Mockito.when(student2.getType()).thenReturn("student");

        Mockito.when(student1.getEmbeddedData()).thenReturn(superDocMap);

        injector.setStudentContext(student1);
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, false));

        assertFalse(validator.canValidate(EntityNames.STUDENT_SECTION_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_SECTION_ASSOCIATION, false));

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
        assertTrue(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, idsToValidate).containsAll(idsToValidate));
    }

    @Test
    public void testHeterogeneousValidate() {
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(assoc1Current.getEntityId(), assoc2.getEntityId()));
        assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, idsToValidate).containsAll(idsToValidate));
    }
}

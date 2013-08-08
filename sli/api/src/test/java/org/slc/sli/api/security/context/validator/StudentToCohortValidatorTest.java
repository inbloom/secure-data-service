
package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentToCohortValidatorTest {
    
    @Autowired
    StudentToCohortValidator underTest;
    
    @Autowired
    private SecurityContextInjector injector;

    @Mock
    Entity student1;
    
    @Before
    public void setUp() throws Exception {
        student1 = Mockito.mock(MongoEntity.class);
        when(student1.getEntityId()).thenReturn("student1");
        when(student1.getType()).thenReturn(EntityNames.STUDENT);
    }
    
    @Test
    public void canValidateTransitiveCohortForStudentOnly() {
        injector.setStudentContext(student1);
        assertTrue(underTest.canValidate(EntityNames.COHORT, true));
        assertFalse(underTest.canValidate(EntityNames.COHORT, false));
        
        injector.setEducatorContext();
        assertFalse(underTest.canValidate(EntityNames.COHORT, true));
        assertFalse(underTest.canValidate(EntityNames.COHORT, false));
    }
    
    @Test
    public void noEmbeddedDataCantValidate() {
        injector.setStudentContext(student1);
        when(student1.getEmbeddedData()).thenReturn(null);
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(student1.getEntityId()));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
    }
    
    @Test
    public void studentCanSeeDirectCohorts() {
        injector.setStudentContext(student1);
        Map<String, List<Entity>> embeddedData = buildCohortAssociation(student1.getEntityId());
        when(student1.getEmbeddedData()).thenReturn(embeddedData);
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("cohort123"));
        assertTrue(idsToValidate.containsAll(underTest.validate(EntityNames.COHORT, idsToValidate)));
    }
    
    @Test
    public void studentCannotSeeCohortsIamNotAssociatedWith() {
        injector.setStudentContext(student1);
        Map<String, List<Entity>> embeddedData = buildCohortAssociation(student1.getEntityId());
        when(student1.getEmbeddedData()).thenReturn(embeddedData);
        // bad id
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("cohort456"));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
        // good and bad id
        idsToValidate = new HashSet<String>(Arrays.asList("cohort123", "cohort456"));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
    }

    private Map<String, List<Entity>> buildCohortAssociation(String entityId) {
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();
        Entity studentCohortAssociations = Mockito.mock(MongoEntity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", student1.getEntityId());
        body.put("cohortId", "cohort123");
        body.put("endDate", "2100-01-01");
        when(studentCohortAssociations.getBody()).thenReturn(body);
        embeddedData.put(EntityNames.STUDENT_COHORT_ASSOCIATION, Arrays.asList(studentCohortAssociations));
        return embeddedData;
    }

}

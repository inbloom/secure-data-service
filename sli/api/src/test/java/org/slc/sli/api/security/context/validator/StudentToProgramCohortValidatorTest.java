/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
public class StudentToProgramCohortValidatorTest {
    
    @Autowired
    StudentToProgramCohortValidator underTest;
    
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
    public void canValidateNonTransitiveProgramCohortForStudentOnly() {
        injector.setStudentContext(student1);
        assertFalse(underTest.canValidate(EntityNames.PROGRAM, true));
        assertTrue(underTest.canValidate(EntityNames.PROGRAM, false));
        assertFalse(underTest.canValidate(EntityNames.COHORT, true));
        assertTrue(underTest.canValidate(EntityNames.COHORT, false));
        
        injector.setEducatorContext();
        assertFalse(underTest.canValidate(EntityNames.PROGRAM, true));
        assertFalse(underTest.canValidate(EntityNames.PROGRAM, false));
        assertFalse(underTest.canValidate(EntityNames.COHORT, true));
        assertFalse(underTest.canValidate(EntityNames.COHORT, false));
    }
    
    @Test
    public void noEmbeddedDataCantValidate() {
        injector.setStudentContext(student1);
        when(student1.getEmbeddedData()).thenReturn(null);
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList(student1.getEntityId()));
        assertFalse(underTest.validate(EntityNames.PROGRAM, idsToValidate).containsAll(idsToValidate));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
    }
    
    @Test
    public void studentCanSeeDirectProgramsCohorts() {
        injector.setStudentContext(student1);
        Map<String, List<Entity>> embeddedData = buildProgramAssociation(student1.getEntityId());
        when(student1.getEmbeddedData()).thenReturn(embeddedData);
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("Program123"));
        assertTrue(underTest.validate(EntityNames.PROGRAM, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList("Cohort123"));
        assertTrue(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
    }
    
    @Test
    public void studentCannotSeeProgramsCohortsIamNotAssociatedWith() {
        injector.setStudentContext(student1);
        Map<String, List<Entity>> embeddedData = buildProgramAssociation(student1.getEntityId());
        when(student1.getEmbeddedData()).thenReturn(embeddedData);
        // bad id
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("Program456"));
        assertFalse(underTest.validate(EntityNames.PROGRAM, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList("Cohort456"));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
        // good and bad id
        idsToValidate = new HashSet<String>(Arrays.asList("Program123", "Program456"));
        assertFalse(underTest.validate(EntityNames.PROGRAM, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList("Cohort123", "Cohort456"));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
    }
    
    @Test
    public void cannotValidateExpiredAssociation() {
        injector.setStudentContext(student1);
        Map<String, List<Entity>> embeddedData = buildProgramAssociation(student1.getEntityId(), "2000-01-01");
        when(student1.getEmbeddedData()).thenReturn(embeddedData);

        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("Program123"));
        assertFalse(underTest.validate(EntityNames.PROGRAM, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList("Cohort123"));
        assertFalse(underTest.validate(EntityNames.COHORT, idsToValidate).containsAll(idsToValidate));
    }

    private Map<String, List<Entity>> buildProgramAssociation(String entityId, String date) {
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        Entity studentProgramAssociations = Mockito.mock(MongoEntity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", student1.getEntityId());
        body.put("programId", "Program123");
        body.put("endDate", date);
        when(studentProgramAssociations.getBody()).thenReturn(body);
        embeddedData.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, Arrays.asList(studentProgramAssociations));

        Entity studentCohortAssociations = Mockito.mock(MongoEntity.class);
        Map<String, Object> cohortBody = new HashMap<String, Object>();
        cohortBody.put("studentId", student1.getEntityId());
        cohortBody.put("cohortId", "Cohort123");
        cohortBody.put("endDate", date);
        when(studentCohortAssociations.getBody()).thenReturn(cohortBody);
        embeddedData.put(EntityNames.STUDENT_COHORT_ASSOCIATION, Arrays.asList(studentCohortAssociations));

        return embeddedData;
    }

    private Map<String, List<Entity>> buildProgramAssociation(String entityId) {
        return buildProgramAssociation(entityId, "2100-01-01");
    }

}

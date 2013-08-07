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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the staff to student cohort association validator.
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToStudentCohortProgramAssociationValidatorTest {
    
    @Autowired
    @InjectMocks
    private StaffToStudentCohortProgramAssociationValidator validator;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Mock
    private StaffToStudentValidator mockStudentValidator;

    Set<String> cohortIds;
    Set<String> programIds;
    
    @Before
    public void setUp() {
        // Set up the principal
        String user = "fake Staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn(helper.STAFF_ID);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, helper.ED_ORG_ID);
        
        cohortIds = new HashSet<String>();
        programIds = new HashSet<String>();
        
        mockStudentValidator = Mockito.mock(StaffToStudentValidator.class);
        MockitoAnnotations.initMocks(this);

    }
    
    @After
    public void tearDown() {
        repo.deleteAll(EntityNames.STUDENT_COHORT_ASSOCIATION, new NeutralQuery());
        
        mockStudentValidator = null;
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COHORT_ASSOCIATION, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, true));

        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_SECTION_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_SECTION_ASSOCIATION, false));
    }
    
    @Test
    public void testCanValidateValidCohortAssociation() {
        Mockito.when(mockStudentValidator.validate(Mockito.eq(EntityNames.STUDENT), Mockito.any(Set.class)))
                .thenReturn(true);
        for (int i = 0; i < 10; ++i) {
            cohortIds.add(helper.generateStudentCohort("Boop", "" + i, false).getEntityId());
        }
        assertTrue(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, cohortIds).equals(cohortIds));
    }

    @Test
    public void testCanValidateValidProgramAssociation() {
        Mockito.when(mockStudentValidator.validate(Mockito.eq(EntityNames.STUDENT), Mockito.any(Set.class)))
                .thenReturn(true);
        for (int i = 0; i < 10; ++i) {
            programIds.add(helper.generateStudentProgram("Boop", "" + i, false).getEntityId());
        }
        assertTrue(validator.validate(EntityNames.STUDENT_PROGRAM_ASSOCIATION, programIds));
    }

    @Test
    public void testCanNotValidExpiredAssociation() {
        Mockito.when(mockStudentValidator.validate(Mockito.eq(EntityNames.STUDENT), Mockito.any(Set.class)))
                .thenReturn(true);
        cohortIds.add(helper.generateStudentCohort("Boop", "Beep", true).getEntityId());
        assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, cohortIds));
    }
    
    @Test
    public void testCanNotValidateAssociationWithoutStudentAccess() {
        Mockito.when(mockStudentValidator.validate(Mockito.eq(EntityNames.STUDENT), Mockito.any(Set.class)))
                .thenReturn(false);
        for (int i = 0; i < 10; ++i) {
            cohortIds.add(helper.generateStudentCohort("Boop", "" + i, false).getEntityId());
        }
        assertFalse(validator.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, cohortIds));
    }
}

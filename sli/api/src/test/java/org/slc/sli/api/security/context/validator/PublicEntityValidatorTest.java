/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests for staff/teacher --> non-transitive public entity context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class PublicEntityValidatorTest {

    @Autowired
    private PublicEntityValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private ValidatorTestHelper helper;

    @Before
    public void setUp() {
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn("1");
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, "123");
    }

    @After
    public void tearDown() {
        repo.deleteAll(EntityNames.ASSESSMENT, new NeutralQuery());
        repo.deleteAll(EntityNames.LEARNING_OBJECTIVE, new NeutralQuery());
        repo.deleteAll(EntityNames.LEARNING_STANDARD, new NeutralQuery());
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidationStaff() throws Exception {
        // First, check can validate for public entities
        // This resolver shouldn't care about the transitive access flag
        assertTrue(validator.canValidate(EntityNames.ASSESSMENT, true));
        assertTrue(validator.canValidate(EntityNames.ASSESSMENT, false));
        assertTrue(validator.canValidate(EntityNames.LEARNING_OBJECTIVE, true));
        assertTrue(validator.canValidate(EntityNames.LEARNING_OBJECTIVE, false));
        assertTrue(validator.canValidate(EntityNames.LEARNING_STANDARD, true));
        assertTrue(validator.canValidate(EntityNames.LEARNING_STANDARD, false));

        // Next, check that it does not return true for non-public entities
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
    }
    
    @Test
    public void testCanValidationTeacher() throws Exception {
        injector.setEducatorContext();
        // First, check can validate for public entities
        // This resolver shouldn't care about the transitive access flag
        assertTrue(validator.canValidate(EntityNames.ASSESSMENT, true));
        assertTrue(validator.canValidate(EntityNames.ASSESSMENT, false));
        assertTrue(validator.canValidate(EntityNames.LEARNING_OBJECTIVE, true));
        assertTrue(validator.canValidate(EntityNames.LEARNING_OBJECTIVE, false));
        assertTrue(validator.canValidate(EntityNames.LEARNING_STANDARD, true));
        assertTrue(validator.canValidate(EntityNames.LEARNING_STANDARD, false));

        // Next, check that it does not return true for non-public entities
        assertFalse(validator.canValidate(EntityNames.COHORT, true));
      
    }

    @Test
    public void testValidateSingleAssessment() throws Exception {
        HashSet<String> assessmentIds = new HashSet<String>();
        assessmentIds.add(helper.generateAssessment().getEntityId());
        assertTrue(validator.validate(EntityNames.ASSESSMENT, assessmentIds));
    }

    @Test
    public void testValidateMultipleAssessments() throws Exception {
        HashSet<String> assessmentIds = new HashSet<String>();
        assessmentIds.add(helper.generateAssessment().getEntityId());
        assessmentIds.add(helper.generateAssessment().getEntityId());
        assessmentIds.add(helper.generateAssessment().getEntityId());
        assertTrue(validator.validate(EntityNames.ASSESSMENT, assessmentIds));
    }

    @Test
    public void testValidateSingleLearningObjective() throws Exception {
        HashSet<String> learningObjectiveIds = new HashSet<String>();
        learningObjectiveIds.add(helper.generateLearningObjective().getEntityId());
        assertTrue(validator.validate(EntityNames.LEARNING_OBJECTIVE, learningObjectiveIds));
    }

    @Test
    public void testValidateMultipleLearningObjectives() throws Exception {
        HashSet<String> learningObjectiveIds = new HashSet<String>();
        learningObjectiveIds.add(helper.generateLearningObjective().getEntityId());
        learningObjectiveIds.add(helper.generateLearningObjective().getEntityId());
        learningObjectiveIds.add(helper.generateLearningObjective().getEntityId());
        assertTrue(validator.validate(EntityNames.LEARNING_OBJECTIVE, learningObjectiveIds));
    }

    @Test
    public void testValidateSingleLearningStandard() throws Exception {
        HashSet<String> learningStandardIds = new HashSet<String>();
        learningStandardIds.add(helper.generateLearningStandard().getEntityId());
        assertTrue(validator.validate(EntityNames.LEARNING_STANDARD, learningStandardIds));
    }

    @Test
    public void testValidateMultipleLearningStandards() throws Exception {
        HashSet<String> learningStandardIds = new HashSet<String>();
        learningStandardIds.add(helper.generateLearningStandard().getEntityId());
        learningStandardIds.add(helper.generateLearningStandard().getEntityId());
        learningStandardIds.add(helper.generateLearningStandard().getEntityId());
        assertTrue(validator.validate(EntityNames.LEARNING_STANDARD, learningStandardIds));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGuards() throws Exception {
        validator.validate(EntityNames.STUDENT, new HashSet<String>(Arrays.asList("student1", "student2")));
    }

}

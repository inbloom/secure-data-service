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
import org.mockito.Mockito;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the staff to student competency objective validator.
 * 
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class StaffToStudentCompetencyObjectiveValidatorTest {
    
    private static final String STAFF_ID = "1";

    @Autowired
    private StaffToStudentCompetencyObjectiveValidator validator;

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
        Mockito.when(entity.getEntityId()).thenReturn(STAFF_ID);
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, "123");
    }

    @After
    public void tearDown() {
        repo.deleteAll(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new NeutralQuery());
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidation() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, false));
        

        assertFalse(validator.canValidate(EntityNames.ASSESSMENT, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
        assertFalse(validator.canValidate(EntityNames.COHORT, false));
        assertFalse(validator.canValidate(EntityNames.COURSE, true));
        assertFalse(validator.canValidate(EntityNames.DISCIPLINE_ACTION, false));
        assertFalse(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, true));
        assertFalse(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, false));
        assertFalse(validator.canValidate(EntityNames.GRADE, true));
        assertFalse(validator.canValidate(EntityNames.GRADEBOOK_ENTRY, false));
        assertFalse(validator.canValidate(EntityNames.GRADING_PERIOD, true));
        assertFalse(validator.canValidate(EntityNames.LEARNING_OBJECTIVE, true));
        assertFalse(validator.canValidate(EntityNames.LEARNING_STANDARD, false));
        assertFalse(validator.canValidate(EntityNames.PARENT, false));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, true));
        assertFalse(validator.canValidate(EntityNames.SESSION, false));
        assertFalse(validator.canValidate(EntityNames.SCHOOL, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT, false));
        assertFalse(validator.canValidate(EntityNames.STAFF, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
        assertFalse(validator.canValidate(EntityNames.REPORT_CARD, false));
        assertFalse(validator.canValidate(EntityNames.TEACHER, true));
        assertFalse(validator.canValidate(EntityNames.STAFF_COHORT_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STAFF_PROGRAM_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_ASSESSMENT, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_PARENT_ASSOCIATION, false));
    }
    
    @Test
    public void testSingleScobjGoodValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String schoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, schoolId, false);
        String scObjId = helper.generateStudentCompetencyObjective(schoolId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(scObjId);
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
    @Test
    public void testExpiredStaffSchoolValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String schoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, schoolId, true);
        String scObjId = helper.generateStudentCompetencyObjective(schoolId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(scObjId);
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
    @Test
    public void testSeperateStaffSchoolValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String aSchoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        String bSchoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, aSchoolId, false);
        String scObjId = helper.generateStudentCompetencyObjective(bSchoolId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(scObjId);
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
    @Test
    public void testScobjInheritanceValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String schoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, schoolId, false);
        String scObjId = helper.generateStudentCompetencyObjective(seaId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(scObjId);
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
    @Test
    public void testScobjUnderneathValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String schoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, seaId, false);
        String scObjId = helper.generateStudentCompetencyObjective(schoolId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(scObjId);
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
    @Test
    public void testMultipleScobjGoodValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String schoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, schoolId, false);
        String aScObjId = helper.generateStudentCompetencyObjective(schoolId).getEntityId();
        String bScObjId = helper.generateStudentCompetencyObjective(leaId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(aScObjId);
        scObjIds.add(bScObjId);
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
    @Test
    public void testMultipleScobjMixedValidation() {
        // Data setup
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String aSchoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        String bSchoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(STAFF_ID, aSchoolId, false);
        String aScObjId = helper.generateStudentCompetencyObjective(aSchoolId).getEntityId();
        String bScObjId = helper.generateStudentCompetencyObjective(bSchoolId).getEntityId();
        
        // Test Validate
        Set<String> scObjIds = new HashSet<String>();
        scObjIds.add(aScObjId);
        scObjIds.add(bScObjId);
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, scObjIds));
    }
    
}

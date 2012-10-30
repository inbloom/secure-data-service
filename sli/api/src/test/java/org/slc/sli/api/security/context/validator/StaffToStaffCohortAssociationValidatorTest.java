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
import java.util.Set;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToStaffCohortAssociationValidatorTest {
    
    @Autowired
    private StaffToStaffCohortAssociationValidator validator;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    Set<String> cohortIds;
    
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
    }
    
    @After
    public void tearDown() {
        repo.deleteAll(EntityNames.STAFF_COHORT_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STAFF_COHORT_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STAFF_COHORT_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
    }
    
    @Test
    public void testCanValidateStaffToCohortAssociation() {
        Entity school = helper.generateEdorgWithParent(null);
        Entity cohort = helper.generateCohort(school.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity SCA = helper.generateStaffCohort(helper.STAFF_ID, cohort.getEntityId(), false, true);
        cohortIds.add(SCA.getEntityId());
        
        assertTrue(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
    }
    
    @Test
    public void testCanValidateIntersection() {
        Entity school = helper.generateEdorgWithParent(null);
        
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        for (int i = 0; i < 10; ++i) {
            Entity cohort = helper.generateCohort(school.getEntityId());
            Entity SCA = helper.generateStaffCohort(helper.STAFF_ID, cohort.getEntityId(), false, true);
            cohortIds.add(SCA.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
        
        // Create one out of bounds to get a failure
        Entity cohort = helper.generateCohort(school.getEntityId());
        Entity SCA = helper.generateStaffCohort("BOOP", cohort.getEntityId(), false, true);
        cohortIds.add(SCA.getEntityId());
        
        assertFalse(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
        
    }
    
    @Test
    public void testCanValidateStaffToStateSCA() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        
        helper.generateStaffEdorg(helper.STAFF_ID, lea.getEntityId(), false);
        Entity leaCohort = helper.generateCohort(lea.getEntityId());
        Entity seaCohort = helper.generateCohort(sea.getEntityId());
        cohortIds.add(helper.generateStaffCohort(helper.STAFF_ID, leaCohort.getEntityId(), false, true).getEntityId());
        assertTrue(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
        
        cohortIds.add(helper.generateStaffCohort("BEEP", helper.generateCohort(school.getEntityId()).getEntityId(),
                false, true).getEntityId());
        assertTrue(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));

        // Try the sea and succeed because of direct association
        cohortIds.add(helper.generateStaffCohort(helper.STAFF_ID, seaCohort.getEntityId(), false, true).getEntityId());
        assertTrue(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
    }
    
    @Test
    public void testCanNotValidateExpired() {
        Entity school = helper.generateEdorgWithParent(null);
        Entity cohort = helper.generateCohort(school.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        cohortIds.add(helper.generateStaffCohort(helper.STAFF_ID, cohort.getEntityId(), true, true).getEntityId());
        assertFalse(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
    }
    
    @Test
    public void testCanNotValidateInvalidSCA() {
        Entity school = helper.generateEdorgWithParent(null);
        Entity cohort = helper.generateCohort(school.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity SCA = helper.generateStaffCohort("MERP", cohort.getEntityId(), false, true);
        cohortIds.add(SCA.getEntityId());
        
        assertFalse(validator.validate(EntityNames.STAFF_COHORT_ASSOCIATION, cohortIds));
    }

}

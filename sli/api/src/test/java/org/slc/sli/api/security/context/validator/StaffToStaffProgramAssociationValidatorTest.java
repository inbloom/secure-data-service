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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the staff to staff program association validator.
 * 
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToStaffProgramAssociationValidatorTest {
    
    @Autowired
    private StaffToStaffProgramAssociationValidator validator;

    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    Set<String> cohortIds;
    
    @Before
    public void setUp() throws Exception {
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
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        cleanProgramData();

    }
    
    private void cleanProgramData() {
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.PROGRAM, new NeutralQuery());
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STAFF_PROGRAM_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.STAFF_PROGRAM_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, true));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
    }
    
    @Test
    public void testCanValidateStaffProgramAssociation() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, lea.getEntityId(), false);
        // Get the ones above that I'm associated to.
        Entity sca = helper.generateStaffProgram(helper.STAFF_ID,
                helper.generateProgram().getEntityId(), false, true);
        cohortIds.add(sca.getEntityId());
        assertTrue(validator.validate(EntityNames.STAFF_PROGRAM_ASSOCIATION, cohortIds));
        
        // And ones below me
        for (int i = 0; i < 5; ++i) {
            sca = helper.generateStaffProgram(i + "", helper.generateProgram().getEntityId(), false,
                    true);
            helper.generateStaffEdorg(i + "", school.getEntityId(), false);
            cohortIds.add(sca.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.STAFF_PROGRAM_ASSOCIATION, cohortIds));
        
    }
    
    @Test
    public void testCanNotValidateExpiredAssociation() {
        // Association itself is bad.
        Entity school = helper.generateEdorgWithParent(null);
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity sca = helper.generateStaffProgram(helper.STAFF_ID, helper.generateProgram()
                .getEntityId(), true, false);
        cohortIds.add(sca.getEntityId());
        assertFalse(validator.validate(EntityNames.STAFF_PROGRAM_ASSOCIATION, cohortIds));
        cohortIds.clear();
        cleanProgramData();
        
        // Staff edorg is bad
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), true);
        sca = helper.generateStaffProgram(helper.STAFF_ID, helper.generateProgram()
                .getEntityId(), false, false);
        cohortIds.add(sca.getEntityId());
        assertFalse(validator.validate(EntityNames.STAFF_PROGRAM_ASSOCIATION, cohortIds));

    }
    
    @Test
    public void testCanNotValidateOutsideOfEdorg() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());
        helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateEdorgWithParent(null);
        helper.generateStaffEdorg(helper.STAFF_ID, lea.getEntityId(), false);
        // Get the ones above that I'm associated to.
        Entity sca = helper.generateStaffProgram("MOOP", helper.generateProgram().getEntityId(),
                false, true);
        cohortIds.add(sca.getEntityId());
        assertFalse(validator.validate(EntityNames.STAFF_PROGRAM_ASSOCIATION, cohortIds));
    }
    
    @Test
    public void testCanNotValidateAtStateLevel() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());
        helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, lea.getEntityId(), false);
        // Get the ones above that I'm associated to.
        Entity sca = helper.generateStaffProgram("MOOP", helper.generateProgram().getEntityId(),
                false, true);
        cohortIds.add(sca.getEntityId());
        assertFalse(validator.validate(EntityNames.STAFF_PROGRAM_ASSOCIATION, cohortIds));
    }

}

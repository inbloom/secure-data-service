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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Unit tests for staff --> education organization context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToEdOrgValidatorTest {
    
    @Autowired
    private StaffToEdOrgValidator validator;
    
    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    Set<String> schoolIds;

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

        schoolIds = new HashSet<String>();
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());

    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.SCHOOL, false));
        assertTrue(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, false));
        assertFalse(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, true));
        assertFalse(validator.canValidate(EntityNames.SCHOOL, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
    }

    @Test
    public void testCanValidateStaffAtSchool() {
        Entity school = helper.generateEdorgWithParent(null);
        schoolIds.add(school.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
    }

    @Test
    public void testCanValidateStateStaffAtSchool() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        schoolIds.add(school.getEntityId());
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));

    }

    @Test
    public void testCanValidateStaffAtSchoolIntersection() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());
        for (int i = 0; i < 10; ++i) {
            Entity school = helper.generateEdorgWithParent(lea.getEntityId());
            helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
            schoolIds.add(school.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        schoolIds.add(school.getEntityId());
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
    }

    @Test
    public void testCanNotValidateStaffNotAtSchool() {
        Entity school = helper.generateEdorgWithParent(null);
        schoolIds.add(school.getEntityId());
        school = helper.generateEdorgWithParent(null);
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
    }

    @Test
    public void testCanNotValidateExpiredStaff() {
        Entity school = helper.generateEdorgWithParent(null);
        schoolIds.add(school.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), true);
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
    }

}

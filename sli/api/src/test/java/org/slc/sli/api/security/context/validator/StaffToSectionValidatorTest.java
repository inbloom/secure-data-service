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

import javax.annotation.Resource;

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

/**
 * Unit tests for staff --> section context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToSectionValidatorTest {

    @Resource(name = "staffToSectionValidator")
    private AbstractContextValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    private Set<String> sectionIds;

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
        sectionIds = new HashSet<String>();
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        repo.deleteAll(EntityNames.SECTION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
    }

    @Test
    public void testCanValidateValidationType() {
        assertTrue(validator.canValidate(EntityNames.SECTION, false));
        assertTrue(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
    }

    @Test
    public void testCanValidateSectionAtSchoolLevel() {
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        helper.generateStaffEdorg(helper.STAFF_ID, seaId, false);
        for (int i = 0; i < 3; ++i) {
            sectionIds.add(helper.generateSection(seaId).getEntityId());
        }
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testCanValidateSectionAtStateLevel() {
        String seaId = helper.generateEdorgWithParent(null).getEntityId();
        String leaId = helper.generateEdorgWithParent(seaId).getEntityId();
        String schoolId = helper.generateEdorgWithParent(leaId).getEntityId();
        helper.generateStaffEdorg(helper.STAFF_ID, seaId, false);
        for (int i = 0; i < 3; ++i) {
            sectionIds.add(helper.generateSection(schoolId).getEntityId());
        }
        for (int i = 0; i < 3; ++i) {
            sectionIds.add(helper.generateSection(leaId).getEntityId());
        }
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testCanNotValidateSectionAcrossEdOrgs() {
        String edorgId = helper.generateEdorgWithParent(null).getEntityId();
        helper.generateStaffEdorg(helper.STAFF_ID, edorgId, false);
        for (int i = 0; i < 3; ++i) {
            sectionIds.add(helper.generateSection(helper.ED_ORG_ID).getEntityId());
        }
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testCanNotValidateExpiredSection() {
        String edorgId = helper.generateEdorgWithParent(null).getEntityId();
        helper.generateStaffEdorg(helper.STAFF_ID, edorgId, true);
        for (int i = 0; i < 3; ++i) {
            sectionIds.add(helper.generateSection(edorgId).getEntityId());
        }
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }

}

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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the staff to program validator.
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class StaffToProgramValidatorTest {

    @Resource
    private StaffToProgramValidator val;

    @Resource
    private ValidatorTestHelper helper;

    @Resource
    private MockRepo repo;

    @Resource
    private SecurityContextInjector injector;

    private Set<String> programIds;

    @Before
    public void init() {
        // Set up the principal
        String user = "fake Staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn(helper.STAFF_ID);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, helper.ED_ORG_ID);

        programIds = new HashSet<String>();
    }

    @After
    public void tearDown() {
        repo.deleteAll(EntityNames.PROGRAM, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
    }

    @Test
    public void testCanValidate() {
        Assert.assertTrue("Must be able to validate", val.canValidate(EntityNames.PROGRAM, false));
        Assert.assertFalse("Must not be able to validate", val.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }

    @Test
    public void testValidation() {
        Assert.assertFalse(val.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList("lamb"))));
    }

    @Test
    public void testCanNotValidateWithStuendRecordAccess() {
        Entity lea = helper.generateEdorgWithParent(null);
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity program = helper.generateProgram();
        programIds.add(program.getEntityId());
        helper.generateStaffProgram(helper.STAFF_ID, program.getEntityId(), false, false);
        assertFalse(val.validateWithStudentAccess(EntityNames.PROGRAM, programIds, true));
        assertTrue(val.validateWithStudentAccess(EntityNames.PROGRAM, programIds, false));
    }
}

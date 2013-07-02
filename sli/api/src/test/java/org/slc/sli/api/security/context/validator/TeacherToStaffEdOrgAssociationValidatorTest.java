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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for TeacherToStaffEdOrgAssociationValidatorTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
@TestExecutionListeners({WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@Component
public class TeacherToStaffEdOrgAssociationValidatorTest {
    @Autowired
    TeacherToStaffEdOrgAssociationValidator validator;

    @Autowired
    ValidatorTestHelper helper;

    Set<String> edOrgAssociationIds;

    @Before
    public void setUp() throws Exception {
        helper.setUpTeacherContext();
        edOrgAssociationIds = new HashSet<String>();
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STAFF_ED_ORG_ASSOCIATION, false));
        assertFalse(validator.canValidate(EntityNames.STAFF_ED_ORG_ASSOCIATION, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
    }


    @Test
    public void testCanValidateNonExpiredAssociation() {
        Entity assoc = helper.generateStaffEdorg(helper.STAFF_ID, helper.ED_ORG_ID, false);
        edOrgAssociationIds.add(assoc.getEntityId());
        assertTrue(validator.validate(EntityNames.STAFF_ED_ORG_ASSOCIATION, edOrgAssociationIds));
    }

    @Test
    public void testInvalidateExpiredAssociation() {
        Entity assoc = helper.generateStaffEdorg(helper.STAFF_ID, helper.ED_ORG_ID, true);
        edOrgAssociationIds.add(assoc.getEntityId());
        assertFalse(validator.validate(EntityNames.STAFF_ED_ORG_ASSOCIATION, edOrgAssociationIds));
    }



}

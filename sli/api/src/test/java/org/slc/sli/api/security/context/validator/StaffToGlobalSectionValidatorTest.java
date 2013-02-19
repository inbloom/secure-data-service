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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Unit tests for global section validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToGlobalSectionValidatorTest {

    @Autowired
    private StaffToGlobalSectionValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    Entity sea = null;
    Entity lea1 = null;
    Entity lea2 = null;
    Entity school1 = null;
    Entity school2 = null;
    Entity section1 = null;
    Entity section2 = null;

    Entity educator1 = null;
    Entity educator2 = null;
    Entity seaStaff = null;
    Entity lea1Staff = null;
    Entity lea2Staff = null;
    Entity school1Staff = null;
    Entity school2Staff = null;

    private void setContext(Entity actor, List<String> roles) {
        String user = "fake actor";
        String fullName = "Fake Actor";
        injector.setCustomContext(user, fullName, "MERPREALM", roles, actor, "111");
    }

    @Before
    public void setup() {
        sea = helper.generateEdorgWithParent(null);
        lea1 = helper.generateEdorgWithParent(sea.getEntityId());
        lea2 = helper.generateEdorgWithParent(sea.getEntityId());
        school1 = helper.generateEdorgWithParent(lea1.getEntityId());
        school2 = helper.generateEdorgWithParent(lea2.getEntityId());
        section1 = helper.generateSection(school1.getEntityId());
        section2 = helper.generateSection(school2.getEntityId());

        seaStaff = helper.generateStaff();
        helper.generateStaffEdorg(seaStaff.getEntityId(), sea.getEntityId(), false);

        lea1Staff = helper.generateStaff();
        helper.generateStaffEdorg(lea1Staff.getEntityId(), lea1.getEntityId(), false);

        lea2Staff = helper.generateStaff();
        helper.generateStaffEdorg(lea2Staff.getEntityId(), lea2.getEntityId(), false);

        school1Staff = helper.generateStaff();
        helper.generateStaffEdorg(school1Staff.getEntityId(), school1.getEntityId(), false);

        school2Staff = helper.generateStaff();
        helper.generateStaffEdorg(school2Staff.getEntityId(), school2.getEntityId(), false);

        educator1 = helper.generateTeacher();
        helper.generateTeacherSchool(educator1.getEntityId(), school1.getEntityId());

        educator2 = helper.generateTeacher();
        helper.generateTeacherSchool(educator2.getEntityId(), school2.getEntityId());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidate() {
        setContext(seaStaff, Arrays.asList(SecureRoleRightAccessImpl.SEA_ADMINISTRATOR));
        assertTrue(validator.canValidate(EntityNames.SECTION, true));
        assertTrue(validator.canValidate(EntityNames.SECTION, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }

    @Test
    public void testCanNotValidate() {
        setContext(educator2, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }

    @Test
    public void testSeaAdministratorCanSeeSectionAtSchool() {
        setContext(seaStaff, Arrays.asList(SecureRoleRightAccessImpl.SEA_ADMINISTRATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section1.getEntityId());
        sectionIds.add(section2.getEntityId());
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testLeaAdministratorCanSeeSectionAtSchool() {
        setContext(lea1Staff, Arrays.asList(SecureRoleRightAccessImpl.LEA_ADMINISTRATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section1.getEntityId());
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testLeaAdministratorCanNotSeeSectionAtSchool() {
        setContext(lea2Staff, Arrays.asList(SecureRoleRightAccessImpl.LEA_ADMINISTRATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section1.getEntityId());
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testSchoolAdministratorCanSeeSectionAtSchool() {
        setContext(school1Staff, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section1.getEntityId());
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testSchoolAdministratorCanNotSeeSectionAtSchool() {
        setContext(school2Staff, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section1.getEntityId());
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testEducatorCanSeeSectionAtSchool() {
        setContext(educator1, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section1.getEntityId());
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

    @Test
    public void testEducatorCanNotSeeSectionAtSchool() {
        setContext(educator2, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add(section2.getEntityId());
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));

        sectionIds.add(section1.getEntityId());
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }
}

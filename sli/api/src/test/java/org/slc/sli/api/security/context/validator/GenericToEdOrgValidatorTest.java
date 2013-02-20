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
 * Unit tests for staff/teacher --> education organization context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericToEdOrgValidatorTest {

    @Autowired
    private GenericToEdOrgValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    Set<String> schoolIds;

    Entity sea = null;
    Entity lea = null;
    Entity school = null;
    Entity teacher = null;
    Entity staff = null;

    private void setTeacherContext(String edOrgId) {
        String user = "fake teacher";
        String fullName = "Fake Teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, teacher, edOrgId);
    }

    private void setStaffContext(String edOrgId) {
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, edOrgId);
    }

    @Before
    public void setUp() throws Exception {
        sea = helper.generateEdorgWithParent(null);
        lea = helper.generateEdorgWithParent(sea.getEntityId());
        school = helper.generateEdorgWithParent(lea.getEntityId());

        teacher = helper.generateTeacher();
        staff = helper.generateStaff();

        schoolIds = new HashSet<String>();
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        schoolIds.clear();
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.SCHOOL, false));
        assertTrue(validator.canValidate(EntityNames.SCHOOL, true));
        assertTrue(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, false));
        assertTrue(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, true));
    }

    @Test
    public void testCanNotValidate() {
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
    }

    @Test
    public void testCanValidateTeacherAtSchool() {
        setTeacherContext(school.getEntityId());
        schoolIds.add(school.getEntityId());
        helper.generateTeacherSchool(teacher.getEntityId(), school.getEntityId());
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertTrue(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanValidateStaffAtSchool() {
        setStaffContext(school.getEntityId());
        schoolIds.add(school.getEntityId());
        helper.generateStaffEdorg(staff.getEntityId(), school.getEntityId(), false);
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertTrue(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanValidateStaffFromStateDownToSchool() {
        setStaffContext(sea.getEntityId());
        helper.generateStaffEdorg(staff.getEntityId(), sea.getEntityId(), false);
        schoolIds.add(school.getEntityId());
        schoolIds.add(lea.getEntityId());
        schoolIds.add(sea.getEntityId());
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertTrue(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanNotValidateTeacherAccessingParentEdOrg() {
        setTeacherContext(school.getEntityId());
        helper.generateTeacherSchool(teacher.getEntityId(), school.getEntityId());
        schoolIds.add(school.getEntityId());
        schoolIds.add(lea.getEntityId());
        schoolIds.add(sea.getEntityId());
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanValidateTeacherAtSchoolIntersection() {
        setTeacherContext(school.getEntityId());
        for (int i = 0; i < 10; ++i) {
            Entity newSchool = helper.generateEdorgWithParent(lea.getEntityId());
            injector.addToAuthorizingEdOrgs(newSchool.getEntityId());
            helper.generateTeacherSchool(teacher.getEntityId(), newSchool.getEntityId());
            schoolIds.add(newSchool.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertTrue(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));

        Entity newSchool = helper.generateEdorgWithParent(lea.getEntityId());
        injector.addToAuthorizingEdOrgs(newSchool.getEntityId());
        schoolIds.add(newSchool.getEntityId());
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanValidateStaffAtSchoolIntersection() {
        setStaffContext(sea.getEntityId());
        for (int i = 0; i < 10; ++i) {
            Entity newSchool = helper.generateEdorgWithParent(lea.getEntityId());
            injector.addToAuthorizingEdOrgs(newSchool.getEntityId());
            helper.generateStaffEdorg(staff.getEntityId(), newSchool.getEntityId(), false);
            schoolIds.add(newSchool.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertTrue(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
        Entity newSchool = helper.generateEdorgWithParent(lea.getEntityId());
        injector.addToAuthorizingEdOrgs(newSchool.getEntityId());
        schoolIds.add(newSchool.getEntityId());
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanNotValidateTeacherNotAtSchool() {
        setTeacherContext(school.getEntityId());
        schoolIds.add(school.getEntityId());
        school = helper.generateEdorgWithParent(null);
        helper.generateTeacherSchool(teacher.getEntityId(), school.getEntityId());
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanNotValidateStaffNotAtSchool() {
        setStaffContext(school.getEntityId());
        schoolIds.add(school.getEntityId());
        school = helper.generateEdorgWithParent(null);
        helper.generateStaffEdorg(staff.getEntityId(), school.getEntityId(), false);
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }

    @Test
    public void testCanNotValidateExpiredStaff() {
        setStaffContext(school.getEntityId());
        schoolIds.add(school.getEntityId());
        helper.generateStaffEdorg(staff.getEntityId(), school.getEntityId(), true);
        assertFalse(validator.validate(EntityNames.SCHOOL, schoolIds));
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds));
    }
}

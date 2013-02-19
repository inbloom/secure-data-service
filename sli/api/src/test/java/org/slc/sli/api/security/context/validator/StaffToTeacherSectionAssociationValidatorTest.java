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
 * Unit tests for staff --> teacher section association context validator.
 *
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToTeacherSectionAssociationValidatorTest {

    @Autowired
    private StaffToTeacherSectionAssociationValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    Entity sea = null;
    Entity lea = null;
    Entity school = null;
    Entity section = null;
    Entity educator = null;
    Entity seaStaff = null;
    Entity leaStaff = null;
    Entity schoolStaff = null;
    Entity teacherSectionAssociation = null;

    private void setContext(Entity actor, List<String> roles) {
        String user = "fake actor";
        String fullName = "Fake Actor";
        injector.setCustomContext(user, fullName, "MERPREALM", roles, actor, "111");
    }

    @Before
    public void setUp() {
        sea = helper.generateEdorgWithParent(null);
        lea = helper.generateEdorgWithParent(sea.getEntityId());
        school = helper.generateEdorgWithParent(lea.getEntityId());
        section = helper.generateSection(school.getEntityId());

        seaStaff = helper.generateStaff();
        helper.generateStaffEdorg(seaStaff.getEntityId(), sea.getEntityId(), false);

        leaStaff = helper.generateStaff();
        helper.generateStaffEdorg(leaStaff.getEntityId(), lea.getEntityId(), false);

        schoolStaff = helper.generateStaff();
        helper.generateStaffEdorg(schoolStaff.getEntityId(), school.getEntityId(), false);

        educator = helper.generateTeacher();
        helper.generateTeacherSchool(educator.getEntityId(), school.getEntityId());
        teacherSectionAssociation = helper.generateTSA(educator.getEntityId(), section.getEntityId(), false);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidateStaffToTeacherSectionAssociation() throws Exception {
    	setContext(seaStaff, Arrays.asList(SecureRoleRightAccessImpl.SEA_ADMINISTRATOR));
        assertTrue(validator.canValidate(EntityNames.TEACHER_SECTION_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.TEACHER_SECTION_ASSOCIATION, true));
    }

    @Test
    public void testDeniedStaffToOtherEntities() throws Exception {
    	setContext(seaStaff, Arrays.asList(SecureRoleRightAccessImpl.SEA_ADMINISTRATOR));
        assertFalse(validator.canValidate(EntityNames.STUDENT, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT, false));
        assertFalse(validator.canValidate(EntityNames.TEACHER, true));
        assertFalse(validator.canValidate(EntityNames.TEACHER, false));
    }

    @Test
    public void testNullTeacherSectionAssociation() throws Exception {
        assertFalse(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, null));
    }

    @Test
    public void testEmptyTeacherSectionAssociation() throws Exception {
        Set<String> teacherSectionAssociations = new HashSet<String>();
        assertFalse(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    @Test
    public void testSeaAdministratorCanGetAccessToTeacherSectionAssociation() throws Exception {
        setContext(seaStaff, Arrays.asList(SecureRoleRightAccessImpl.SEA_ADMINISTRATOR));
        Set<String> teacherSectionAssociations = new HashSet<String>();
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        assertTrue(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    @Test
    public void testLeaAdministratorCanGetAccessToTeacherSectionAssociation() throws Exception {
        setContext(leaStaff, Arrays.asList(SecureRoleRightAccessImpl.LEA_ADMINISTRATOR));
        Set<String> teacherSectionAssociations = new HashSet<String>();
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        assertTrue(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    @Test
    public void testSchoolAdministratorCanGetAccessToTeacherSectionAssociation() throws Exception {
        setContext(schoolStaff, Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR));
        Set<String> teacherSectionAssociations = new HashSet<String>();
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        assertTrue(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    @Test
    public void testEducatorCanNotAccessTeacherSectionAssociation() throws Exception {
        setContext(educator, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR));
        Set<String> teacherSectionAssociations = new HashSet<String>();
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        assertFalse(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }
}

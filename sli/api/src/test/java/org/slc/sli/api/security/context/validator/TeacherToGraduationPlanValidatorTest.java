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

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class TeacherToGraduationPlanValidatorTest {

    @Autowired
    TeacherToGraduationPlanValidator validator;

    @Autowired
    PagingRepositoryDelegate<Entity> repo;

    @Autowired
    ValidatorTestHelper helper;

    private Set<String> graduationPlanIds;

    @Before
    public void setUp() throws Exception {
        helper.setUpTeacherContext();
        graduationPlanIds = new HashSet<String>();
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.GRADUATION_PLAN, new NeutralQuery());
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        repo.deleteAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        graduationPlanIds.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.GRADUATION_PLAN, false));
        assertTrue(validator.canValidate(EntityNames.GRADUATION_PLAN, true));
    }

    @Test
    public void testCanValidateSingleGraduationPlan() {
        Entity edorg = helper.generateEdorgWithParent(null);
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, edorg.getEntityId());
        Entity graduationPlan = helper.generateGraduationPlan(edorg.getEntityId());
        graduationPlanIds.add(graduationPlan.getEntityId());
        assertTrue(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
    }

    @Test
    public void testCanNotValidateInvalidGraduationPlan() {
        Entity edorg = helper.generateEdorgWithParent(null);
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, edorg.getEntityId());
        Entity graduationPlan = helper.generateGraduationPlan(null);
        graduationPlanIds.add(graduationPlan.getEntityId());
        assertFalse(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
    }

    @Test
    public void testCanSeeGraduationPlanInHeirarchy() {
        Entity lea = helper.generateEdorgWithParent(null);
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school.getEntityId());
        Entity graduationPlan = helper.generateGraduationPlan(lea.getEntityId());
        graduationPlanIds.add(graduationPlan.getEntityId());
        assertTrue(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
    }

    @Test
    public void testCanSeeGraduationPlanAtOtherSchoolBasedOnStudent() {
        boolean isExpired = false;
        Entity school1 = helper.generateEdorgWithParent(null);
        Entity school2 = helper.generateEdorgWithParent(null);
        Entity graduationPlan = helper.generateGraduationPlan(school2.getEntityId());
        Entity section = helper.generateSection(school1.getEntityId());
        String student = helper.generateStudentAndStudentSchoolAssociation("student1", school2.getEntityId(), graduationPlan.getEntityId(), isExpired);
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
        helper.generateTSA(ValidatorTestHelper.STAFF_ID, section.getEntityId(), isExpired);
        helper.generateSSA(student, section.getEntityId(), isExpired);
        graduationPlanIds.add(graduationPlan.getEntityId());
        assertTrue(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
    }

    @Test
    public void testCanNotSeeGraduationPlanAtOtherSchoolBasedOnStudent() {
        boolean isExpired = false;
        Entity school1 = helper.generateEdorgWithParent(null);
        Entity school2 = helper.generateEdorgWithParent(null);
        Entity graduationPlan = helper.generateGraduationPlan(school2.getEntityId());
        Entity section = helper.generateSection(school1.getEntityId());
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
        helper.generateTSA(ValidatorTestHelper.STAFF_ID, section.getEntityId(), isExpired);
        graduationPlanIds.add(graduationPlan.getEntityId());
        assertFalse(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
    }

    @Test
    public void testValidateIntersectionRules() {
        Entity school1 = helper.generateEdorgWithParent(null);
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
        for (int i = 0; i < 10; ++i) {
            Entity plan = helper.generateGraduationPlan(school1.getEntityId());
            graduationPlanIds.add(plan.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
        // Disconnected graduation plan
        Entity school2 = helper.generateEdorgWithParent(null);
        Entity graduationPlan = helper.generateGraduationPlan(school2.getEntityId());
        graduationPlanIds.add(graduationPlan.getEntityId());
        assertFalse(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
        // Reconnected graduation plan
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school2.getEntityId());
        assertTrue(validator.validate(EntityNames.GRADUATION_PLAN, graduationPlanIds));
    }
}

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
import org.junit.Ignore;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests for teacher --> student context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TeacherToStudentValidatorTest {

    private static final String ED_ORG_ID = "111";

    private static final String TEACHER_ID = "1";
    private static final String SECTION_ID = "SECTION99";


    @Autowired
    private TeacherToStudentValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> mockRepo;

    @Autowired
    private ValidatorTestHelper helper;

    private Set<String> studentIds;
    private String programId;

    @Before
    public void setUp() {
        // Set up the principal
        String user = "fake teacher";
        String fullName = "Fake Teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("teacher");
        Mockito.when(entity.getEntityId()).thenReturn(TEACHER_ID);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, ED_ORG_ID);

        studentIds = new HashSet<String>();
        programId = helper.generateProgram().getEntityId();
    }

    @After
    public void tearDown() {
        mockRepo.deleteAll(EntityNames.TEACHER_SECTION_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT_SECTION_ASSOCIATION, new NeutralQuery());
        cleanProgramCohorts();
        SecurityContextHolder.clearContext();
    }

    private void cleanProgramCohorts() {
        mockRepo.deleteAll(EntityNames.STAFF_COHORT_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT_COHORT_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.COHORT, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.PROGRAM, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
    }

    @Test
    public void testCanValidateTeacherToStudent() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT, false));
    }

    @Test
    public void testCanNotValidateOtherEntities() throws Exception {
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }

    @Test
    public void testCanGetAccessThroughSingleValidStudent() throws Exception {
        helper.generateTSA(TEACHER_ID, "3", false);
        helper.generateSSA("2", "3", false);
        studentIds.add("2");
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughInvalidStudent() throws Exception {
        helper.generateTSA(TEACHER_ID, "-1", false);

        helper.generateSSA("2", "3", false);

        studentIds.add("2");
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanGetAccessThroughManyStudents() throws Exception {

        for (int i = 0; i < 100; ++i) {
            helper.generateTSA(TEACHER_ID, "" + i, false);
        }

        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                helper.generateSSA(String.valueOf(j), "" + i, false);
                studentIds.add(String.valueOf(j));
            }
        }

        assertTrue(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCantGetAccessThroughManyStudents() throws Exception {

        for (int i = 0; i < 2; ++i) {
            helper.generateTSA(TEACHER_ID, String.valueOf(i), false);
        }

        for (int i = 0; i < 2; ++i) {
            for (int j = -1; j > -2; --j) {
                helper.generateSSA(String.valueOf(j), String.valueOf(i), false);
                studentIds.add(String.valueOf(j));
            }
        }

        helper.generateSSA("100", "6", false);
        studentIds.add("100");

        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanGetAccessThroughStudentsWithManySections() throws Exception {

        helper.generateTSA(TEACHER_ID, "0", false);

        for (int i = 0; i < 10; ++i) {
            helper.generateSSA("2", String.valueOf(i), false);
            studentIds.add("2");
        }
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughManyStudents() throws Exception {

        for (int i = 100; i < 200; ++i) {
            helper.generateTSA(TEACHER_ID, "" + i, false);
        }

        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                helper.generateSSA("" + j, "" + i, false);
                studentIds.add("" + j);
            }
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughManyStudentsWithOneFailure() throws Exception {

        for (int i = 0; i < 100; ++i) {
            helper.generateTSA(TEACHER_ID, "" + i, false);
        }

        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                helper.generateSSA("" + j, "" + i, false);
                studentIds.add("" + j);
            }
        }
        helper.generateSSA("-32", "101", false);
        studentIds.add("-32");
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    @Ignore
    // TODO: Fix cohort tests once all those denormilzation hacks are removed
    public void testCanGetAccessThroughValidCohort() throws Exception {
        helper.generateTeacherSchool(TEACHER_ID, ED_ORG_ID);
        String cohortId = helper.generateCohort(ED_ORG_ID).getEntityId();
        helper.generateStaffCohort(TEACHER_ID, cohortId, false, true);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentCohort(i + "", cohortId, false);
            studentIds.add(i + "");
        }
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughExpiredCohort() throws Exception {

        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughDeniedCohort() throws Exception {
        helper.generateTeacherSchool(TEACHER_ID, ED_ORG_ID);
        String cohortId = helper.generateCohort(ED_ORG_ID).getEntityId();
        helper.generateStaffCohort(TEACHER_ID, cohortId, false, false);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentCohort(i + "", cohortId, false);
            studentIds.add(i + "");
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughInvalidCohort() throws Exception {
        helper.generateTeacherSchool(TEACHER_ID, ED_ORG_ID);
        String cohortId = helper.generateCohort(ED_ORG_ID).getEntityId();
        helper.generateStaffCohort(TEACHER_ID, cohortId, false, true);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentCohort(i + "", "" + i * -1, false);
            studentIds.add(i + "");
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    // This test doesn't matter. The rule is if you have a staffCohortAssociation, you an see
    // the cohort regardless of where it is in an edorg hierarchy.
    @Test
    @Ignore
    public void testCanNotGetAccessThroughCohortOutsideOfEdorg() throws Exception {
        helper.generateTeacherSchool(TEACHER_ID, ED_ORG_ID);
        String cohortId = helper.generateCohort("122").getEntityId();
        helper.generateStaffCohort(TEACHER_ID, cohortId, false, true);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentCohort(i + "", cohortId, false);
            studentIds.add(i + "");
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCohortAccessIntersectionRules() throws Exception {
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanGetAccessThroughValidProgram() throws Exception {
        String edOrgId = helper.generateEdorgWithProgram(Arrays.asList(programId)).getEntityId();
        helper.generateTeacherSchool(TEACHER_ID, edOrgId);

        helper.generateStaffProgram(TEACHER_ID, programId, false, true);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentProgram(i + "", programId, false);
            studentIds.add(i + "");
        }
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanAccessStudentsThoughSectionAndProgramAssociations() throws Exception {

        String studentId1 = "STUDENT11";
        String studentId2 = "STUDENT22";

        helper.generateTSA(TEACHER_ID, SECTION_ID, false);
        helper.generateSSA(studentId1, SECTION_ID, false);
        studentIds.add(studentId1);

        String edOrgId = helper.generateEdorgWithProgram(Arrays.asList(programId)).getEntityId();
        helper.generateTeacherSchool(TEACHER_ID, edOrgId);
        helper.generateStaffProgram(TEACHER_ID, programId, false, true);
        helper.generateStudentProgram(studentId2, programId, false);
        studentIds.add(studentId2);

        assertTrue(validator.validate(EntityNames.STUDENT, studentIds));
    }


    @Test
    public void testCanNotGetAccessThroughExpiredProgram() throws Exception {

        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughDeniedProgram() throws Exception {
        String edOrgId = helper.generateEdorgWithProgram(Arrays.asList(programId)).getEntityId();
        helper.generateTeacherSchool(TEACHER_ID, edOrgId);

        helper.generateStaffProgram(TEACHER_ID, programId, false, false);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentProgram(i + "", programId, false);
            studentIds.add(i + "");
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughWithOneDeniedProgram() throws Exception {
        String edOrgId = helper.generateEdorgWithProgram(Arrays.asList(programId)).getEntityId();
        helper.generateTeacherSchool(TEACHER_ID, edOrgId);

        helper.generateStaffProgram(TEACHER_ID, programId, false, true);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentProgram(i + "", programId, false);
            studentIds.add(i + "");
        }

        helper.generateStudentProgram("-32", "101", false);
        studentIds.add("-32");

        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughInvalidProgram() throws Exception {
        String edOrgId = helper.generateEdorgWithProgram(Arrays.asList(programId)).getEntityId();
        helper.generateTeacherSchool(TEACHER_ID, edOrgId);

        helper.generateStaffProgram(TEACHER_ID, programId, false, true);
        for (int i = 0; i < 10; ++i) {
            helper.generateStudentProgram(i + "", "" + i * -1, false);
            studentIds.add(i + "");
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testProgramAccessIntersectionRules() throws Exception {
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds));
    }

}

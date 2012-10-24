/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TeacherToStudentValidatorTest {
    
    private static final String ED_ORG_ID = "111";
    
    private static final String TEACHER_ID = "1";

    @Autowired
    private TeacherToStudentValidator validator;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private PagingRepositoryDelegate<Entity> mockRepo;
    
    private Set<String> studentIds;
    
    private String badDate;

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
        
        badDate = "2001-01-01";

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
        generateTSA(TEACHER_ID, "3", false);
        generateSSA("2", "3", false);
        studentIds.add("2");
        assertTrue(validator.validate(studentIds));
    }
    
    @Test
    public void testCanNotGetAccessThroughInvalidStudent() throws Exception {
        generateTSA(TEACHER_ID, "-1", false);
        
        generateSSA("2", "3", false);
        
        studentIds.add("2");
        assertFalse(validator.validate(studentIds));
    }
    
    @Test
    public void testCanGetAccessThroughManyStudents() throws Exception {

        for (int i = 0; i < 100; ++i) {
            generateTSA(TEACHER_ID, "" + i, false);
        }

        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                generateSSA("" + j, "" + i, false);
                studentIds.add("" + j);
            }
        }

        assertTrue(validator.validate(studentIds));
    }
    
    @Test
    public void testCanGetAccessThroughStudentsWithManySections() throws Exception {
        
        generateTSA(TEACHER_ID, "0", false);

        List<Entity> ssas = new ArrayList<Entity>();
        for (int i = 0; i < 10; ++i) {
            generateSSA("2", "" + i, false);
            studentIds.add("2");
        }
        assertTrue(validator.validate(studentIds));
    }
    
    @Test
    public void testCanNotGetAccessThroughManyStudents() throws Exception {

        for (int i = 100; i < 200; ++i) {
            generateTSA(TEACHER_ID, "" + i, false);
        }

        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                generateSSA("" + j, "" + i, false);
                studentIds.add("" + j);
            }
        }
        assertFalse(validator.validate(studentIds));
    }
    
    @Test
    public void testCanNotGetAccessThroughManyStudentsWithOneFailure() throws Exception {

        for (int i = 0; i < 100; ++i) {
            generateTSA(TEACHER_ID, "" + i, false);
        }

        for (int i = 0; i < 100; ++i) {
            for (int j = -1; j > -31; --j) {
                generateSSA("" + j, "" + i, false);
                studentIds.add("" + j);
            }
        }
        generateSSA("-32", "101", false);
        studentIds.add("-32");
        assertFalse(validator.validate(studentIds));
    }
    
    // @Test
    // public void testCanGetAccessThroughValidCohort() throws Exception {
    // generateTeacherSchool(TEACHER_ID, ED_ORG_ID);
    // String cohortId = generateCohort(ED_ORG_ID).getEntityId();
    // generateStaffCohort(TEACHER_ID, cohortId, false, true);
    // for (int i = 0; i < 10; ++i) {
    // generateStudentCohort(i + "", cohortId, false);
    // studentIds.add(i + "");
    // }
    //
    // assertTrue(validator.validate(studentIds));
    // }
    //
    //
    //
    // @Test
    // public void testCanNotGetAccessThroughExpiredCohort() throws Exception {
    // assertFalse(validator.validate(studentIds));
    // }
    //
    // @Test
    // public void testCanNotGetAccessThroughDeniedCohort() throws Exception {
    // assertFalse(validator.validate(studentIds));
    // }
    //
    // @Test
    // public void testCanNotGetAccessThroughInvalidCohort() throws Exception {
    // assertFalse(validator.validate(studentIds));
    // }
    //
    // @Test
    // public void testCanNotGetAccessThroughOutsideOfEdorg() throws Exception {
    // assertFalse(validator.validate(studentIds));
    // }
    //
    // @Test
    // public void testCohortAccessIntersectionRules() throws Exception {
    // assertFalse(validator.validate(studentIds));
    // }

    private void generateSSA(String studentId, String sectionId, boolean isExpired) {
        Map<String, Object> ssaBody = new HashMap<String, Object>();
        ssaBody.put(ParameterConstants.SECTION_ID, sectionId);
        ssaBody.put(ParameterConstants.STUDENT_ID, studentId);
        if (isExpired) {
            ssaBody.put(ParameterConstants.END_DATE, badDate);
        }
        mockRepo.create(EntityNames.STUDENT_SECTION_ASSOCIATION, ssaBody);
    }
    
    private void generateTeacherSchool(String teacherId, String edorgId) {
        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put(ParameterConstants.TEACHER_ID, teacherId);
        tsaBody.put(ParameterConstants.SCHOOL_ID, edorgId);
        
        mockRepo.create(EntityNames.TEACHER_SCHOOL_ASSOCIATION, tsaBody);
    }

    private void generateTSA(String teacherId, String sectionId, boolean isExpired) {
        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put(ParameterConstants.TEACHER_ID, teacherId);
        tsaBody.put(ParameterConstants.SECTION_ID, sectionId);
        if (isExpired) {
            tsaBody.put(ParameterConstants.END_DATE, badDate);
        }
        mockRepo.create(EntityNames.TEACHER_SECTION_ASSOCIATION, tsaBody);
    }
    
    private Entity generateCohort(String edOrgId) {
        Map<String, Object> cohortBody = new HashMap<String, Object>();
        cohortBody.put(ParameterConstants.EDUCATION_ORGANIZATION_ID, edOrgId);
        
        return mockRepo.create(EntityNames.COHORT, cohortBody);
    }
    
    private void generateStaffCohort(String teacherId, String cohortId, boolean isExpired, boolean studentAccess) {
        Map<String, Object> staffCohort = new HashMap<String, Object>();
        staffCohort.put(ParameterConstants.STAFF_ID, teacherId);
        staffCohort.put(ParameterConstants.COHORT_ID, cohortId);
        if (isExpired) {
            staffCohort.put(ParameterConstants.END_DATE, getBadDate());
        }
        staffCohort.put(ParameterConstants.STUDENT_RECORD_ACCESS, studentAccess);
        
        mockRepo.create(EntityNames.STAFF_COHORT_ASSOCIATION, staffCohort);
        
    }
    
    private void generateStudentCohort(String studentId, String cohortId, boolean isExpired) {
        Map<String, Object> studentCohort = new HashMap<String, Object>();
        studentCohort.put(ParameterConstants.STUDENT_ID, studentId);
        studentCohort.put(ParameterConstants.COHORT_ID, cohortId);
        if (isExpired) {
            studentCohort.put(ParameterConstants.END_DATE, getBadDate());
        }
        
        mockRepo.create(EntityNames.STUDENT_COHORT_ASSOCIATION, studentCohort);
        
    }
    
    private String getBadDate() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime past = DateTime.now().minusYears(10);
        return past.toString(fmt);
    }
}

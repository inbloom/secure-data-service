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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Unit tests for staff --> student context validator.
 *
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToStudentValidatorTest {

    private static final String STAFF_ID = "1";
    private static final Boolean IS_EXPIRED = true;
    private static final Boolean NOT_EXPIRED = false;
    private static final int N_TEST_EDORGS = 20;
    private Entity edOrg = null;
    private String ED_ORG_ID;
    private String ED_ORG_ID_2;
    private String DERP;
    private String [] edorgArray = new String[N_TEST_EDORGS];

    @Autowired
    private StaffToStudentValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> mockRepo;

    @Autowired
    private ValidatorTestHelper helper;

    private Set<String> studentIds;

    private GenericToProgramValidator mockProgramValidator;
    private GenericToCohortValidator mockCohortValidator;

    @Before
    public void setUp() {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.LEADER);
        edOrg = helper.generateSchoolEdOrg(null);
        ED_ORG_ID = edOrg.getEntityId();

        ED_ORG_ID_2 =  helper.generateSchoolEdOrg(null).getEntityId();
        DERP =  helper.generateSchoolEdOrg(null).getEntityId();

        for ( int i = 0; i < N_TEST_EDORGS; i++ ) {
            edorgArray[ i ] = helper.generateSchoolEdOrg(null).getEntityId();
        }

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn(STAFF_ID);
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, ED_ORG_ID);

        studentIds = new HashSet<String>();
        mockProgramValidator = Mockito.mock(GenericToProgramValidator.class);
        mockCohortValidator = Mockito.mock(GenericToCohortValidator.class);
        validator.setProgramValidator(mockProgramValidator);
        validator.setCohortValidator(mockCohortValidator);

        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
    }

    @After
    public void tearDown() {
        mockRepo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT_SCHOOL_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT_COHORT_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new NeutralQuery());
        mockRepo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        SecurityContextHolder.clearContext();
        mockProgramValidator = null;
        mockCohortValidator = null;
    }

    @Test
    public void testCanValidateStaffToStudents() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT, false));
    }

    @Test
    public void testCanNotValidateOtherEntities() throws Exception {
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }


    @Test
    public void testCanGetAccessThroughSingleValidAssociation() throws Exception {

        helper.generateStaffEdorg(STAFF_ID, ED_ORG_ID, NOT_EXPIRED);
        String studentId = helper.generateStudentAndStudentSchoolAssociation("2", edOrg.getEntityId(), NOT_EXPIRED);
        studentIds.add(studentId);
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughInvalidAssociation() throws Exception {
        helper.generateStaffEdorg(STAFF_ID, ED_ORG_ID, NOT_EXPIRED);
        String studentId = helper.generateStudentAndStudentSchoolAssociation("2", ED_ORG_ID_2, NOT_EXPIRED);
        studentIds.add(studentId);
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessDueToExpiration() throws Exception {
        helper.generateStaffEdorg(STAFF_ID, ED_ORG_ID, NOT_EXPIRED);
        String studentId = helper.generateStudentAndStudentSchoolAssociation("2", ED_ORG_ID, IS_EXPIRED);
        studentIds.add(studentId);
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanGetAccessThroughManyStudents() throws Exception {
        Set<String> expectedIds = new HashSet<String>();
        for (int i = 0; i < N_TEST_EDORGS/2; ++i) {
            helper.generateStaffEdorg(STAFF_ID, edorgArray[ i ], NOT_EXPIRED);
            injector.addToAuthorizingEdOrgs( edorgArray[ i  ] );
        }

        for (int i = 0; i < N_TEST_EDORGS/2; ++i) {
            for (int j = -1; j > -31; --j) {
                String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j),
                        edorgArray[ i ], NOT_EXPIRED);
                studentIds.add(studentId);
                expectedIds.add(studentId);
            }
        }

        assertTrue(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));

        for (int i = N_TEST_EDORGS/2; i < N_TEST_EDORGS; ++i) {
            for (int j = -1; j > -31; --j) {
                String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j),
                        edorgArray[ i ], NOT_EXPIRED);
                studentIds.add(studentId);
            }
        }

        Assert.assertEquals(expectedIds, validator.validate(EntityNames.STUDENT, studentIds));

    }

    @Test
    public void testCanNotGetAccessThroughManyStudents() throws Exception {
        for (int i = N_TEST_EDORGS/2; i < N_TEST_EDORGS; ++i) {
            helper.generateStaffEdorg(STAFF_ID, edorgArray[ i ], NOT_EXPIRED);
            injector.addToAuthorizingEdOrgs( edorgArray[ i  ] );
        }

        for (int i = 0; i < N_TEST_EDORGS/2; ++i) {
            for (int j = -1; j > -31; --j) {
                String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j),
                        edorgArray[ i ], NOT_EXPIRED);
                studentIds.add(studentId);
            }
        }
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughManyStudentsWithOneFailure() throws Exception {
        for (int i = 0; i < N_TEST_EDORGS/2; ++i) {
            helper.generateStaffEdorg(STAFF_ID, edorgArray[ i ], NOT_EXPIRED);
            injector.addToAuthorizingEdOrgs( edorgArray[ i  ] );
        }

        Set<String> expected = new HashSet<String>();
        for (int i = 0; i < N_TEST_EDORGS/2; ++i) {
            for (int j = -1; j > -31; --j) {
                String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j),
                        edorgArray[ i ], NOT_EXPIRED);
                studentIds.add(studentId);
                expected.add(studentId);
            }
        }

        String anotherEdOrg = helper.generateSchoolEdOrg(null).getEntityId();
        String studentId = helper.generateStudentAndStudentSchoolAssociation("-32", anotherEdOrg, NOT_EXPIRED);
        studentIds.add(studentId);
        Assert.assertEquals(expected, validator.validate(EntityNames.STUDENT, studentIds));
    }

    @Test
    public void testIsLhsBeforeRhs() {
        //Same date, different times
        DateTime today = new DateTime(2010, 10, 10, 10, 10, 10);
        DateTime today2 = new DateTime(2010, 10, 10, 12, 12, 12);
        DateTime old = new DateTime(2000, 1, 1, 1, 1, 1);
        assertTrue(validator.isLhsBeforeRhs(today, today2));    //dates are equal
        assertTrue(validator.isLhsBeforeRhs(today2, today));

        assertTrue(validator.isLhsBeforeRhs(old, today));
        assertFalse(validator.isLhsBeforeRhs(today, old));
    }

    public void testCanGetAccessThroughProgram() {
        helper.generateStaffEdorg(STAFF_ID, DERP, NOT_EXPIRED);
        for (int j = -1; j > -31; --j) {
            String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j), ED_ORG_ID,
                    NOT_EXPIRED);
            studentIds.add(studentId);
        }
        Entity program = helper.generateStudentProgram("Merp", "Derp", false);
        Mockito.when(mockProgramValidator.validate(Mockito.eq(EntityNames.PROGRAM), Mockito.anySet())).thenReturn(new HashSet<String>(Arrays.asList(program.getEntityId())));
        studentIds.add("Merp");
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanGetAccessThroughCohort() {
        Entity cohort = helper.generateStudentCohort("Merp", "Derp", false);
        Mockito.when(mockCohortValidator.validate(Mockito.eq(EntityNames.COHORT), Mockito.anySet())).thenReturn(new HashSet<String>(Arrays.asList(cohort.getEntityId())));
        helper.generateStaffEdorg(STAFF_ID, DERP, NOT_EXPIRED);
        for (int j = -1; j > -31; --j) {
            String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j), ED_ORG_ID,
                    NOT_EXPIRED);
            studentIds.add(studentId);
        }
        assertTrue(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughExpiredCohort() {
        Entity cohort = helper.generateStudentCohort("Merp", "Derp", true);
        Mockito.when(mockCohortValidator.validate(Mockito.eq(EntityNames.COHORT), Mockito.anySet())).thenReturn(new HashSet<String>(Arrays.asList(cohort.getEntityId())));
        helper.generateStaffEdorg(STAFF_ID, DERP, NOT_EXPIRED);
        for (int j = -1; j > -31; --j) {
            String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j), ED_ORG_ID,
                    NOT_EXPIRED);
            studentIds.add(studentId);
        }
        studentIds.add("Merp");
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughExpiredProgram() {
        Entity program = helper.generateStudentProgram("Merp", DERP, true);
    	Mockito.when(mockProgramValidator.validate(Mockito.eq(EntityNames.PROGRAM), Mockito.anySet())).thenReturn(new HashSet<String>(Arrays.asList(program.getEntityId())));
        helper.generateStaffEdorg(STAFF_ID, DERP, NOT_EXPIRED);
        for (int j = -1; j > -31; --j) {
            String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j), ED_ORG_ID,
                    NOT_EXPIRED);
            studentIds.add(studentId);
        }
        studentIds.add("Merp");
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughInvalidProgram() {
        Entity program = helper.generateStudentProgram("Merp", "Derp", false);
    	Mockito.when(mockProgramValidator.validate(Mockito.eq(EntityNames.PROGRAM), Mockito.anySet())).thenReturn(new HashSet<String>(Arrays.asList(program.getEntityId())));
        helper.generateStaffEdorg(STAFF_ID, DERP, NOT_EXPIRED);
        for (int j = -1; j > -31; --j) {
            String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j), ED_ORG_ID,
                    NOT_EXPIRED);
            studentIds.add(studentId);
        }
        studentIds.add("Merp");
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testCanNotGetAccessThroughInvalidCohort() {
        Entity cohort = helper.generateStudentCohort("Merp", "Derp", false);
        Mockito.when(
                mockCohortValidator.validate(Mockito.eq(EntityNames.COHORT), Mockito.anySet())).thenReturn(new HashSet<String>(Arrays.asList(cohort.getEntityId())));
        helper.generateStaffEdorg(STAFF_ID, DERP, NOT_EXPIRED);
        for (int j = -1; j > -31; --j) {
            String studentId = helper.generateStudentAndStudentSchoolAssociation(String.valueOf(j), ED_ORG_ID,
                    NOT_EXPIRED);
            studentIds.add(studentId);
        }
        studentIds.add("Merp");
        assertFalse(validator.validate(EntityNames.STUDENT, studentIds).equals(studentIds));
    }

    @Test
    public void testGetValidWithSomeValid() {
        StaffToStudentValidator mock = Mockito.spy(validator);
        Mockito.doReturn(new HashSet<String>(Arrays.asList("1"))).when(mock).validate(Mockito.eq(EntityNames.STUDENT), Mockito.eq(new HashSet<String>(Arrays.asList("1"))));
        Mockito.doReturn(new HashSet<String>(Arrays.asList("2"))).when(mock).validate(Mockito.eq(EntityNames.STUDENT), Mockito.eq(new HashSet<String>(Arrays.asList("2"))));
        Mockito.doReturn(new HashSet<String>(Arrays.asList("3"))).when(mock).validate(Mockito.eq(EntityNames.STUDENT), Mockito.eq(new HashSet<String>(Arrays.asList("3"))));
        Mockito.doReturn(new HashSet<String>(Arrays.asList("1", "2", "3"))).when(mock).validate(Mockito.eq(EntityNames.STUDENT), Mockito.eq(new HashSet<String>(Arrays.asList("1", "2", "3", "4"))));
        Set<String> validated = mock.getValid(EntityNames.STUDENT, new HashSet<String>(Arrays.asList("1", "2", "3", "4")));
        validated.removeAll(new HashSet<String>(Arrays.asList("1", "2", "3")));
        // has to have only 1,2,3
        assertTrue(validated.isEmpty());
    }

    @Test
    public void testGetValidWithAllValid() {
        StaffToStudentValidator mock = Mockito.spy(validator);
        Mockito.doReturn(new HashSet<String>(Arrays.asList("5", "6", "7", "8"))).when(mock).validate(Mockito.eq(EntityNames.STUDENT), Mockito.eq(new HashSet<String>(Arrays.asList("5", "6", "7", "8"))));
        HashSet<String> input = new HashSet<String>(Arrays.asList("5", "6", "7", "8"));
        Set<String> validated = mock.getValid(EntityNames.STUDENT, input);
        assertEquals(4, validated.size());
        assertTrue(validated.containsAll(input));
    }

    //DE2400
    @Test
    public void testNullEnddate() {
        Map<String, Object> body = new HashMap<String, Object>(1);
        body.put(ParameterConstants.END_DATE, null);
        assertFalse(validator.isFieldExpired(body, ParameterConstants.END_DATE, true));
    }

}

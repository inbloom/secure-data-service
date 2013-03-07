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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Unit tests for staff --> teacher school association context validator.
 *
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToTeacherSchoolAssociationValidatorTest {

    @Autowired
    private StaffToTeacherSchoolAssociationValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    private PagingRepositoryDelegate<Entity> mockRepo;
    private GenericToEdOrgValidator staffToSchoolValidator;
    private Set<String> schoolIds;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        schoolIds = new HashSet<String>();
        mockRepo = Mockito.mock(PagingRepositoryDelegate.class);
        staffToSchoolValidator = Mockito.mock(GenericToEdOrgValidator.class);

        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn("1");
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, "123");

        validator.setRepo(mockRepo);
        validator.setStaffToEdOrgValidator(staffToSchoolValidator);
    }

    @After
    public void tearDown() {
        mockRepo = null;
        staffToSchoolValidator = null;
        schoolIds.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidateStaffToTeacherSchoolAssociation() throws Exception {
        assertTrue(validator.canValidate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, true));
    }

    @Test
    public void testDeniedStaffToOtherEntities() throws Exception {
        assertFalse(validator.canValidate(EntityNames.STUDENT, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT, false));
        assertFalse(validator.canValidate(EntityNames.TEACHER, true));
        assertFalse(validator.canValidate(EntityNames.TEACHER, false));
    }

    @Test
    public void testNullTeacherSchoolAssociation() throws Exception {
        assertFalse(validator.validate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, null));
    }

    @Test
    public void testEmptyTeacherSchoolAssociation() throws Exception {
        Set<String> teacherSchoolAssociations = new HashSet<String>();
        assertFalse(validator.validate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, teacherSchoolAssociations));
    }

    @Test
    public void testCanGetAccessToTeacherSchoolAssociation() throws Exception {
        Set<String> teacherSchoolAssociations = new HashSet<String>();
        Map<String, Object> association = buildTeacherSchoolAssociation("teacher123", "school123");
        Entity teacherSchoolAssociation = new MongoEntity(EntityNames.TEACHER_SCHOOL_ASSOCIATION, association);
        teacherSchoolAssociations.add(teacherSchoolAssociation.getEntityId());
        schoolIds.add("school123");

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(teacherSchoolAssociation));

        Mockito.when(staffToSchoolValidator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds)).thenReturn(true);
        assertTrue(validator.validate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, teacherSchoolAssociations));
    }

    @Test
    public void testCanNotGetAccessToTeacherSchoolAssociationDueToBadLookup() throws Exception {
        Set<String> teacherSchoolAssociations = new HashSet<String>();
        Map<String, Object> association = buildTeacherSchoolAssociation("teacher123", "school123");
        Entity teacherSchoolAssociation = new MongoEntity(EntityNames.TEACHER_SCHOOL_ASSOCIATION, association);
        teacherSchoolAssociations.add(teacherSchoolAssociation.getEntityId());
        schoolIds.add("school123");

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(new ArrayList<Entity>());

        assertFalse(validator.validate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, teacherSchoolAssociations));
    }

    @Test
    public void testCanNotGetAccessToTeacherSchoolAssociation() throws Exception {
        Set<String> teacherSchoolAssociations = new HashSet<String>();
        Map<String, Object> association = buildTeacherSchoolAssociation("teacher123", "school123");
        Entity teacherSchoolAssociation = new MongoEntity(EntityNames.TEACHER_SCHOOL_ASSOCIATION, association);
        teacherSchoolAssociations.add(teacherSchoolAssociation.getEntityId());
        schoolIds.add("school123");

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(teacherSchoolAssociation));

        Mockito.when(staffToSchoolValidator.validate(EntityNames.EDUCATION_ORGANIZATION, schoolIds)).thenReturn(false);
        assertFalse(validator.validate(EntityNames.TEACHER_SCHOOL_ASSOCIATION, teacherSchoolAssociations));
    }

    private Map<String, Object> buildTeacherSchoolAssociation(String teacher, String school) {
        Map<String, Object> association = new HashMap<String, Object>();
        association.put("teacherId", teacher);
        association.put("schoolId", school);
        association.put("programAssignment", "Regular Education");
        return association;
    }
}

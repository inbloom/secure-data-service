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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;

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
    private SecurityContextInjector injector;

    private PagingRepositoryDelegate<Entity> mockRepo;
    private StaffToSectionValidator staffToSectionValidator;
    private Set<String> sectionIds;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        sectionIds = new HashSet<String>();
        mockRepo = Mockito.mock(PagingRepositoryDelegate.class);
        staffToSectionValidator = Mockito.mock(StaffToSectionValidator.class);

        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn("1");
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, "123");

        validator.setRepo(mockRepo);
        validator.setStaffToSectionValidator(staffToSectionValidator);
    }

    @After
    public void tearDown() {
        mockRepo = null;
        staffToSectionValidator = null;
        sectionIds.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidateStaffToTeacherSectionAssociation() throws Exception {
        assertTrue(validator.canValidate(EntityNames.TEACHER_SECTION_ASSOCIATION, false));
        assertTrue(validator.canValidate(EntityNames.TEACHER_SECTION_ASSOCIATION, true));
    }

    @Test
    public void testDeniedStaffToOtherEntities() throws Exception {
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
    public void testCanGetAccessToTeacherSectionAssociation() throws Exception {
        Set<String> teacherSectionAssociations = new HashSet<String>();
        Map<String, Object> association = buildTeacherSectionAssociation("teacher123", "section123");
        Entity teacherSectionAssociation = new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, association);
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        sectionIds.add("section123");

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(teacherSectionAssociation));

        Mockito.when(staffToSectionValidator.validate(EntityNames.SECTION, sectionIds)).thenReturn(true);
        assertTrue(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    @Test
    public void testCanNotGetAccessToTeacherSchoolAssociationDueToBadLookup() throws Exception {
        Set<String> teacherSectionAssociations = new HashSet<String>();
        Map<String, Object> association = buildTeacherSectionAssociation("teacher123", "section123");
        Entity teacherSectionAssociation = new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, association);
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        sectionIds.add("section123");

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(new ArrayList<Entity>());

        assertFalse(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    @Test
    public void testCanNotGetAccessToTeacherSchoolAssociation() throws Exception {
        Set<String> teacherSectionAssociations = new HashSet<String>();
        Map<String, Object> association = buildTeacherSectionAssociation("teacher123", "section123");
        Entity teacherSectionAssociation = new MongoEntity(EntityNames.TEACHER_SECTION_ASSOCIATION, association);
        teacherSectionAssociations.add(teacherSectionAssociation.getEntityId());
        sectionIds.add("section123");

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(teacherSectionAssociation));

        Mockito.when(staffToSectionValidator.validate(EntityNames.SECTION, sectionIds)).thenReturn(false);
        assertFalse(validator.validate(EntityNames.TEACHER_SECTION_ASSOCIATION, teacherSectionAssociations));
    }

    private Map<String, Object> buildTeacherSectionAssociation(String teacher, String section) {
        Map<String, Object> association = new HashMap<String, Object>();
        association.put("teacherId", teacher);
        association.put("sectionId", section);
        association.put("classroomPosition", "Teacher of Record");
        return association;
    }
}

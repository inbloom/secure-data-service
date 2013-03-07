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

import org.elasticsearch.common.settings.loader.SettingsLoader.Helper;
import org.joda.time.DateTime;
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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Unit tests for staff --> grade context validator.
 *
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToSubStudentSectionAssociationEntityValidatorTest {

    @Autowired
    private StaffToSubStudentSectionAssociationEntityValidator validator;

    @Autowired
    private SecurityContextInjector injector;

    private PagingRepositoryDelegate<Entity> mockRepo;
    private StaffToStudentValidator staffToStudentValidator;
    private Set<String> studentIds;
    
    @Autowired
    ValidatorTestHelper helper;
    

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        studentIds = new HashSet<String>();
        mockRepo = Mockito.mock(PagingRepositoryDelegate.class);
        staffToStudentValidator = Mockito.mock(StaffToStudentValidator.class);

        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn("1");
        injector.setCustomContext(user, fullName, "DERPREALM", roles, entity, "123");

        validator.setRepo(mockRepo);
        validator.setStaffToStudentValidator(staffToStudentValidator);
    }

    @After
    public void tearDown() {
        mockRepo = null;
        staffToStudentValidator = null;
        studentIds.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanValidateStaffToGrade() throws Exception {
        assertTrue(validator.canValidate(EntityNames.GRADE, false));
        assertTrue(validator.canValidate(EntityNames.GRADE, true));
    }

    @Test
    public void testCanValidateStaffToStudentCompetency() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY, true));
    }

    @Test
    public void testDeniedStaffToOtherEntity() throws Exception {
        assertFalse(validator.canValidate(EntityNames.STUDENT, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, false));
        assertFalse(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, true));
    }

    @Test
    public void testCanGetAccessToGrade() throws Exception {
        Set<String> grades = new HashSet<String>();
        Map<String, Object> association = buildStudentSectionAssociation("student123", "section123", DateTime.now()
                .minusDays(3));
        Entity studentSectionAssociation = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, association);

        Entity gradeEntity = helper.generateGrade(studentSectionAssociation.getEntityId());
        grades.add(gradeEntity.getEntityId());
        studentIds.add("student123");

        Mockito.when(mockRepo.findAll(Mockito.eq(EntityNames.GRADE), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(gradeEntity));

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(studentSectionAssociation));

        Mockito.when(staffToStudentValidator.validate(EntityNames.STUDENT, studentIds)).thenReturn(true);
        assertTrue(validator.validate(EntityNames.GRADE, grades));
    }

    @Test
    public void testCanNotGetAccessToGrade() throws Exception {
        Set<String> grades = new HashSet<String>();
        Map<String, Object> association = buildStudentSectionAssociation("student123", "section123", DateTime.now()
                .minusDays(3));
        Entity studentSectionAssociation = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, association);

        Entity gradeEntity = helper.generateGrade(studentSectionAssociation.getEntityId());
        grades.add(gradeEntity.getEntityId());
        studentIds.add("student123");

        Mockito.when(mockRepo.findAll(Mockito.eq(EntityNames.GRADE), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(gradeEntity));

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(studentSectionAssociation));

        Mockito.when(staffToStudentValidator.validate(EntityNames.STUDENT, studentIds)).thenReturn(false);
        assertFalse(validator.validate(EntityNames.GRADE, grades));
    }

    @Test
    public void testCanNotGetAccessToGradeDueToGradeLookup() throws Exception {
        Set<String> grades = new HashSet<String>();
        Map<String, Object> association = buildStudentSectionAssociation("student123", "section123", DateTime.now()
                .minusDays(3));
        Entity studentSectionAssociation = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, association);
;
        Entity gradeEntity = helper.generateGrade(studentSectionAssociation.getEntityId());
        grades.add(gradeEntity.getEntityId());

        Mockito.when(mockRepo.findAll(Mockito.eq(EntityNames.GRADE), Mockito.any(NeutralQuery.class))).thenReturn(
                new ArrayList<Entity>());

        assertFalse(validator.validate(EntityNames.GRADE, grades));
    }

    @Test
    public void testCanNotGetAccessToGradeDueToStudentSectionAssociationLookup() throws Exception {
        Set<String> grades = new HashSet<String>();
        Map<String, Object> association = buildStudentSectionAssociation("student123", "section123", DateTime.now()
                .minusDays(3));
        Entity studentSectionAssociation = new MongoEntity(EntityNames.STUDENT_SECTION_ASSOCIATION, association);

        Entity gradeEntity = helper.generateGrade(studentSectionAssociation.getEntityId());
        grades.add(gradeEntity.getEntityId());

        Mockito.when(mockRepo.findAll(Mockito.eq(EntityNames.GRADE), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(gradeEntity));

        Mockito.when(
                mockRepo.findAll(Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(new ArrayList<Entity>());

        assertFalse(validator.validate(EntityNames.GRADE, grades));
    }

    private Map<String, Object> buildStudentSectionAssociation(String student, String section, DateTime begin) {
        Map<String, Object> association = new HashMap<String, Object>();
        association.put("studentId", student);
        association.put("sectionId", section);
        association.put("beginDate", validator.getDateTimeString(begin));
        return association;
    }
}

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class TransitiveTeacherToSectionValidatorTest {
    @Autowired
    TransitiveTeacherToSectionValidator validator;
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    SecurityContextInjector injector;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    private TeacherToSectionValidator mockSectionValidator;
    private StudentValidatorHelper mockStudentValidator;

    Set<String> sectionIds;
    Set<String> studentIds;
    
    @Before
    public void setUp() throws Exception {
        helper.setUpTeacherContext();
        
        sectionIds = new HashSet<String>();
        studentIds = new HashSet<String>();
        mockSectionValidator = Mockito.mock(TeacherToSectionValidator.class);
        mockStudentValidator = Mockito.mock(StudentValidatorHelper.class);
        validator.setStudentHelper(mockStudentValidator);
        validator.setSectionValidator(mockSectionValidator);
    }
    
    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.TEACHER_SECTION_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STUDENT_SECTION_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.SECTION, new NeutralQuery());
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
    }
    
    @Test
    public void testCanValidateSingleSection() {
        Entity section = helper.generateSection(helper.ED_ORG_ID);
        sectionIds.add(section.getEntityId());
        // Directly associated
        Mockito.when(mockStudentValidator.getStudentIds()).thenReturn(new ArrayList<String>(studentIds));
        Mockito.when(mockSectionValidator.getValid(EntityNames.SECTION, sectionIds)).thenReturn(sectionIds);
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
        // Via Student
        helper.generateSSA("BERP", section.getEntityId(), false);
        studentIds.add("BERP");
        Mockito.when(mockStudentValidator.getStudentIds()).thenReturn(new ArrayList<String>(studentIds));
        Mockito.when(mockSectionValidator.getValid(EntityNames.SECTION, sectionIds)).thenReturn(sectionIds);
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }
    
    @Test
    public void testCanNotValidateExpiredSection() {
        Entity section = helper.generateSection(helper.ED_ORG_ID);
        helper.generateSSA("BERP", section.getEntityId(), true);
        studentIds.add("BERP");
        Mockito.when(mockStudentValidator.getStudentIds()).thenReturn(new ArrayList<String>(studentIds));
        Mockito.when(mockSectionValidator.getValid(EntityNames.SECTION, sectionIds)).thenReturn(sectionIds);
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }
    
    @Test
    public void testCanNotValidateInvalidSection() {
        Entity section = helper.generateSection(helper.ED_ORG_ID);
        sectionIds.add(section.getEntityId());
        // Directly associated
        Mockito.when(mockStudentValidator.getStudentIds()).thenReturn(new ArrayList<String>(studentIds));
        Mockito.when(mockSectionValidator.getValid(EntityNames.SECTION, sectionIds)).thenReturn(Collections.EMPTY_SET);
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
        // Via Student
        helper.generateSSA("BERP", section.getEntityId(), false);
        studentIds.add("BERP");
        Mockito.when(mockStudentValidator.getStudentIds()).thenReturn(new ArrayList<String>());
        Mockito.when(mockSectionValidator.getValid(EntityNames.SECTION, sectionIds)).thenReturn(Collections.EMPTY_SET);
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }
}

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
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
public class TeacherToGradeValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToGradeValidator validator;
    
    Entity section1 = null;     //teacher teaches
    Entity section2 = null;     //teacher doesn't teach but has student in
    Entity section3 = null;     //teacher doesn't teach and doesn't have student in
    Entity grade1 = null;       //grade from section teacher teaches
    Entity grade2 = null;       //grade from section teacher doesn't teach
    Entity grade3 = null;       //grade from completely unrelated student/section
    
    @Before
    public void setUp() throws Exception {
        helper.resetRepo();
        helper.setUpTeacherContext();
        String student1 = helper.generateStudentAndStudentSchoolAssociation("student1", "school1", false);
        String student2 = helper.generateStudentAndStudentSchoolAssociation("student2", "school1", false);
        section1 = helper.generateSection("school1");
        section2 = helper.generateSection("school1");
        section3 = helper.generateSection("school1");
        Entity studentSectionAssociation1 = helper.generateSSA(student1, section1.getEntityId(), false);
        Entity studentSectionAssociation2 = helper.generateSSA(student1, section2.getEntityId(), false);
        Entity studentSectionAssociation3 = helper.generateSSA(student2, section3.getEntityId(), false);
        helper.generateTSA(ValidatorTestHelper.STAFF_ID, section1.getEntityId(), false);
        grade1 = helper.generateGrade(studentSectionAssociation1.getEntityId());
        grade2 = helper.generateGrade(studentSectionAssociation2.getEntityId());
        grade3 = helper.generateGrade(studentSectionAssociation3.getEntityId());
    }
    
    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.GRADE, true));
        assertTrue(validator.canValidate(EntityNames.GRADE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }
        
    @Test
    public void testValidGrades() {
        assertTrue(validator.validate(EntityNames.GRADE, new HashSet<String>(Arrays.asList(grade1.getEntityId()))));
        assertTrue(validator.validate(EntityNames.GRADE, new HashSet<String>(Arrays.asList(grade2.getEntityId()))));
        assertTrue(validator.validate(EntityNames.GRADE, new HashSet<String>(Arrays.asList(grade1.getEntityId(), grade2.getEntityId()))));
    }
    
    @Test
    public void testInvalidGrades() {
        assertFalse(validator.validate(EntityNames.GRADE, new HashSet<String>(Arrays.asList(grade3.getEntityId()))));
        assertFalse(validator.validate(EntityNames.GRADE, new HashSet<String>(Arrays.asList(grade1.getEntityId(), grade3.getEntityId()))));
    }
}

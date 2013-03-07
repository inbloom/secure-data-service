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
import org.slc.sli.api.constants.EntityNames;
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
public class TeacherToStudentCompetencyValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToStudentCompetencyValidator validator;
    
    Entity section1 = null;     //teacher teaches
    Entity section2 = null;     //teacher doesn't teach but has student in
    Entity section3 = null;     //teacher doesn't teach and doesn't have student in
    Entity sComp1 = null;       //comp from section teacher teaches
    Entity sComp2 = null;       //comp from section teacher doesn't teach
    Entity sComp3 = null;       //comp from completely unrelated student/section
    
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
        Entity compObj = helper.generateStudentCompetencyObjective("school1");
        helper.generateTSA(ValidatorTestHelper.STAFF_ID, section1.getEntityId(), false);
        sComp1 = helper.generateStudentCompetency(studentSectionAssociation1.getEntityId(), compObj.getEntityId());
        sComp2 = helper.generateStudentCompetency(studentSectionAssociation2.getEntityId(), compObj.getEntityId());
        sComp3 = helper.generateStudentCompetency(studentSectionAssociation3.getEntityId(), compObj.getEntityId());
    }
    
    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }
    
    @Test
    public void testValidComps() {
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY, new HashSet<String>(Arrays.asList(sComp1.getEntityId()))));
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY, new HashSet<String>(Arrays.asList(sComp2.getEntityId()))));
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY, new HashSet<String>(Arrays.asList(sComp1.getEntityId(), sComp2.getEntityId()))));
    }
    
    @Test
    public void testInvalidComps() {
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY, new HashSet<String>(Arrays.asList(sComp3.getEntityId()))));
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY, new HashSet<String>(Arrays.asList(sComp1.getEntityId(), sComp3.getEntityId()))));
    }
}

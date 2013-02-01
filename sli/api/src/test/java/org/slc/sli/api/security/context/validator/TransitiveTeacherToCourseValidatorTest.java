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
public class TransitiveTeacherToCourseValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TransitiveTeacherToCourseValidator validator;
    

    
    @Before
    public void setUp() throws Exception {
        helper.resetRepo();
        helper.setUpTeacherContext();
    }
    
    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertFalse(validator.canValidate(EntityNames.COURSE, false));
        assertTrue(validator.canValidate(EntityNames.COURSE, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }

    @Test
    public void testValidateThroughEdorgs() {
        
        Entity sea1 = helper.generateEdorgWithParent(null);
        Entity lea1 = helper.generateEdorgWithParent(sea1.getEntityId());
        Entity school1 = helper.generateEdorgWithParent(lea1.getEntityId());
        Entity school2 = helper.generateEdorgWithParent(lea1.getEntityId());
        
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
        Entity course1 = helper.generateCourse(school1.getEntityId());
        Entity course2 = helper.generateCourse(school2.getEntityId());
        Entity course3 = helper.generateCourse(lea1.getEntityId());
        Entity  course4 = helper.generateCourse(sea1.getEntityId());
        
        assertTrue(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        assertTrue(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
        assertTrue(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course4.getEntityId()))));
        assertTrue(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId(), course3.getEntityId(), course4.getEntityId()))));
        assertFalse(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        assertFalse(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId(), course2.getEntityId()))));
    }
    
    @Test
    public void testValidateThroughCourseOffering() {
        Entity course1 = helper.generateCourse("someEdorg");
        Entity course2 = helper.generateCourse("someEdorg");
        Entity courseOffering1 = helper.generateCourseOffering("someEdorg", course1.getEntityId());
        Entity courseOffering11 = helper.generateCourseOffering("someEdorg", course1.getEntityId());
        Entity courseOffering2 = helper.generateCourseOffering("someEdorg", course2.getEntityId());
        Entity courseOffering22 = helper.generateCourseOffering("someEdorg", course2.getEntityId());
        Entity section1 = helper.generateSectionWithCourseOffering("someEdorg", courseOffering1.getEntityId());
        Entity section11 = helper.generateSectionWithCourseOffering("someEdorg", courseOffering1.getEntityId());
        Entity section111 = helper.generateSectionWithCourseOffering("someEdorg", courseOffering1.getEntityId());
        Entity section2 = helper.generateSectionWithCourseOffering("someEdorg", courseOffering2.getEntityId());
        helper.generateTSA(ValidatorTestHelper.STAFF_ID, section1.getEntityId(), false);
        
        assertTrue(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        assertFalse(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        assertFalse(validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId(), course2.getEntityId()))));
    }
    
    @Test
    public void testInvalidGrades() {

    }
}

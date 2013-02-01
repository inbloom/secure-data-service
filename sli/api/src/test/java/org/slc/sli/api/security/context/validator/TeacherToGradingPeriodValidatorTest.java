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
import java.util.Set;

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
public class TeacherToGradingPeriodValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToGradingPeriodValidator validator;
    
    Entity gradingPeriod1 = null;   //session1
    Entity gradingPeriod11 = null;   //session1
    Entity gradingPeriod2 = null;   //session2
    Entity gradingPeriod22 = null;   //session2
    Entity gradingPeriod3 = null;   //session3
    Entity gradingPeriod33 = null;   //session3
    Entity gradingPeriod4 = null;   //no session
    
    Entity session1 = null;         //school1
    Entity session2 = null;         //school2
    Entity session3 = null;         //lea1
    
    Entity school1 = null;  //teacher's school
    Entity school2 = null;
    Entity lea1 = null;
    
    @Before
    public void setUp() throws Exception {
        helper.resetRepo();
        helper.setUpTeacherContext();

        lea1 = helper.generateEdorgWithParent(null);
        school1 = helper.generateEdorgWithParent(lea1.getEntityId());
        school2 = helper.generateEdorgWithParent(lea1.getEntityId());
        
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
        
        gradingPeriod1 = helper.generateGradingPeriod();
        gradingPeriod11 = helper.generateGradingPeriod();
        gradingPeriod2 = helper.generateGradingPeriod();
        gradingPeriod22 = helper.generateGradingPeriod();
        gradingPeriod3 = helper.generateGradingPeriod();
        gradingPeriod33 = helper.generateGradingPeriod();
        gradingPeriod4 = helper.generateGradingPeriod();
        
        
        session1 = helper.generateSession(school1.getEntityId(), Arrays.asList(gradingPeriod1.getEntityId(), gradingPeriod11.getEntityId()));
        session2 = helper.generateSession(school2.getEntityId(), Arrays.asList(gradingPeriod2.getEntityId(), gradingPeriod22.getEntityId()));
        session3 = helper.generateSession(lea1.getEntityId(), Arrays.asList(gradingPeriod3.getEntityId(), gradingPeriod33.getEntityId()));
    }
    
    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.GRADING_PERIOD, true));
        assertTrue(validator.canValidate(EntityNames.GRADING_PERIOD, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }
    
    @Test
    public void testValidGradingPeriods() {
        assertTrue(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod1.getEntityId())));
        assertTrue(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod11.getEntityId())));
        assertTrue(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod1.getEntityId(), gradingPeriod11.getEntityId())));
        assertTrue(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod3.getEntityId())));
        assertTrue(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod33.getEntityId())));
        assertTrue(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod1.getEntityId(), gradingPeriod3.getEntityId())));
    }
    
    @Test
    public void testInvalidGrades() {
        assertFalse(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod2.getEntityId())));
        assertFalse(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod22.getEntityId())));
        assertFalse(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod4.getEntityId())));
        assertFalse(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod1.getEntityId(), gradingPeriod2.getEntityId())));
        assertFalse(validator.validate(EntityNames.GRADING_PERIOD, list(gradingPeriod1.getEntityId(), gradingPeriod4.getEntityId())));
    }
    
    private Set<String> list(String ... elements ) {
        Set<String> toReturn = new HashSet<String>();
        for (String s : elements) {
            toReturn.add(s);
        }
        return toReturn;
    }
}

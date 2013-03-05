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
public class TeacherToStudentCompetencyObjectiveValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToStudentCompetencyObjectiveValidator validator;
    
    Entity school1;
    Entity school2;
    Entity lea1;
    
    Entity sco1;    //school1
    Entity sco2;    //school2
    Entity sco3;    //lea1
    
    @Before
    public void setUp() throws Exception {
        helper.resetRepo();
        helper.setUpTeacherContext();
        lea1 = helper.generateEdorgWithParent(null);
        school1 = helper.generateEdorgWithParent(lea1.getEntityId());
        school2 = helper.generateEdorgWithParent(lea1.getEntityId());
        sco1 = helper.generateStudentCompetencyObjective(school1.getEntityId());
        sco2 = helper.generateStudentCompetencyObjective(school2.getEntityId());
        sco3 = helper.generateStudentCompetencyObjective(lea1.getEntityId());
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
    }
    
    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }

    @Test
    public void testValidSCOs() {
        assertTrue(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new HashSet<String>(Arrays.asList(sco1.getEntityId()))));
    }
    
    @Test
    public void testInvalidSCOs() {
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new HashSet<String>(Arrays.asList(sco2.getEntityId()))));
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new HashSet<String>(Arrays.asList(sco3.getEntityId()))));
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new HashSet<String>(Arrays.asList(sco2.getEntityId(), sco3.getEntityId()))));
        assertFalse(validator.validate(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new HashSet<String>(Arrays.asList(sco1.getEntityId(), sco3.getEntityId()))));
    }
}

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

import java.util.HashSet;
import java.util.Set;

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
public class TeacherToDisciplineIncidentValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToDisciplineIncidentValidator validator;
        
    Entity school1 = null;  //teacher's school
    Entity school2 = null;
    Entity lea1 = null;
    
    Entity disciplineIncident1 = null;  //direct association via staffId
    Entity disciplineIncident2 = null;  //association via student1
    Entity disciplineIncident3 = null;  //no association - with student2
    Entity disciplineIncident4 = null;  //associated to student1 and student2, so visible
    String student1 = null; //student teacher has access to
    String student2 = null; //student teacher does not have access to
    
    @Before
    public void setUp() throws Exception {
        helper.resetRepo();
        helper.setUpTeacherContext();

        lea1 = helper.generateEdorgWithParent(null);
        school1 = helper.generateEdorgWithParent(lea1.getEntityId());
        school2 = helper.generateEdorgWithParent(lea1.getEntityId());
        
        helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, school1.getEntityId());
        
        disciplineIncident1 = helper.generateDisciplineIncident(school1.getEntityId(), ValidatorTestHelper.STAFF_ID, "someOtherStaffId");
        disciplineIncident2 = helper.generateDisciplineIncident(school1.getEntityId());
        disciplineIncident3 = helper.generateDisciplineIncident(school2.getEntityId());
        disciplineIncident4 = helper.generateDisciplineIncident(school1.getEntityId());
        
        student1 = helper.generateStudentAndStudentSchoolAssociation("student1", school1.getEntityId(), false);
        student2 = helper.generateStudentAndStudentSchoolAssociation("student2", school2.getEntityId(), false);
        
        Entity section1 = helper.generateSection(school1.getEntityId(), "sessionId");
        Entity section2 = helper.generateSection(school2.getEntityId(), "sessionId");
        
        helper.generateTSA(ValidatorTestHelper.STAFF_ID, section1.getEntityId(), false);
        helper.generateSSA(student1, section1.getEntityId(), false);
        helper.generateSSA(student2, section2.getEntityId(), false);
        
        helper.generateStudentDisciplineIncidentAssociation(student1, disciplineIncident2.getEntityId());
        helper.generateStudentDisciplineIncidentAssociation(student2, disciplineIncident3.getEntityId());
        helper.generateStudentDisciplineIncidentAssociation(student1, disciplineIncident4.getEntityId());
        helper.generateStudentDisciplineIncidentAssociation(student2, disciplineIncident4.getEntityId());
    }
    
    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, true));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }
        
    @Test
    public void testValidIncident() {
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, list(disciplineIncident1.getEntityId())));
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, list(disciplineIncident2.getEntityId())));
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, list(disciplineIncident1.getEntityId(), disciplineIncident2.getEntityId())));
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, list(disciplineIncident4.getEntityId())));
    }
    
    @Test
    public void testInvalidIncident() {
        assertFalse(validator.validate(EntityNames.DISCIPLINE_INCIDENT, list(disciplineIncident3.getEntityId())));
        assertFalse(validator.validate(EntityNames.DISCIPLINE_INCIDENT, list(disciplineIncident3.getEntityId(), disciplineIncident1.getEntityId())));
    }
    
    private Set<String> list(String ... elements ) {
        Set<String> toReturn = new HashSet<String>();
        for (String s : elements) {
            toReturn.add(s);
        }
        return toReturn;
    }
}

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
public class TeacherToSectionValidatorTest {
    @Autowired
    TeacherToSectionValidator validator;
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    SecurityContextInjector injector;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    Set<String> sectionIds;

    @Before
    public void setUp() throws Exception {
        helper.setUpTeacherContext();
        
        sectionIds = new HashSet<String>();
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.TEACHER_SECTION_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.SECTION, new NeutralQuery());
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.SECTION, false));
        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
    }
    
    @Test
    public void testCanValidateSingleSection() {
        helper.generateTeacherSchool(helper.STAFF_ID, helper.ED_ORG_ID);
        Entity section = helper.generateSection(helper.ED_ORG_ID);
        helper.generateTSA(helper.STAFF_ID, section.getEntityId(), false);
        sectionIds.add(section.getEntityId());
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }
    
    @Test
    public void testCanNotValidateInvalidSection() {
        helper.generateTeacherSchool(helper.STAFF_ID, helper.ED_ORG_ID);
        Entity section = helper.generateSection(helper.ED_ORG_ID);
        helper.generateTSA("BEEPBOOP", section.getEntityId(), false);
        sectionIds.add(section.getEntityId());
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
        helper.generateTSA(helper.STAFF_ID, "DERPBERP", false);
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
    }
    
    @Test
    public void testCanValidateMultiple() {
        helper.generateTeacherSchool(helper.STAFF_ID, helper.ED_ORG_ID);
        for (int i = 0; i < 10; ++i) {
            Entity section = helper.generateSection(helper.ED_ORG_ID);
            helper.generateTSA(helper.STAFF_ID, section.getEntityId(), false);
            sectionIds.add(section.getEntityId());
        }
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }
    
    @Test
    public void testValidateIntersection() {
        helper.generateTeacherSchool(helper.STAFF_ID, helper.ED_ORG_ID);
        Entity section = null;
        for (int i = 0; i < 10; ++i) {
            section = helper.generateSection(helper.ED_ORG_ID);
            helper.generateTSA(helper.STAFF_ID, section.getEntityId(), false);
            sectionIds.add(section.getEntityId());
        }
        // Disconnected section
        section = helper.generateSection(helper.ED_ORG_ID);
        sectionIds.add(section.getEntityId());
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
        // Improperly associated section
        helper.generateTSA("DERPDERP", section.getEntityId(), false);
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
        // Improperly associated section
        helper.generateTSA(helper.STAFF_ID, "MERPMERP", false);
        assertFalse(validator.validate(EntityNames.SECTION, sectionIds));
        // Correctly associating the section
        helper.generateTSA(helper.STAFF_ID, section.getEntityId(), false);
        assertTrue(validator.validate(EntityNames.SECTION, sectionIds));
    }

}

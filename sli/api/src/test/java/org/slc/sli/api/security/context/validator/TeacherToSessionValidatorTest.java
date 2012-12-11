/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
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
public class TeacherToSessionValidatorTest {
    
    @Autowired
    TeacherToSessionValidator validator;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    ValidatorTestHelper helper;
    
    private TeacherToSectionValidator mockSectionValidator;
    private Set<String> sessionIds;
    private Set<String> sectionIds;

    @Before
    public void setUp() throws Exception {
        helper.setUpTeacherContext();
        
        mockSectionValidator = Mockito.mock(TeacherToSectionValidator.class);
        validator.setSectionValidator(mockSectionValidator);
        sessionIds = new HashSet<String>();
        sectionIds = new HashSet<String>();
    }
    
    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.SECTION, new NeutralQuery());
        repo.deleteAll(EntityNames.SESSION, new NeutralQuery());
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.SESSION, false));
        assertFalse(validator.canValidate(EntityNames.SESSION, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, true));
    }
    
    @Test
    public void testCanNotValidateBadInputs() {
        assertFalse(validator.validate(null, null));
        assertFalse(validator.validate(EntityNames.SESSION, null));
        assertFalse(validator.validate(EntityNames.SESSION, new HashSet<String>()));
    }
    
    @Test
    public void testCanValidateSingleSession() {
        Entity session = helper.generateSession(helper.ED_ORG_ID, null);
        sectionIds.add(helper.generateSection(helper.ED_ORG_ID, session.getEntityId()).getEntityId());
        sessionIds.add(session.getEntityId());
        Mockito.when(mockSectionValidator.validate(EntityNames.SECTION, sectionIds)).thenReturn(true);
        assertTrue(validator.validate(EntityNames.SESSION, sessionIds));
    }
    
    @Test
    public void testCanNotValidateInvalidSession() {
        Entity session = helper.generateSession(helper.ED_ORG_ID, null);
        sectionIds.add(helper.generateSection(helper.ED_ORG_ID, null).getEntityId());
        sessionIds.add(session.getEntityId());
        Mockito.when(mockSectionValidator.validate(EntityNames.SECTION, sectionIds)).thenReturn(false);
        assertFalse(validator.validate(EntityNames.SESSION, sessionIds));
    }
    
    @Test
    public void testValidateIntersectionRules() {
        for (int i = 0; i < 10; ++i) {
            Entity session = helper.generateSession(helper.ED_ORG_ID, null);
            sectionIds.add(helper.generateSection(helper.ED_ORG_ID, session.getEntityId()).getEntityId());
            sessionIds.add(session.getEntityId());
        }
        Mockito.when(mockSectionValidator.validate(EntityNames.SECTION, sectionIds)).thenReturn(true);
        assertTrue(validator.validate(EntityNames.SESSION, sessionIds));
        sectionIds.add(helper.generateSection(helper.ED_ORG_ID, "BOOP").getEntityId());
        Mockito.when(mockSectionValidator.validate(EntityNames.SECTION, sectionIds)).thenReturn(false);
        assertFalse(validator.validate(EntityNames.SESSION, sessionIds));
    }
}

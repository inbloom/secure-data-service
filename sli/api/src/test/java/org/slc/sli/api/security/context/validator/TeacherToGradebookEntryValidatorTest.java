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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the teacher to gradebook entries validator.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class TeacherToGradebookEntryValidatorTest {
    
    @Autowired
    private GenericToGradebookEntryValidator validator;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    ValidatorTestHelper helper;
    
    Entity teacher1 = null;   //associated to school1

    Entity lea1 = null;
    Entity school1 = null;

    Entity section1 = null; //associated with teacher1
    Entity section3 = null; //no association
    
    Entity gradebookEntry1 = null;  //section1
    Entity gradebookEntry3 = null;  //section3
    
    @Before
    public void setUp() throws Exception {
        helper.resetRepo();

        Map<String, Object> body = new HashMap<String, Object>();
        teacher1 = helper.generateTeacher();

        lea1 = helper.generateEdorgWithParent(null);

        school1 = helper.generateEdorgWithParent(lea1.getEntityId());
        
        helper.generateTeacherSchool(teacher1.getEntityId(), school1.getEntityId());
        
        body = new HashMap<String, Object>();

        section1 = helper.generateSection("school1");
        helper.generateTSA(teacher1.getEntityId(), section1.getEntityId(), false);
        
        section3 = helper.generateSection("school1");
        
        body = new HashMap<String, Object>();
        body.put("sectionId", section1.getEntityId());
        gradebookEntry1 = repo.create(EntityNames.GRADEBOOK_ENTRY, body);
        
        body = new HashMap<String, Object>();
        body.put("sectionId", section3.getEntityId());
        gradebookEntry3 = repo.create(EntityNames.GRADEBOOK_ENTRY, body);
      
    }
    
    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }
    
    @Test
    public void testCanValidateAsTeacher() {
        setupCurrentUser(teacher1);
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.GRADEBOOK_ENTRY, false));
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.GRADEBOOK_ENTRY, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }
    
    @Test
    public void testValidAssociations() {
        setupCurrentUser(teacher1);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.GRADEBOOK_ENTRY, new HashSet<String>(Arrays.asList(gradebookEntry1.getEntityId()))));
    }
        
    @Test
    public void testInvalidAssociations() {
        setupCurrentUser(teacher1);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.GRADEBOOK_ENTRY, 
                new HashSet<String>(Arrays.asList(UUID.randomUUID().toString()))));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.GRADEBOOK_ENTRY, 
                new HashSet<String>()));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.GRADEBOOK_ENTRY, 
                new HashSet<String>(Arrays.asList(gradebookEntry3.getEntityId()))));
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.GRADEBOOK_ENTRY, 
                new HashSet<String>(Arrays.asList(gradebookEntry1.getEntityId(), gradebookEntry3.getEntityId()))));
    }
    
}

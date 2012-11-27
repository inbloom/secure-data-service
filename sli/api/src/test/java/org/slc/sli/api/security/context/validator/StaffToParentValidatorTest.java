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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
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
 * Tests the staff to parent validator.
 * 
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class StaffToParentValidatorTest {
    
    @Autowired
    private StaffToParentValidator validator;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private SecurityContextInjector injector;
    
    Entity staff1 = null;   //associated to LEA
    Entity staff2 = null;   //associated to school1
    Entity staff3 = null;   //associated to school2
    Entity student1 = null;   //associated to school1
    Entity student2 = null;   //associated to school2

    
    Entity lea1 = null;
    Entity school1 = null;
    Entity school2 = null;
    
    Entity parent1 = null;  //parent of student1
    Entity parent2 = null;  //parent of student2
    @Before
    public void setUp() {

        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);
        repo.deleteAll("course", null);
        repo.deleteAll(EntityNames.PARENT, null);
        repo.deleteAll(EntityNames.STUDENT_PARENT_ASSOCIATION, null);
        repo.deleteAll(EntityNames.STUDENT, null);
        repo.deleteAll(EntityNames.COHORT, null);
        repo.deleteAll(EntityNames.PROGRAM, null);
        repo.deleteAll(EntityNames.STUDENT_COHORT_ASSOCIATION, null);
        repo.deleteAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, null);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);
        
        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create("staff", body);
        
        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff3");
        staff3 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        lea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school1 = repo.create("educationOrganization", body);
        
        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);
        
        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);
        
        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school2.getEntityId());
        body.put("staffReference", staff3.getEntityId());
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, body);
        
        body = new HashMap<String, Object>();
        student1 = repo.create("student", body);
        Map<String, Object> schoolData = new HashMap<String, Object>();
        schoolData.put("edOrgs", Arrays.asList(lea1.getEntityId(), school1.getEntityId()));
        student1.getDenormalizedData().put("schools", Arrays.asList(schoolData));

        
        body = new HashMap<String, Object>();
        student2 = repo.create("student", body);
        schoolData = new HashMap<String, Object>();
        schoolData.put("edOrgs", Arrays.asList(lea1.getEntityId(), school2.getEntityId()));
        student2.getDenormalizedData().put("schools", Arrays.asList(schoolData));
        
        body = new HashMap<String, Object>();
        body.put("schoolId", school1.getEntityId());
        body.put("studentId", student1.getEntityId());
        repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);
        
        body = new HashMap<String, Object>();
        body.put("schoolId", school2.getEntityId());
        body.put("studentId", student2.getEntityId());
        repo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, body);
        
        body = new HashMap<String, Object>();
        parent1 = repo.create("parent", body);
        
        body = new HashMap<String, Object>();
        parent2 = repo.create("parent", body);

        body = new HashMap<String, Object>();
        body.put("parentId", parent1.getEntityId());
        body.put("studentId", student1.getEntityId());
        repo.create(EntityNames.STUDENT_PARENT_ASSOCIATION, body);

        body = new HashMap<String, Object>();
        body.put("parentId", parent2.getEntityId());
        body.put("studentId", student2.getEntityId());
        repo.create(EntityNames.STUDENT_PARENT_ASSOCIATION, body);
      
    }
    
    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }
    
    @Test
    public void testCanValidateAsStaff() {
        setupCurrentUser(staff1);
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.PARENT, false));
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.PARENT, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }
       
    @Test
    public void testValidAssociationsForStaff1() {
        setupCurrentUser(staff1);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(parent1.getEntityId()))));
        Assert.assertTrue("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(parent2.getEntityId()))));
        Assert.assertTrue("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>(
                Arrays.asList(parent1.getEntityId(), parent2.getEntityId()))));
    }
    
    @Test
    public void testValidAssociationsForStaff2() {
        setupCurrentUser(staff2);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(parent1.getEntityId()))));
    }
    
    @Test
    public void testInvalidAssociationsForStaff2() {
        setupCurrentUser(staff2);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(parent2.getEntityId()))));
    }
    
    @Test
    public void testValidAssociationsForStaff3() {
        setupCurrentUser(staff3);
        Assert.assertTrue("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(parent2.getEntityId()))));
    }
    
    @Test
    public void testInvalidAssociationsForStaff3() {
        setupCurrentUser(staff3);
        Assert.assertFalse("Must not validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(parent1.getEntityId()))));
    }
    
    @Test
    public void testInvalidAssociations() {
        setupCurrentUser(staff2);
        Assert.assertFalse("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>(Arrays.asList(UUID.randomUUID().toString()))));
        Assert.assertFalse("Must validate", validator.validate(EntityNames.PARENT, new HashSet<String>()));
    }
    
}

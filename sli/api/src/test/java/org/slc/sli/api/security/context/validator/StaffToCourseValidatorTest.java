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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Tests the staff to course validator.
 *
 * @author kmyers
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class StaffToCourseValidatorTest {

    @Autowired
    private StaffToCourseValidator validator;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    ValidatorTestHelper helper;

    Entity staff0 = null;   // associated to sea1
    Entity staff1 = null;   // associated to lea1
    Entity staff2 = null;   // associated to school1
    Entity staff3 = null;   // associated to school2

    Entity teacher1 = null;

    Entity course0 = null;   // associated to sea1
    Entity course1 = null;   // associated to lea1
    Entity course2 = null;   // associated to school1
    Entity course3 = null;   // associated to school2
    Entity course4 = null;   // associated to sea2

    Entity sea1 = null;
    Entity sea2 = null;
    Entity lea1 = null;
    Entity school1 = null;
    Entity school2 = null;

    @Before
    public void setUp() {

        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);
        repo.deleteAll("course", null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff0");
        staff0 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff2");
        staff2 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        teacher1 = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff3");
        staff3 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        sea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        sea2 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", sea1.getEntityId());
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
        body.put("educationOrganizationReference", sea1.getEntityId());
        body.put("staffReference", staff0.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school2.getEntityId());
        body.put("staffReference", staff3.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        course0 = helper.generateCourse(sea1.getEntityId());
        course1 = helper.generateCourse(lea1.getEntityId());
        course2 = helper.generateCourse(school1.getEntityId());
        course3 = helper.generateCourse(school2.getEntityId());
        course4 = helper.generateCourse(sea2.getEntityId());
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
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE, false));
        Assert.assertTrue("Must be able to validate", validator.canValidate(EntityNames.COURSE, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }

    @Test
    public void testCannotValidateAsTeacher() {
        setupCurrentUser(teacher1);
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.COURSE, false));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.COURSE, true));
        Assert.assertFalse("Must not be able to validate", validator.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }

    @Test
    public void testContextValidationForStateEducationAgencyStaff() {
        setupCurrentUser(staff0);
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course0.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
        Assert.assertTrue(
                "Should validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course3.getEntityId()))));

        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>()));
        Assert.assertFalse(
                "Should not validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course3.getEntityId(), course4.getEntityId()))));
    }

    @Test
    public void testContextValidationForLocalEducationAgencyStaff() {
        setupCurrentUser(staff1);
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course0.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
        Assert.assertTrue(
                "Should validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course3.getEntityId()))));

        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>()));
        Assert.assertFalse(
                "Should not validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course3.getEntityId(), course4.getEntityId()))));
    }

    @Test
    public void testContextValidationForSchool1Staff() {
        setupCurrentUser(staff2);
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course0.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        Assert.assertTrue(
                "Should validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId()))));

        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>()));
        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course4.getEntityId()))));
        Assert.assertFalse(
                "Should not validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course3.getEntityId()))));
        Assert.assertFalse(
                "Should not validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course4.getEntityId()))));
    }

    @Test
    public void testContextValidationForSchool2Staff() {
        setupCurrentUser(staff3);
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course0.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course1.getEntityId()))));
        Assert.assertTrue("Should validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course3.getEntityId()))));
        Assert.assertTrue(
                "Should validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course3.getEntityId()))));

        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>()));
        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course2.getEntityId()))));
        Assert.assertFalse("Should not validate context.",
                validator.validate(EntityNames.COURSE, new HashSet<String>(Arrays.asList(course4.getEntityId()))));
        Assert.assertFalse(
                "Should not validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course2.getEntityId(), course3.getEntityId()))));
        Assert.assertFalse(
                "Should not validate context.",
                validator.validate(
                        EntityNames.COURSE,
                        new HashSet<String>(Arrays.asList(course0.getEntityId(), course1.getEntityId(),
                                course3.getEntityId(), course4.getEntityId()))));
    }
}

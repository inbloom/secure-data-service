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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests for teacher --> staff context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TransitiveTeacherToStaffValidatorTest {

    @Autowired
    private TransitiveTeacherToStaffValidator validator;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    Entity teacher1Myself = null;
    Entity staff1 = null;   //associated to LEA
    Entity staff2 = null;   //associated to school (no enddate)
    Entity staff3 = null;   //associated to school (with enddate)
    Entity staff4 = null;   //associated to school (expired enddate)
    Entity staff5 = null;   //not associated to anything
    Entity lea1 = null;
    Entity school1 = null;

    @Before
    public void setUp() {
        // Set up the principal
        String user = "fake teacher";
        String fullName = "Fake Teacher";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);


        repo.deleteAll("staff", null);
        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staffEducationOrganizationAssociation", null);

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
        body.put("staffUniqueStateId", "staff4");
        staff4 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff5");
        staff5 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        teacher1Myself = repo.create("teacher", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        lea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("School"));
        body.put("parentEducationAgencyReference", lea1.getEntityId());
        school1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff2.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff3.getEntityId());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime future = DateTime.now().plusDays(5);
        body.put("endDate", future.toString(fmt));
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", staff4.getEntityId());
        DateTime past = DateTime.now().minusYears(10);
        body.put("endDate", past.toString(fmt));
        repo.create("staffEducationOrganizationAssociation", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", school1.getEntityId());
        body.put("staffReference", teacher1Myself.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        injector.setCustomContext(user, fullName, "MERPREALM", roles, teacher1Myself, "111");
    }

    @After
    public void tearDown() {
        repo = null;
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testCanValidateTeacherToStaff() throws Exception {
        assertTrue(validator.canValidate(EntityNames.STAFF, true));
        assertFalse(validator.canValidate(EntityNames.STAFF, false));
    }

    @Test
    public void testInvalidTeacherStaffAssociation() {
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff1.getEntityId()))));
    }

    @Test
    public void testValidTeacherStaffAssociationNoEndDate() {
        assertTrue(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff2.getEntityId()))));
    }

    @Test
    public void testValidTeacherStaffAssociationWithEndDate() {
        assertTrue(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff3.getEntityId()))));
    }

    @Test
    public void testExpiredTeacherStaffAssociation() {
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff4.getEntityId()))));
    }

    @Test
    public void testStaffWithNoEdorgAssociation() {
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff2.getEntityId(), staff5.getEntityId()))));
    }

    @Test
    public void testMulti1() {
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff1.getEntityId(), staff2.getEntityId(), staff3.getEntityId(), staff4.getEntityId()))));
    }

    @Test
    public void testMulti2() {
        assertFalse(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff1.getEntityId(), staff4.getEntityId()))));
    }

    @Test
    public void testMulti3() {
        assertTrue(validator.validate(EntityNames.STAFF, new HashSet<String>(Arrays.asList(staff2.getEntityId(), staff3.getEntityId()))));
    }

}

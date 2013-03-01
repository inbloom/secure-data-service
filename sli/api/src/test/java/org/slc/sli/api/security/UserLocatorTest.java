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

package org.slc.sli.api.security;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.validator.ValidatorTestHelper;
import org.slc.sli.api.security.mock.Mocker;
import org.slc.sli.api.security.resolve.impl.MongoUserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 *
 * @author dkornishev
 *
 */
@Ignore
// Needs to be reworked with new querying structure/MockRepo
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class UserLocatorTest {

    @Autowired
    private MongoUserLocator locator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private Repository<Entity> repo;

    Entity staff1 = null;
    Entity student1 = null;
    Entity student2 = null;

    @Before
    public void init() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STAFF_UNIQUE_STATE_ID, Mocker.VALID_STAFF_ID);
        staff1 = repo.create(EntityNames.TEACHER, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_UNIQUE_STATE_ID, Mocker.VALID_STUDENT_UNIQUE_ID);
        student1 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_UNIQUE_STATE_ID, Mocker.VALID_STUDENT_COPIED_ID);
        student2 = repo.create(EntityNames.STUDENT, body);
    }

    @Test
    public void testFindStaffWithStaffUserType() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_STAFF_ID, "staff");
        Assert.assertNotNull(principal);
        Assert.assertEquals(staff1.getEntityId(), principal.getEntity().getEntityId());
        Assert.assertEquals("staff", principal.getUserType());
    }

    @Test
    public void testFindStaffWithEmptyUserType() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_STAFF_ID, "");
        Assert.assertNotNull(principal);
        Assert.assertEquals(staff1.getEntityId(), principal.getEntity().getEntityId());
        Assert.assertEquals("staff", principal.getUserType());
    }

    @Test
    public void testFindStaffWithNullUserType() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_STAFF_ID, null);
        Assert.assertNotNull(principal);
        Assert.assertEquals(staff1.getEntityId(), principal.getEntity().getEntityId());
        Assert.assertEquals("staff", principal.getUserType());
    }

    @Test
    public void testFindStudentWithUniqueId() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_STUDENT_UNIQUE_ID, "student");
        Assert.assertNotNull(principal);
        Assert.assertEquals(student1.getEntityId(), principal.getEntity().getEntityId());
        Assert.assertEquals("student", principal.getUserType());
    }

    @Test
    public void testFindStudentWithNonUniqueId() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_STUDENT_COPIED_ID, "student");
        Assert.assertNotNull(principal);
        Assert.assertEquals(student2.getEntityId(), principal.getEntity().getEntityId());
        Assert.assertEquals("student", principal.getUserType());
    }

    @Test
    public void testStaffNotFound() {
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.INVALID_STAFF_ID, "");
        Assert.assertNull(principal);
    }

    @Test
    public void testGarbageInput() {
        SLIPrincipal principal = this.locator.locate(null, null, "");
        Assert.assertNull(principal);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFailsWithInvalidAssociation() {
        Entity school = helper.generateEdorgWithParent(null);
        helper.generateStaffEdorg(Mocker.VALID_STAFF_ID, school.getEntityId(), true);
        SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_STAFF_ID, "");

        Assert.assertNull(principal);
    }
}

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

package org.slc.sli.api.security;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.context.validator.ValidatorTestHelper;
import org.slc.sli.api.security.mock.Mocker;
import org.slc.sli.api.security.resolve.impl.MongoUserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

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

	@Before
	public void init() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("staffUniqueStateId", Mocker.VALID_USER_ID);
		repo.create("teacher", body);
	}

	@Test
	public void testUserFound() {
		SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_USER_ID, "");

		Assert.assertNotNull(principal);
	}

	@Test
	public void testuserNotFound() {
		SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.INVALID_USER_ID, "");

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
		helper.generateStaffEdorg(Mocker.VALID_USER_ID, school.getEntityId(), true);
		SLIPrincipal principal = this.locator.locate(Mocker.VALID_REALM, Mocker.VALID_USER_ID, "");

		Assert.assertNull(principal);
	}
}

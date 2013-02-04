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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.domain.Entity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherToStaffProgramAssociationValidatorTest {

	private static final String CORRECT_ENTITY_TYPE = EntityNames.STAFF_PROGRAM_ASSOCIATION;
	private static final String USER_ID = "Master of Magic";
	private static final String PROGRAM_ID = "Nature Node";

	@Resource
	private TeacherToStaffProgramAssociationValidator val;

	@Resource
	private SecurityContextInjector injector;

	@Resource
	private ValidatorTestHelper vth;

	@Before
	public void init() {
		injector.setEducatorContext(USER_ID);
	}

	@After
	public void cleanUp() {
	    SecurityContextHolder.clearContext();
	    try {
            vth.resetRepo();
        } catch (Exception e) {
        }
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateWrongType() {
		val.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
	}

	@Test
	public void testSuccessOne() {
		Entity tsa = this.vth.generateStaffProgram(USER_ID, PROGRAM_ID, false, true);
		Assert.assertTrue(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton(tsa.getEntityId())));
	}

	@Test
	public void testSuccessMulti() {
		Set<String> ids = new HashSet<String>();

		for (int i = 0; i < 100; i++) {
			ids.add(this.vth.generateStaffProgram(USER_ID, PROGRAM_ID, false, true).getEntityId());
		}

		Assert.assertTrue(val.validate(CORRECT_ENTITY_TYPE, ids));
	}

	@Test
	public void testWrongId() {
		Assert.assertFalse(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton("Hammerhands")));
		Assert.assertFalse(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton("Nagas")));
		Assert.assertFalse(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton("Phantom Warriors")));
	}

	@Test
	public void testHeterogenousList() {
		Assert.assertFalse(val.validate(
				CORRECT_ENTITY_TYPE,
				new HashSet<String>(Arrays.asList(this.vth.generateStaffProgram(USER_ID, PROGRAM_ID, false, true).getEntityId(), this.vth.generateStaffProgram("Ssss'ra", "Arcanus", false, true).getEntityId(),
						this.vth.generateStaffProgram("Kali", "Arcanus", false, true).getEntityId()))));
	}
}

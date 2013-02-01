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
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
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

/**
 * Not fully unit-tested due to weirdness in magic dal
 * and its interaction with mockrepo 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@Component
public class TransitiveTeacherToCourseOfferingValidatorTest {

	@Autowired
	private ValidatorTestHelper helper;

	@Autowired
	private PagingRepositoryDelegate<Entity> repo;

	@Autowired
	private TransitiveTeacherToCourseOfferingValidator validator;

	private Set<String> courseOfferingIds;

	@Before
	public void setUp() throws Exception {
		helper.setUpTeacherContext();
		courseOfferingIds = new HashSet<String>();
	}

	@After
	public void tearDown() throws Exception {
		SecurityContextHolder.clearContext();
		repo.deleteAll(EntityNames.COURSE_OFFERING, new NeutralQuery());
		repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
		repo.deleteAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new NeutralQuery());

	}

	@Test
	public void testCanValidate() {
		assertTrue(validator.canValidate(EntityNames.COURSE_OFFERING, true));
		assertFalse(validator.canValidate(EntityNames.COURSE_OFFERING, false));
		assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
	}

	@Test
	public void testCanNotValidateInvalidCourseOffering() {
		Entity edorg = helper.generateEdorgWithParent(null);
		helper.generateTeacherSchool(ValidatorTestHelper.STAFF_ID, edorg.getEntityId());
		Entity session = helper.generateCourseOffering("MERP");
		courseOfferingIds.add(session.getEntityId());
		assertFalse(validator.validate(EntityNames.COURSE_OFFERING, courseOfferingIds));
	}


	@SuppressWarnings({ "unchecked", "unused" })
	private void generateSection() {
		Map<String, Object> section = new HashMap<String, Object>();
		section.put(ParameterConstants.SCHOOL_ID, "never");
		section.put(ParameterConstants.SESSION_ID, "fear");
		section.put(ParameterConstants.COURSE_OFFERING_ID, "courseOffering");		
		
		Map<String, Object> dm = new HashMap<String, Object>();
		Map<String, Object> ssa = new HashMap<String, Object>();
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("studentId", "student");
		ssa.put("body", body);
		dm.put("studentSectionAssociation", Arrays.asList(ssa));
		section.put("denormalized", dm);
		Entity sectionEntity=repo.create(EntityNames.SECTION, section);
		
		this.helper.generateTSA("1", sectionEntity.getEntityId(), false);
	}
}

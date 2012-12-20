package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.domain.Entity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TransitiveTeacherToTeacherSchoolAssociationValidatorTest {

	private static final String CORRECT_ENTITY_TYPE = EntityNames.TEACHER_SCHOOL_ASSOCIATION;
	private static final String SCHOOL_ID = "Myrran";
	private static final String USER_ID = "Master of Magic";

	@Resource
	private TransitiveTeacherToTeacherSchoolAssociationValidator val;

	@Resource
	private SecurityContextInjector injector;

	@Resource
	private ValidatorTestHelper vth;

	@Before
	public void init() {
		injector.setEducatorContext(USER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyIdList() {
		val.validate(CORRECT_ENTITY_TYPE, new HashSet<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateWrongType() {
		val.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
	}

	@Test
	public void testSuccessOne() {
		this.vth.generateTeacherSchool(USER_ID, SCHOOL_ID);

		Entity tsa = this.vth.generateTeacherSchool("Heru-er", SCHOOL_ID);
		Assert.assertTrue(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton(tsa.getEntityId())));

		tsa = this.vth.generateTeacherSchool("Izida", SCHOOL_ID);
		Assert.assertTrue(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton(tsa.getEntityId())));

		tsa = this.vth.generateTeacherSchool("Ptah", SCHOOL_ID);
		Assert.assertTrue(val.validate(CORRECT_ENTITY_TYPE, Collections.singleton(tsa.getEntityId())));
	}

	@Test
	public void testSuccessMulti() {
		this.vth.generateTeacherSchool(USER_ID, SCHOOL_ID);
		Set<String> ids = new HashSet<String>();

		int random = (int) Math.floor(Math.random() * 100);
		for (int i = 0; i < random; i++) {
			ids.add(this.vth.generateTeacherSchool("Thor" + i, SCHOOL_ID).getEntityId());
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
				new HashSet<String>(Arrays.asList(this.vth.generateTeacherSchool(USER_ID, SCHOOL_ID).getEntityId(), this.vth.generateTeacherSchool("Ssss'ra", "Arcanus").getEntityId(), this.vth.generateTeacherSchool("Kali", "Arcanus")
						.getEntityId()))));
	}
}

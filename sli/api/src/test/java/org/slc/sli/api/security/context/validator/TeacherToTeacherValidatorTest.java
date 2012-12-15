package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherToTeacherValidatorTest {

	private static final String USER_ID = "Master of Magic";

	@Resource
	private TeacherToTeacherValidator val;

	@Resource
	private SecurityContextInjector injector;

	@Before
	public void init() {
		injector.setEducatorContext(USER_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyIdList() {
		val.validate(EntityNames.TEACHER, new HashSet<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateWrongType() {
		val.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
	}

	@Test
	public void testSuccess() {
		Assert.assertTrue(val.validate(EntityNames.TEACHER, Collections.singleton(USER_ID)));
	}

	@Test
	public void testWrongId() {
		Assert.assertFalse(val.validate(EntityNames.TEACHER, Collections.singleton("Hammerhands")));
		Assert.assertFalse(val.validate(EntityNames.TEACHER, Collections.singleton("Nagas")));
		Assert.assertFalse(val.validate(EntityNames.TEACHER, Collections.singleton("Phantom Warriors")));
	}

	@Test
	public void testMultipleIds() {
		Assert.assertFalse(val.validate(EntityNames.TEACHER, new HashSet<String>(Arrays.asList(USER_ID, "Pikemen", "Pegasi"))));
	}
}

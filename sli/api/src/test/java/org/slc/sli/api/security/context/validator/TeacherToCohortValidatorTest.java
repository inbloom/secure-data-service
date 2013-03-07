package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
public class TeacherToCohortValidatorTest {

	private static final String USER_ID = "Master of Magic";

	@Resource
	private TeacherToCohortValidator val;

	@Resource
	private ValidatorTestHelper vth;

	@Resource
	private SecurityContextInjector injector;

	@Before
	public void init() {
		injector.setEducatorContext(USER_ID);
	}

	@Test
    public void testCanValidate() {
        assertFalse(val.canValidate(EntityNames.COHORT, false));
        assertTrue(val.canValidate(EntityNames.COHORT, true));
        assertFalse(val.canValidate(EntityNames.SECTION, true));
        assertFalse(val.canValidate(EntityNames.SECTION, false));
    }

	@Test(expected = IllegalArgumentException.class)
	public void testValidateWrongType() {
		val.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
	}

	@Test
	public void testCanAccessAll() {
		List<String> descs = Arrays.asList("Just Cause", "Armaggedon", "Awareness");

		Set<String> cohortIds = new HashSet<String>();
		for (String desc : descs) {
			cohortIds.add(this.generateCohortAndAssociate(USER_ID, desc));
		}

		Assert.assertTrue(val.validate(EntityNames.COHORT, cohortIds));
	}

	@Test
	public void testCannotAccessAll() {
		List<String> descs = Arrays.asList("Just Cause", "Armaggedon", "Awareness");

		Set<String> cohortIds = new HashSet<String>();
		for (String desc : descs) {
			cohortIds.add(this.generateCohort(USER_ID, desc));
		}

		Assert.assertFalse(val.validate(EntityNames.COHORT, cohortIds));

		for (String id : cohortIds) {
			Assert.assertFalse(val.validate(EntityNames.COHORT, Collections.singleton(id)));
		}

	}

	@Test
	public void testHeterogeneousList() {
		List<String> descs = Arrays.asList("Just Cause", "Armaggedon", "Awareness", "Chaos Mastery", "Life Mastery", "Death and Decay", "Node Mastery", "Artificer", "Warlord", "Conjurer");

		Set<String> cohortIds = new HashSet<String>();
		List<String> successes = new ArrayList<String>();
		for (String desc : descs) {
			if(Math.random()>0.5) {
				cohortIds.add(this.generateCohort(USER_ID, desc));
			}
			else {
				String id = this.generateCohortAndAssociate(USER_ID, desc);
				cohortIds.add(id);
				successes.add(id);
			}
		}

		Assert.assertFalse(val.validate(EntityNames.COHORT, cohortIds));

		for (String id : cohortIds) {
			if(successes.contains(id)) {
				Assert.assertTrue(val.validate(EntityNames.COHORT, Collections.singleton(id)));
			}
			else {
				Assert.assertFalse(val.validate(EntityNames.COHORT, Collections.singleton(id)));
			}
		}
	}

	// ==========================================================================

	private String generateCohortAndAssociate(String teacherId, String desc) {
		String id = this.generateCohort(teacherId, desc);
		this.vth.generateStaffCohort(teacherId, id, false, false);

		return id;
	}

	private String generateCohort(String teacherId, String desc) {
		Entity generated = vth.generateCohort("V2");
		generated.getBody().put("cohortDescription", desc);

		return generated.getEntityId();
	}

}

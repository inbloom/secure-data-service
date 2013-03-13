package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;


/**
 * Unit tests for staff/teacher --> cohort context validator.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericToCohortValidatorTest {

	private static final String USER_ID = "Master of Magic";
	
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
	private GenericToCohortValidator validator;

	@Autowired
	private ValidatorTestHelper helper;
	
    @Before
	public void init() {
		injector.setEducatorContext(USER_ID);
	}

    @Test
    public void testCanValidate() {
    	assertTrue(validator.canValidate(EntityNames.COHORT, false));
        assertFalse(validator.canValidate(EntityNames.COHORT, true));
        assertFalse(validator.canValidate(EntityNames.COMPETENCY_LEVEL_DESCRIPTOR, false));
        assertFalse(validator.canValidate(EntityNames.STAFF_COHORT_ASSOCIATION, true));
    }
    
	@Test(expected = IllegalArgumentException.class)
	public void testValidateWrongType() {
		validator.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
	}

	@Test
	public void testCanAccessAll() {
		List<String> descs = Arrays.asList("Just Cause", "Armaggedon", "Awareness");

		Set<String> cohortIds = new HashSet<String>();
		for (String desc : descs) {
			cohortIds.add(this.generateCohortAndAssociate(USER_ID, desc));
		}

		Assert.assertTrue(validator.validate(EntityNames.COHORT, cohortIds));
	}

	@Test
	public void testCannotAccessAll() {
		List<String> descs = Arrays.asList("Just Cause", "Armaggedon", "Awareness");

		Set<String> cohortIds = new HashSet<String>();
		for (String desc : descs) {
			cohortIds.add(this.generateCohort(USER_ID, desc));
		}

		Assert.assertFalse(validator.validate(EntityNames.COHORT, cohortIds));

		for (String id : cohortIds) {
			Assert.assertFalse(validator.validate(EntityNames.COHORT, Collections.singleton(id)));
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

		Assert.assertFalse(validator.validate(EntityNames.COHORT, cohortIds));

		for (String id : cohortIds) {
			if(successes.contains(id)) {
				Assert.assertTrue(validator.validate(EntityNames.COHORT, Collections.singleton(id)));
			}
			else {
				Assert.assertFalse(validator.validate(EntityNames.COHORT, Collections.singleton(id)));
			}
		}
	}

	// ==========================================================================

	private String generateCohortAndAssociate(String teacherId, String desc) {
		String id = this.generateCohort(teacherId, desc);
		this.helper.generateStaffCohort(teacherId, id, false, true);

		return id;
	}

	private String generateCohort(String teacherId, String desc) {
		Entity generated = helper.generateCohort("V2");
		generated.getBody().put("cohortDescription", desc);

		return generated.getEntityId();
	}
	
    @Test
    public void testCanNotValidateStudentRecordFlag() {
    	Set<String> cohortIds = new HashSet<String>();
        Entity lea = helper.generateEdorgWithParent(null);
        Entity school = helper.generateEdorgWithParent(lea.getEntityId());
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity cohort = helper.generateCohort(lea.getEntityId());
        cohortIds.add(cohort.getEntityId());
        helper.generateStaffCohort(helper.STAFF_ID, cohort.getEntityId(), false, false);
        assertFalse(validator.validate(EntityNames.COHORT, cohortIds));
    }

}

package org.slc.sli.api.security.context.validator;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherToStaffCohortAssociationValidatorTest {

    private static final String USER_ID = "Master of Magic";

    @Resource
    private TeacherToStaffCohortAssociationValidator val;

    @Resource
    private ValidatorTestHelper vth;

    @Resource
    private SecurityContextInjector injector;

    @Before
    public void init() {
        injector.setEducatorContext(USER_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateWrongType() {
        val.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
    }

    @Test
    public void testCanAccessAll() {
        List<String> ids = Arrays.asList("Just Cause", "Armaggedon", "Awareness");

        Set<String> sca = new HashSet<String>();
        for (String id : ids) {
            sca.add(this.vth.generateStaffCohort(USER_ID, id, false, true).getEntityId());
        }

        Assert.assertTrue(val.validate(EntityNames.STAFF_COHORT_ASSOCIATION, sca));
    }

    @Test
    public void testCannotAccessAll() {
        List<String> ids = Arrays.asList("Just Cause", "Armaggedon", "Awareness");

        Set<String> sca = new HashSet<String>();
        for (String id : ids) {
            sca.add(this.vth.generateStaffCohort("Sky Drake", id, false, true).getEntityId());
        }

        Assert.assertFalse(val.validate(EntityNames.STAFF_COHORT_ASSOCIATION, sca));

        for (String id : sca) {
            Assert.assertFalse(val.validate(EntityNames.STAFF_COHORT_ASSOCIATION, Collections.singleton(id)));
        }

    }

    @Test
    public void testHeterogeneousList() {
        List<String> ids = Arrays.asList("Just Cause", "Armaggedon", "Awareness", "Chaos Mastery", "Life Mastery", "Death and Decay", "Node Mastery", "Artificer", "Warlord", "Conjurer");

        Set<String> sca = new HashSet<String>();
        List<String> successes = new ArrayList<String>();
        for (String id : ids) {
            if (Math.random() > 0.33) {
                sca.add(this.vth.generateStaffCohort("Sky Drake", id, false, true).getEntityId());
            } else if (Math.random() > 0.5) {
                String id2 = this.vth.generateStaffCohort(USER_ID, id, false, true).getEntityId();
                sca.add(id2);
                successes.add(id2);
            } else {
                sca.add("Earth Elemental");
            }
        
        }

        Assert.assertFalse(val.validate(EntityNames.STAFF_COHORT_ASSOCIATION, sca));

        for (String id : sca) {
            if (successes.contains(id)) {
                Assert.assertTrue(val.validate(EntityNames.STAFF_COHORT_ASSOCIATION, Collections.singleton(id)));
            } else {
                Assert.assertFalse(val.validate(EntityNames.STAFF_COHORT_ASSOCIATION, Collections.singleton(id)));
            }
        }
    }

}
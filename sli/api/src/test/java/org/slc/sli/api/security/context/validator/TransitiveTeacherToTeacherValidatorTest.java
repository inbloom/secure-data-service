package org.slc.sli.api.security.context.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.MockRepo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TransitiveTeacherToTeacherValidatorTest {

    private static final String ED_ORG = "Myrran";

    private static final String USER_ID = "Master of Magic";

    @Resource
    private TransitiveTeacherToTeacherValidator val;

    @Resource
    private ValidatorTestHelper vth;

    @Resource
    private SecurityContextInjector injector;
    
    @Resource
    private MockRepo repo;

    @Before
    public void init() {
        injector.setEducatorContext(USER_ID);
    }
    
    @After
    public void teardown() {
        repo.deleteAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateWrongType() {
        val.validate(EntityNames.ASSESSMENT, new HashSet<String>(Arrays.asList("Jomolungma")));
    }

    @Test
    public void testCanAccessAll() {
        vth.generateStaffEdorg(USER_ID, ED_ORG, false);
        Set<String> teacherIds = new HashSet<String>(Arrays.asList("Just Cause", "Armaggedon", "Awareness"));

        for (String id : teacherIds) {
            vth.generateStaffEdorg(id, ED_ORG, false);
        }

        Assert.assertTrue(val.validate(EntityNames.TEACHER, teacherIds));
    }

    @Test
    public void testCannotAccessAll() {
        Set<String> teacherIds = new HashSet<String>(Arrays.asList("Just Cause", "Armaggedon", "Awareness"));

        for (String id : teacherIds) {
            vth.generateStaffEdorg(id, ED_ORG, false);
        }

        Assert.assertFalse(val.validate(EntityNames.TEACHER, teacherIds));

        for (String id : teacherIds) {
            Assert.assertFalse(val.validate(EntityNames.TEACHER, Collections.singleton(id)));
        }

    }

    @Test
    public void testHeterogeneousList() {
        vth.generateStaffEdorg(USER_ID, ED_ORG, false);
        Set<String> teacherIds = new HashSet<String>(Arrays.asList("Just Cause", "Armaggedon", "Awareness", "Chaos Mastery", "Life Mastery", "Death and Decay", "Node Mastery", "Artificer", "Warlord", "Conjurer"));

        List<String> successes = new ArrayList<String>();
        for (String id : teacherIds) {
            if (Math.random() > 0.5) {
                vth.generateStaffEdorg(id, ED_ORG, false);
                successes.add(id);
            } else {
                vth.generateStaffEdorg(id, "Arcanus", false);
            }
        }

        Assert.assertFalse(val.validate(EntityNames.TEACHER, teacherIds));

        for (String id : teacherIds) {
            if (successes.contains(id)) {
                Assert.assertTrue(val.validate(EntityNames.TEACHER, Collections.singleton(id)));
            } else {
                Assert.assertFalse(val.validate(EntityNames.TEACHER, Collections.singleton(id)));
            }
        }
    }
    
    @Test
    public void testExpiredTeacher() {
        vth.generateStaffEdorg(USER_ID, ED_ORG, false);
        Set<String> teacherIds = new HashSet<String>(Arrays.asList("Just Cause"));
        
        for (String id : teacherIds) {
            vth.generateStaffEdorg(id, ED_ORG, true);
        }
        
        Assert.assertFalse(val.validate(EntityNames.TEACHER, teacherIds));
        for (String id : teacherIds) {
            vth.generateStaffEdorg(id, ED_ORG, false);
        }
        Assert.assertTrue(val.validate(EntityNames.TEACHER, teacherIds));
    }
}

package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the staff to education organization association validator.
 * 
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class StaffToEducationOrganizationValidatorTest {

    @Resource
    private StaffToEducationOrganizationAssociationValidator val;
    
    @Resource
    private SecurityContextInjector injector;
    
    @Before
    public void init() {
        injector.setStaffContext();
    }
    
    @Test
    public void testCanValidate() {
        Assert.assertTrue("Must be able to validate", val.canValidate(EntityNames.STAFF_ED_ORG_ASSOCIATION, false));
        Assert.assertFalse("Must not be able to validate", val.canValidate(EntityNames.ADMIN_DELEGATION, false));
    }
    
    @Test
    public void testValidation() {
        Assert.assertFalse(val.validate(EntityNames.STAFF_ED_ORG_ASSOCIATION,
                new HashSet<String>(Arrays.asList("lamb"))));
    }
}

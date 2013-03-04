package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TransitiveStaffToEdOrgValidatorTest {

    @Autowired
    private TransitiveStaffToEdOrgValidator validator;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private SecurityContextInjector injector;

    
    Entity edOrg = null;
    
    @Before
    public void setup() {
        edOrg = helper.generateEdorgWithParent(null);
        
        //make teacher
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);
        Entity entity = helper.generateStaff();
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, "111");
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, true));
        assertTrue(validator.canValidate(EntityNames.SCHOOL, true));
    }
    
    @Test
    public void testCannotValidate() {
        assertFalse(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, false));
        assertFalse(validator.canValidate(EntityNames.SCHOOL, false));
        
        injector.setEducatorContext();
        assertFalse(validator.canValidate(EntityNames.EDUCATION_ORGANIZATION, true));
        assertFalse(validator.canValidate(EntityNames.SCHOOL, true));
    }
    
    @Test
    public void testValidate() {
        assertTrue(validator.validate(EntityNames.EDUCATION_ORGANIZATION, 
                new HashSet<String>(Arrays.asList(edOrg.getEntityId()))));
    }
    
    @Test
    public void testValidateEmpty() {
        assertFalse(validator.validate(EntityNames.EDUCATION_ORGANIZATION, 
                new HashSet<String>()));
    }
}

package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CommonValidatorTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private SecurityContextInjector injector;
    

    /**
     * Test every staff validator to make sure it won't validate a bad ID
     * @throws Exception
     */
    @Test
    public void testStaffValidatingBadId() throws Exception {
        setupCurrentUser(new MongoEntity("staff", new HashMap<String, Object>()));
        Collection<IContextValidator> validators = applicationContext.getBeansOfType(IContextValidator.class).values();
        for (IContextValidator validator : validators) {
            for (String entityName : getEntityNames()) {
                if (validator.canValidate(entityName, true) || validator.canValidate(entityName, false))  {
                    assertFalse(validator + " for " + entityName + " shouldn't validate a non-existent id",
                            validator.validate(entityName, new HashSet<String>(Arrays.asList("bad-entity-id"))));
                }

            }

        }

    }
    
    private void setupCurrentUser(Entity staff) {
        // Set up the principal
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, "111");
    }


    /**
     * Get all the available entity names
     * @return
     * @throws Exception
     */
    private List<String> getEntityNames() throws Exception {
        List<String> entityNames = new ArrayList<String>();
        Field[] fields = EntityNames.class.getDeclaredFields();

        for (Field f: fields) {
            if (f.getType() == String.class && Modifier.isStatic(f.getModifiers())) {
                entityNames.add((String) f.get(null));
            }
        }
        return entityNames;
    }

}

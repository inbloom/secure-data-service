package org.slc.sli.api.security.roles;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjection;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Simple test to test getting roles and permissions back.
 *
 * Doesn't test posting new roles at this time, but you can create roles.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RolesAndPermissionsResourceTest {

    @Autowired
    private RolesAndPermissionsResource api;

    private Map<String, Object> createTestRole() {
        Map<String, Object> role = new HashMap<String, Object>();
        List<String> permissions = new ArrayList<String>();
        permissions.add(Right.READ_RESTRICTED.getRight());
        permissions.add(Right.WRITE_RESTRICTED.getRight());
        role.put("name", "Role1");
        role.put("rights", permissions);
        return role;
    }
    
    @Before
    public void setUp() {
        // inject administrator security context for unit testing
        SecurityContextInjection.setAdminContext();
    }
    
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetDefaultRoles() throws Exception {
        List<Map<String, Object>> result = api.getRolesAndPermissions();
        assertTrue(result.size() >= 4);
    }

    @Test
    public void testGetRolesAndPermissions() {
        //Create a role.
        Map<String, Object> role = createTestRole();
        assertNotNull(role);
        assertNotNull(api);
        assertNotNull(role.get("name"));
        assertNotNull(role.get("rights"));
        api.createRoleWithPermission((String) role.get("name"), role.get("rights"));
        
        //Fetch it back out.
        List<Map<String, Object>> result = api.getRolesAndPermissions();
        assertNotNull(result);
        assertTrue(result.size() == 5);
    }
}

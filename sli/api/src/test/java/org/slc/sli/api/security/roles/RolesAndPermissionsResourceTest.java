package org.slc.sli.api.security.roles;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
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
    RoleRightAccess mockRoles;

    @Autowired
    private SecurityContextInjector securityContextInjector;


    private EntityBody createTestRole() {
        EntityBody role = new EntityBody();
        List<String> permissions = new ArrayList<String>();
        permissions.add(Right.READ_RESTRICTED.toString());
        permissions.add(Right.WRITE_RESTRICTED.toString());
        role.put("name", "Role1");
        role.put("rights", permissions);
        return role;
    }
    
    @Before
    public void setUp() {
        // inject administrator security context for unit testing
        securityContextInjector.setAdminContext();
        mockRoles = mock(RoleRightAccess.class);
        api.setRoleAccessor(mockRoles);
    }
    
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetDefaultRoles() throws Exception {
        List<Role> roles = getRoles();
        when(mockRoles.fetchAllRoles()).thenReturn(roles);
        List<Map<String, Object>> result = api.getRolesAndPermissions();
        assertTrue(result.size() >= 4);
    }

    private List<Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        roles.add(RoleBuilder.makeRole("Something").build());
        roles.add(RoleBuilder.makeRole("Something Else").build());
        roles.add(RoleBuilder.makeRole("Something Else too").build());
        roles.add(RoleBuilder.makeRole("Something even more").build());
        return roles;
    }

//    @Test
//    public void testCreateRole() {
//        //Create a role.
//        EntityBody role = createTestRole();
//        assertNotNull(role);
//        assertNotNull(api);
//        assertNotNull(role.get("name"));
//        assertNotNull(role.get("rights"));
//        when(mockRoles.addRole(RoleBuilder.makeRole(role).build())).thenReturn(true);
//
//        boolean roleWithPermission = api.createRoleWithPermission((String) role.get("name"), (List<String>) role.get("rights"));
//        assertTrue(roleWithPermission);
//    }
}

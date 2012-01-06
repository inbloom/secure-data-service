package org.slc.sli.api.security.roles;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Set of tests for the basic RoleRightsAccessImpl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class BasicRoleRightAccessImplTest {

    @Autowired
    private BasicRoleRightAccessImpl access;
    
    private EntityService mockService;

    private EntityBody roleBody;
    
    @Before
    public void setUp() throws Exception {
        List<String> ids = new ArrayList<String>();
        mockService = mock(EntityService.class);
        access.setService(mockService);

        List<String> rights = new ArrayList<String>();
        rights.add("READ_MONKEYS");
        rights.add("WRITE_MONKEYS");
        ids.add("EducatorID");
        ids.add("LeaderID");
        ids.add("AggregatorID");
        ids.add("BadID");
        roleBody = new EntityBody();
        roleBody.put("name", "Educator");
        roleBody.put("rights", rights);
        when(mockService.list(0, 100)).thenReturn(ids);
    }

    @Test
    public void testFindRoleByName() throws Exception {
        Role testRole = null;
        //Find a role that does exist
        when(mockService.get("EducatorID")).thenReturn(roleBody);
        testRole = access.findRoleByName("Educator");
        assertNotNull(testRole);

        //Find a role that doesn't exist.
        testRole = access.findRoleByName("Waffles");
        assertNull(testRole);

    }

    @Test
    public void testFindRoleBySpringName() throws Exception {
        Role testRole = null;
        //Find a role that does exist
        when(mockService.get("EducatorID")).thenReturn(roleBody);
        testRole = access.findRoleBySpringName("ROLE_EDUCATOR");
        assertNotNull(testRole);

        //Find a role that doesn't exist.
        testRole = access.findRoleByName("ROLE_WAFFLES");
        assertNull(testRole);

    }

    @Test
    public void testFetchAllRoles() throws Exception {
        when(mockService.get("EducatorID")).thenReturn(roleBody);
        when(mockService.get("LeaderID")).thenReturn(roleBody);
        when(mockService.get("AggregatorID")).thenReturn(roleBody);
        when(mockService.get("BadID")).thenReturn(roleBody);
        List<Role> roles = access.fetchAllRoles();
        assertNotNull(roles);
        assertTrue(roles.size() == 4);
    }

    @Test
    public void testGetRightByName() throws Exception {
        fail("Not implemented");

    }

    @Test
    public void testFetchAllRights() throws Exception {
        fail("Not implemented");

    }

    @Test
    public void testAddRole() throws Exception {
        Role tempRole = new Role("Somebody");
        tempRole.addRight(Right.AGGREGATE_READ);
        assertTrue(access.addRole(tempRole));
    }

    @Test
    public void testDeleteRole() throws Exception {
        //Delete a role that exists.
        Role tempRole = new Role("Somebody");
        tempRole.setId("BadID");
        tempRole.addRight(Right.READ_RESTRICTED);
        assertTrue(access.deleteRole(tempRole));
        
        //Can't delete a role that doesn't exist now.
        assertFalse(access.deleteRole(tempRole));

    }

    @Test
    public void testUpdateRole() throws Exception {
        //Update a role that exists
        Role tempRole = new Role("Somebody");
        tempRole.setId("BadID");
        tempRole.addRight(Right.READ_RESTRICTED);
        assertTrue(access.updateRole(tempRole));

        //Update a role that doesn't exist.
        tempRole.setId("BLAHBLAH");
        assertFalse(access.updateRole(tempRole));

    }
}

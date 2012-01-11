package org.slc.sli.api.security.roles;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Set of tests for the basic RoleRightsAccessImpl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultRoleRightAccessImplTest {

    @Autowired
    private DefaultRoleRightAccessImpl access;

    @Autowired
    private SecurityContextInjector securityContextInjector;
    
    private EntityService mockService;

    @Before
    public void setUp() throws Exception {

        securityContextInjector.setAdminContext();
        List<String> ids = new ArrayList<String>();
        mockService = mock(EntityService.class);
        access.setService(mockService);
        
        ids.add("EducatorID");
        ids.add("LeaderID");
        ids.add("AggregatorID");
        ids.add("BadID");
        ids.add("ITID");

        when(mockService.get("EducatorID")).thenReturn(getEntityBody());
        when(mockService.get("LeaderID")).thenReturn(getEntityBody());
        when(mockService.get("AggregatorID")).thenReturn(getEntityBody());
        when(mockService.get("BadID")).thenReturn(getEntityBody());
        when(mockService.get("ITID")).thenReturn(getITEntityBody());

        when(mockService.list(0, 100)).thenReturn(ids);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    private EntityBody getEntityBody() {
        EntityBody roleBody;
        List<String> rights = new ArrayList<String>();
        rights.add(Right.READ_GENERAL.toString());
        rights.add(Right.READ_RESTRICTED.toString());
        roleBody = new EntityBody();
        roleBody.put("name", "Educator");
        roleBody.put("rights", rights);
        return roleBody;
    }
    private EntityBody getITEntityBody() {
        EntityBody roleBody;
        List<String> rights = new ArrayList<String>();
        rights.add(Right.READ_GENERAL.toString());
        rights.add(Right.READ_RESTRICTED.toString());
        roleBody = new EntityBody();
        roleBody.put("name", "IT Administrator");
        roleBody.put("rights", rights);
        return roleBody;
    }

    @Test
    public void testFindRoleByName() throws Exception {
        Role testRole = null;
        //Find a role that does exist
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
        testRole = access.findRoleBySpringName("ROLE_EDUCATOR");
        assertNotNull(testRole);

        //Find a role that doesn't exist.
        testRole = access.findRoleByName("ROLE_WAFFLES");
        assertNull(testRole);

    }

    @Test
    public void testFetchAllRoles() throws Exception {

        List<Role> roles = access.fetchAllRoles();
        assertNotNull(roles);
        assertTrue(roles.size() == 5);
    }

    @Test
    public void testAddRole() throws Exception {
        Role tempRole = new Role("Somebody");
        tempRole.addRight(Right.AGGREGATE_READ);
        when(mockService.create(tempRole.getRoleAsEntityBody())).thenReturn("Something");
        assertTrue(access.addRole(tempRole));

        //TODO fail case?
    }

    @Test
    public void testDeleteRole() throws Exception {
        //Delete a role that exists.
        Role tempRole = new Role("Somebody");
        tempRole.setId("BadID");
        tempRole.addRight(Right.READ_RESTRICTED);
        assertTrue(access.deleteRole(tempRole));

        //Can't delete a role that doesn't exist now.
        doThrow(new EntityNotFoundException(tempRole.getId())).when(mockService).delete(tempRole.getId());
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
        doThrow(new EntityNotFoundException(tempRole.getId())).when(mockService).update(tempRole.getId(), tempRole.getRoleAsEntityBody());
        assertFalse(access.updateRole(tempRole));

    }

    @Test
    public void testGetDefaultRole() throws Exception {
        //Valid default role.
        assertTrue(access.getDefaultRole(DefaultRoleRightAccessImpl.EDUCATOR).getName().equals("Educator"));
        //Invalid default role.
        assertNull(access.getDefaultRole("Monkeys"));
    }
}

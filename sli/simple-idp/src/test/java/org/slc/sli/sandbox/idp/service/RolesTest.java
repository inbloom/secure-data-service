package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.RoleService.Role;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class RolesTest {
    
    RoleService roleService = new RoleService();
    
    @Test
    public void testGetAvailableRoles() {
        List<Role> roles = roleService.getAvailableRoles();
        assertNotNull(roles);
        assertEquals(4, roles.size());
        
        Set<String> values = new HashSet<String>();
        for (Role role : roles) {
            values.add(role.getName());
            values.add(role.getId());
        }
        assertTrue(values.contains("IT Administrator"));
        assertTrue(values.contains("Educator"));
        assertTrue(values.contains("Aggregator"));
        assertTrue(values.contains("Leader"));
    }
}

package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.resolve.RealmRoleMappingException;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the DefaultClientRoleManager class
 * 
 * @author jnanney
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class DefaultClientRoleManagerTest {
    
    @Autowired
    private DefaultClientRoleManager roleManager;
    
    private static final String MOCK_REALM_ID = "mock_realm_id";
    private List<String> allRoles;
    
    @Before
    public void setUp() {
        allRoles = new ArrayList<String>();
        allRoles.add(SecureRoleRightAccessImpl.AGGREGATOR);
        allRoles.add(SecureRoleRightAccessImpl.EDUCATOR);
        allRoles.add(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        allRoles.add(SecureRoleRightAccessImpl.LEADER);
        
        try {
            roleManager.addClientRole(MOCK_REALM_ID, SecureRoleRightAccessImpl.EDUCATOR, "Coach");
            roleManager.addClientRole(MOCK_REALM_ID, SecureRoleRightAccessImpl.EDUCATOR, "Math Teacher");
            roleManager.addClientRole(MOCK_REALM_ID, SecureRoleRightAccessImpl.IT_ADMINISTRATOR, "Moss");
        } catch (RealmRoleMappingException e) {
            // TODO Auto-generated catch block
            return;
        }
        
    }
    
    @Test
    public void testAddClientRole() {
        try {
            roleManager.addClientRole(MOCK_REALM_ID, SecureRoleRightAccessImpl.LEADER, "Superintendent");
        } catch (RealmRoleMappingException e) {
            // TODO Auto-generated catch block
            return;
        }
        String sliRole = roleManager.getSliRoleName(MOCK_REALM_ID, "Superintendent");
        Assert.assertNotNull(sliRole);
        Assert.assertTrue(sliRole.equals(SecureRoleRightAccessImpl.LEADER));
    }
    
    @Test
    public void testDeleteClientRole() {
        Assert.assertEquals(SecureRoleRightAccessImpl.EDUCATOR, roleManager.getSliRoleName(MOCK_REALM_ID, "Coach"));
        roleManager.deleteClientRole(MOCK_REALM_ID, "Coach");
        List<String> mappings = roleManager.getMappings(MOCK_REALM_ID, SecureRoleRightAccessImpl.EDUCATOR);
        Assert.assertNotNull(mappings);
        Assert.assertTrue(mappings.size() > 0);
    }
}

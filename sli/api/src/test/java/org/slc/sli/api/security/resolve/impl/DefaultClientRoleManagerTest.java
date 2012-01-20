package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import org.slc.sli.api.security.roles.DefaultRoleRightAccessImpl;
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

	@Autowired
    private DefaultRoleRightAccessImpl mockAccess;

	private static final String MOCK_REALM_ID = "mock_realm_id";
	private List<String> allRoles;

	@Before
	public void setUp() {
		allRoles = new ArrayList<String>();
		allRoles.add(DefaultRoleRightAccessImpl.AGGREGATOR);
		allRoles.add(DefaultRoleRightAccessImpl.EDUCATOR);
		allRoles.add(DefaultRoleRightAccessImpl.IT_ADMINISTRATOR);
		allRoles.add(DefaultRoleRightAccessImpl.LEADER);
		
		roleManager.addClientRole(MOCK_REALM_ID, DefaultRoleRightAccessImpl.EDUCATOR, "Coach");
		roleManager.addClientRole(MOCK_REALM_ID, DefaultRoleRightAccessImpl.EDUCATOR, "Math Teacher");
		
		roleManager.addClientRole(MOCK_REALM_ID, DefaultRoleRightAccessImpl.IT_ADMINISTRATOR, "Moss");

	}

	@Test
	public void testAddClientRole() {
		roleManager.addClientRole(MOCK_REALM_ID, DefaultRoleRightAccessImpl.LEADER, "Superintendent");
		String sliRole = roleManager.getSliRoleName(MOCK_REALM_ID, "Superintendent");
		Assert.assertNotNull(sliRole);
		Assert.assertTrue(sliRole.equals(DefaultRoleRightAccessImpl.LEADER));
 	}

	@Test
	public void testDeleteClientRole() {
		Assert.assertEquals(DefaultRoleRightAccessImpl.EDUCATOR, roleManager.getSliRoleName(MOCK_REALM_ID, "Coach"));
		roleManager.deleteClientRole(MOCK_REALM_ID, "Coach");
		List<String> mappings = roleManager.getMappings(MOCK_REALM_ID, DefaultRoleRightAccessImpl.EDUCATOR);
		Assert.assertNotNull(mappings);
		Assert.assertTrue(mappings.size() > 0);
		
	}
	
}

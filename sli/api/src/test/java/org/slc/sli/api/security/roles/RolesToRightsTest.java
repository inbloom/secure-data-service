package org.slc.sli.api.security.roles;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.resolve.ClientRoleManager;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Tests default role to rights resolution pipeline
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class RolesToRightsTest {
    
    @Autowired
    private DefaultRolesToRightsResolver resolver;
    @Autowired
    private RoleRightAccess mockAccess;
    @Autowired
    private ClientRoleManager mockRoleManager;
    
    private static final String DEFAULT_REALM_ID = "dc=slidev,dc=net";
    
    @Before
    public void setUp() throws Exception {
        mockAccess = mock(RoleRightAccess.class);
        mockRoleManager = mock(ClientRoleManager.class);
        
        resolver.setRoleRightAccess(mockAccess);
        resolver.setRoleMapper(mockRoleManager);
        
        when(
                mockRoleManager.resolveRoles(DEFAULT_REALM_ID,
                        Arrays.asList(InsecureRoleRightAccessImpl.EDUCATOR, InsecureRoleRightAccessImpl.AGGREGATOR)))
                .thenReturn(Arrays.asList(InsecureRoleRightAccessImpl.EDUCATOR, InsecureRoleRightAccessImpl.AGGREGATOR));
        when(
                mockRoleManager.resolveRoles(DEFAULT_REALM_ID, Arrays.asList(InsecureRoleRightAccessImpl.EDUCATOR,
                        InsecureRoleRightAccessImpl.AGGREGATOR, "bad", "doggie"))).thenReturn(
                Arrays.asList(InsecureRoleRightAccessImpl.EDUCATOR, InsecureRoleRightAccessImpl.AGGREGATOR));
        when(mockAccess.getDefaultRole(InsecureRoleRightAccessImpl.EDUCATOR)).thenReturn(buildRole());
        when(mockAccess.getDefaultRole(InsecureRoleRightAccessImpl.AGGREGATOR)).thenReturn(buildRole());
        when(mockAccess.getDefaultRole("bad")).thenReturn(null);
        when(mockAccess.getDefaultRole("doggie")).thenReturn(null);
        when(mockAccess.getDefaultRole("Pink")).thenReturn(null);
        when(mockAccess.getDefaultRole("Goo")).thenReturn(null);
    }
    
    private Role buildRole() {
        return RoleBuilder.makeRole(InsecureRoleRightAccessImpl.EDUCATOR).addRight(Right.AGGREGATE_READ).build();
    }
    
    @Test
    public void testMappedRoles() throws Exception {
        
        Set<GrantedAuthority> rights = resolver.resolveRoles(DEFAULT_REALM_ID,
                Arrays.asList(InsecureRoleRightAccessImpl.EDUCATOR, InsecureRoleRightAccessImpl.AGGREGATOR));
        Assert.assertTrue(rights.size() > 0);
    }
    
    @Test
    public void testBadRoles() throws Exception {
        Set<GrantedAuthority> authorities = resolver.resolveRoles(DEFAULT_REALM_ID, Arrays.asList("Pink", "Goo"));
        Assert.assertTrue("Authorities must be empty", authorities.size() == 0);
    }
    
    @Test
    public void testMixedRoles() throws Exception {
        Set<GrantedAuthority> authorities = resolver.resolveRoles(DEFAULT_REALM_ID, Arrays.asList(
                InsecureRoleRightAccessImpl.EDUCATOR, InsecureRoleRightAccessImpl.AGGREGATOR, "bad", "doggie"));
        Assert.assertTrue(authorities.size() > 0);
    }
}

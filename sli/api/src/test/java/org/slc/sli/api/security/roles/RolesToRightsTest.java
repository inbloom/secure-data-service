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

import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.enums.Right;

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
    private ClientRoleResolver mockRoleManager;
    
    private static final String DEFAULT_REALM_ID = "dc=slidev,dc=net";
    
    @Before
    public void setUp() throws Exception {
        mockAccess = mock(RoleRightAccess.class);
        mockRoleManager = mock(ClientRoleResolver.class);
        
        resolver.setRoleRightAccess(mockAccess);
        resolver.setRoleMapper(mockRoleManager);
        
        when(
                mockRoleManager.resolveRoles(DEFAULT_REALM_ID,
                        Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR)))
                        .thenReturn(Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR));
        when(
                mockRoleManager.resolveRoles(DEFAULT_REALM_ID, Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR,
                        SecureRoleRightAccessImpl.AGGREGATOR, "bad", "doggie"))).thenReturn(
                                Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR));
        when(mockAccess.getDefaultRole(SecureRoleRightAccessImpl.EDUCATOR)).thenReturn(buildRole());
        when(mockAccess.getDefaultRole(SecureRoleRightAccessImpl.AGGREGATOR)).thenReturn(buildRole());
        when(mockAccess.getDefaultRole("bad")).thenReturn(null);
        when(mockAccess.getDefaultRole("doggie")).thenReturn(null);
        when(mockAccess.getDefaultRole("Pink")).thenReturn(null);
        when(mockAccess.getDefaultRole("Goo")).thenReturn(null);
    }
    
    private Role buildRole() {
        return RoleBuilder.makeRole(SecureRoleRightAccessImpl.EDUCATOR).addRight(Right.AGGREGATE_READ).build();
    }
    
    @Test
    public void testMappedRoles() throws Exception {
        
        Set<GrantedAuthority> rights = resolver.resolveRoles(DEFAULT_REALM_ID,
                Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR));
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
                SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR, "bad", "doggie"));
        Assert.assertTrue(authorities.size() > 0);
    }
}

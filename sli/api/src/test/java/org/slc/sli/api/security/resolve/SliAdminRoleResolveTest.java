package org.slc.sli.api.security.resolve;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Verifies that {@link DefaultRolesToRightsResolver} filters out SLI Admins properly if
 * not coming from a valid admin realm.
 * 
 * @author pwolf
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@DirtiesContext
public class SliAdminRoleResolveTest {
    
    @Autowired
    private DefaultRolesToRightsResolver resolver;
    
    private static final String ADMIN_REALM_NAME = "dc=adminrealm,dc=org";
    private static final String NON_ADMIN_REALM_NAME = "dc=sea,dc=org";
    
    private List<String> rolesContainingAdmin = null;
    private List<String> rolesNotContainingAdmin = null;
    
    @Before
    public void setUp() throws Exception {
        
        SliAdminValidator validator = mock(SliAdminValidator.class);
        resolver.setSliAdminValidator(validator);
        when(validator.isSliAdminRealm(ADMIN_REALM_NAME)).thenReturn(true);
        when(validator.isSliAdminRealm("")).thenReturn(false);
        
        RoleRightAccess rra = mock(RoleRightAccess.class);
        
        // Define educator per RoleInitializer
        when(rra.getDefaultRole(RoleInitializer.EDUCATOR)).thenReturn(
                RoleBuilder.makeRole(RoleInitializer.EDUCATOR)
                        .addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL }).build());
        
        // Define sli admin per RoleInitializer
        when(rra.getDefaultRole(RoleInitializer.SLI_ADMINISTRATOR)).thenReturn(
                RoleBuilder.makeRole(RoleInitializer.SLI_ADMINISTRATOR).addRights(new Right[] { Right.READ_ROLES })
                        .build());
        
        resolver.setRoleRightAccess(rra);
        rolesContainingAdmin = new ArrayList<String>();
        rolesContainingAdmin.add(RoleInitializer.EDUCATOR);
        rolesContainingAdmin.add(RoleInitializer.SLI_ADMINISTRATOR);
        
        rolesNotContainingAdmin = new ArrayList<String>();
        rolesNotContainingAdmin.add(RoleInitializer.EDUCATOR);
        
    }
    
    @Test
    public void testAdminInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(ADMIN_REALM_NAME, rolesContainingAdmin);
        Assert.assertTrue(roles.contains(Right.READ_ROLES));
    }
    
    @Test
    public void testAdminNotInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(NON_ADMIN_REALM_NAME, rolesNotContainingAdmin);
        Assert.assertFalse(roles.contains(Right.READ_ROLES));
    }
    
    @Test
    public void testNonAdminInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(ADMIN_REALM_NAME, rolesNotContainingAdmin);
        Assert.assertFalse(roles.contains(Right.READ_ROLES));
    }
    
    @Test
    public void testNonAdminNotInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(NON_ADMIN_REALM_NAME, rolesNotContainingAdmin);
        Assert.assertFalse(roles.contains(Right.READ_ROLES));
    }
    
}

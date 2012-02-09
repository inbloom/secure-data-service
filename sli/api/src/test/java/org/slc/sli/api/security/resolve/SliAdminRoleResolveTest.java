package org.slc.sli.api.security.resolve;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.domain.enums.Right;

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
    
    private static final String          ADMIN_REALM_NAME        = "dc=adminrealm,dc=org";
    private static final String          NON_ADMIN_REALM_NAME    = "dc=sea,dc=org";
    
    private List<String>                 rolesContainingAdmin    = null;
    private List<String>                 rolesNotContainingAdmin = null;
    
    @Autowired
    private MockRepo                     mockRepo;
    
    @Before
    public void setUp() throws Exception {
        
        SliAdminValidator validator = mock(SliAdminValidator.class);
        resolver.setSliAdminValidator(validator);
        when(validator.isSliAdminRealm(ADMIN_REALM_NAME)).thenReturn(true);
        when(validator.isSliAdminRealm("")).thenReturn(false);
        
        RoleRightAccess rra = mock(RoleRightAccess.class);
        mockRepo.create("realm", buildRealm());
        // Define educator per RoleInitializer
        when(rra.getDefaultRole(RoleInitializer.EDUCATOR)).thenReturn(RoleBuilder.makeRole(RoleInitializer.EDUCATOR).addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL }).build());
        
        // Define sli admin per RoleInitializer
        when(rra.getDefaultRole(RoleInitializer.SLI_ADMINISTRATOR)).thenReturn(RoleBuilder.makeRole(RoleInitializer.SLI_ADMINISTRATOR).addRights(new Right[] { Right.ADMIN_ACCESS }).build());
        
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
        Assert.assertTrue(roles.contains(Right.ADMIN_ACCESS));
    }
    
    @Test
    public void testAdminNotInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(NON_ADMIN_REALM_NAME, rolesNotContainingAdmin);
        Assert.assertFalse(roles.contains(Right.ADMIN_ACCESS));
    }
    
    @Test
    public void testNonAdminInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(ADMIN_REALM_NAME, rolesNotContainingAdmin);
        Assert.assertFalse(roles.contains(Right.ADMIN_ACCESS));
    }
    
    @Test
    public void testNonAdminNotInAdminRealm() {
        Set<GrantedAuthority> roles = resolver.resolveRoles(NON_ADMIN_REALM_NAME, rolesNotContainingAdmin);
        Assert.assertFalse(roles.contains(Right.ADMIN_ACCESS));
    }
    
    private Map<String, Object> buildRealm() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("state", "Admin Realm");
        result.put("realm", "dc=adminrealm,dc=org");
        Map<String, List<Map<String, Object>>> mappings = new HashMap<String, List<Map<String, Object>>>();
        
        ArrayList<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
        mappings.put("role", roles);
        
        roles.add(createRole(RoleInitializer.SLI_ADMINISTRATOR, new ArrayList<String>(Arrays.asList(RoleInitializer.SLI_ADMINISTRATOR))));
        roles.add(createRole(RoleInitializer.EDUCATOR, new ArrayList<String>(Arrays.asList(RoleInitializer.EDUCATOR))));
        
        /*
         * mappings.put(RoleInitializer.SLI_ADMINISTRATOR, Arrays.asList(new String[] { RoleInitializer.SLI_ADMINISTRATOR }));
         * mappings.put(RoleInitializer.AGGREGATE_VIEWER, Arrays.asList(new String[] { RoleInitializer.AGGREGATE_VIEWER }));
         * mappings.put(RoleInitializer.IT_ADMINISTRATOR, Arrays.asList(new String[] { RoleInitializer.IT_ADMINISTRATOR }));
         * mappings.put(RoleInitializer.LEADER, Arrays.asList(new String[] { RoleInitializer.LEADER }));
         * mappings.put(RoleInitializer.EDUCATOR, Arrays.asList(new String[] { RoleInitializer.EDUCATOR }));
         */
        result.put("mappings", mappings);
        return result;
    }
    
    private Map<String, Object> createRole(String sliRoleName, List<String> clientRoleName) {
        Map<String, Object> role = new HashMap<String, Object>();
        role.put("sliRoleName", sliRoleName);
        role.put("clientRoleName", clientRoleName);
        
        return role;
    }
}

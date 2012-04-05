package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * 
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApprovedApplicationResourceTest {
    
    @Autowired
    @InjectMocks
    private ApprovedApplicationResource resource;
    
    @Mock
    EntityService service;
    
    @Mock
    ApplicationAuthorizationValidator appValidator;
    
    EntityBody adminApp, userApp, disabledApp;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        adminApp = new EntityBody();
        adminApp.put("is_admin", true);
        adminApp.put("enabled", true);
        
        userApp = new EntityBody();
        userApp.put("is_admin", false);
        userApp.put("enabled", true);
        
        disabledApp = new EntityBody();
        disabledApp.put("is_admin", false);
        disabledApp.put("enabled", false);
        
        Mockito.when(service.get("adminAppId")).thenReturn(adminApp);
        Mockito.when(service.get("userAppId")).thenReturn(userApp);
        Mockito.when(service.get("disabledAppId")).thenReturn(disabledApp);
        Mockito.when(service.listIds(Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
    }
    
    @Test (expected = InsufficientAuthenticationException.class)
    public void testNotLoggedIn() {
        SecurityContextHolder.clearContext();
        resource.getApplications("");
    }
    
    @Test
    public void testAdminUser() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(null);
        setupAuth(Right.ADMIN_ACCESS);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertTrue(ents.contains(adminApp));
        assertTrue(ents.contains(userApp));
        assertFalse(ents.contains(disabledApp));
    }
    
    @Test
    public void testAdminUserFilterNonAdmin() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(null);
        setupAuth(Right.ADMIN_ACCESS);
        Response resp = resource.getApplications("false");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertFalse(ents.contains(adminApp));
        assertTrue(ents.contains(userApp));
        assertFalse(ents.contains(disabledApp));
    }
    
    @Test
    public void testAdminUserFilterAdmin() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(null);
        setupAuth(Right.ADMIN_ACCESS);
        Response resp = resource.getApplications("true");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertTrue(ents.contains(adminApp));
        assertFalse(ents.contains(userApp));
        assertFalse(ents.contains(disabledApp));
    }   
    
    @Test
    public void testNoneAllowed() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(new ArrayList<String>());
        setupAuth(Right.READ_GENERAL);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertFalse(ents.contains(adminApp));
        assertFalse(ents.contains(userApp));
        assertFalse(ents.contains(disabledApp));
    }
    
    private static void setupAuth(GrantedAuthority auth) {
        Authentication mockAuth = Mockito.mock(Authentication.class);
        ArrayList<GrantedAuthority> rights = new ArrayList<GrantedAuthority>();
        rights.add(auth);
        Mockito.when(mockAuth.getAuthorities()).thenReturn(rights);
        Mockito.when(mockAuth.getPrincipal()).thenReturn(new SLIPrincipal());
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
    }

}

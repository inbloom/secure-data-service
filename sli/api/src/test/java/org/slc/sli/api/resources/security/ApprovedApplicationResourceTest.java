package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    EntityBody adminApp, userApp, installedApp;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        adminApp = new EntityBody();
        adminApp.put("is_admin", true);
        adminApp.put("installed", false);
        
        //endpoint list
        List<Map<String, Object>> endpoints = new ArrayList<Map<String, Object>>();
        Map<String, Object> endpoint = new HashMap<String, Object>();
        endpoint.put("name", "myName");
        endpoint.put("description", "myDesc");
        endpoint.put("url", "http://url/");
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SLI Administrator");
        roles.add("LEA Administrator");
        endpoint.put("roles", roles);
        endpoints.add(endpoint);
        adminApp.put("endpoints", endpoints);
        adminApp.put("created_by", "slcdeveloper");
        adminApp.put("name", "Admin App");
        userApp = new EntityBody();
        userApp.put("is_admin", false);
        userApp.put("installed", false);
        userApp.put("created_by", "bob");
        userApp.put("name", "User App");
        installedApp = new EntityBody();
        installedApp.put("name", "Installed App");
        installedApp.put("is_admin", false);
        installedApp.put("installed", true);
        installedApp.put("created_by", "bob");
        Mockito.when(service.get("adminAppId")).thenReturn(adminApp);
        Mockito.when(service.get("userAppId")).thenReturn(userApp);
        Mockito.when(service.get("disabledAppId")).thenReturn(installedApp);
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

        setupAuth(Right.ADMIN_ACCESS, Arrays.asList("LEA Administrator"));
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertTrue(ents.contains(adminApp));
        assertFalse(ents.contains(userApp));
        assertFalse(ents.contains(installedApp));
    }
    
    @Test
    public void testEndpointFilteringNoRoles() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        setupAuth(Right.ADMIN_ACCESS, null);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        boolean foundAdmin = false;
        for (EntityBody body : ents) {
            
            if (body == adminApp) {
                foundAdmin = true;
            }
         }
        assertFalse("Admin should be filtered out since no endpoints are applicable", foundAdmin);
    }
    
    @Test
    public void testEndpointFilteringWithRole() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        
        ArrayList<String> myRoles = new ArrayList<String>();
        myRoles.add("SLI Administrator");
        setupAuth(Right.ADMIN_ACCESS, myRoles);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        boolean foundAdmin = false;
        for (EntityBody body : ents) {
            
            if (body == adminApp) {
                foundAdmin = true;
                assertTrue("endpoint found", ((List) body.get("endpoints")).size() == 1);
            }
         }
        assertTrue(foundAdmin);
    }
    
    @Test
    public void testEndpointNoRolesRequired() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        
        Map endpoint = (Map) ((List) adminApp.get("endpoints")).get(0);
        endpoint.put("roles", new ArrayList<String>()); //no roles means never filter
        setupAuth(Right.ADMIN_ACCESS, null);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        boolean foundAdmin = false;
        for (EntityBody body : ents) { 
            if (body == adminApp) {
                foundAdmin = true;
                assertTrue("endpoint found", ((List) body.get("endpoints")).size() == 1);
            }
         }
        assertTrue(foundAdmin);
    }
    
    @Test
    public void testAdminUserFilterNonAdmin() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        setupAuth(Right.ADMIN_ACCESS, null);
        Response resp = resource.getApplications("false");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertFalse(ents.contains(adminApp));
        assertTrue(ents.contains(userApp));
        assertFalse(ents.contains(installedApp));
    }
    
    @Test
    public void testAdminUserFilterAdmin() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        setupAuth(Right.ADMIN_ACCESS, Arrays.asList("LEA Administrator"));
        Response resp = resource.getApplications("true");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertTrue(ents.contains(adminApp));
        assertFalse(ents.contains(userApp));
        assertFalse(ents.contains(installedApp));
    }   
    
    @Test
    public void testNoneAllowed() {
        setupAuth(Right.READ_GENERAL, null);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertFalse(ents.contains(adminApp));
        assertFalse(ents.contains(userApp));
        assertFalse(ents.contains(installedApp));
    }
    
    private static void setupAuth(GrantedAuthority auth, List<String> roles) {
        Authentication mockAuth = Mockito.mock(Authentication.class);
        ArrayList<GrantedAuthority> rights = new ArrayList<GrantedAuthority>();
        rights.add(auth);
        Mockito.when(mockAuth.getAuthorities()).thenReturn(rights);
        SLIPrincipal principal = new SLIPrincipal();
        if (roles == null) {
            principal.setRoles(new ArrayList<String>());
        } else {
            principal.setRoles(roles);
        }
        Mockito.when(mockAuth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);

    }

}

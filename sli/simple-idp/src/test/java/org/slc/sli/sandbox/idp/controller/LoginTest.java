package org.slc.sli.sandbox.idp.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.AuthRequestService.Request;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.RoleService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests
 */
// @RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LoginTest {
    
    @Mock
    HttpSession httpSession;
    
    @Mock
    AuthRequestService authRequestService;
    
    @Mock
    SamlAssertionService loginService;
    
    @Mock
    UserService userService;
    
    @Mock
    RoleService roleService;
    
    @InjectMocks
    Login loginController = new Login();
    
    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        loginController.setSliAdminRealmName("SLIAdmin");
    }
    
    @Test
    public void testLoginSetup() {
        loginController.setSandboxImpersonationEnabled(false);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        
        ModelAndView mov = loginController.form("SAMLRequest", "realm", httpSession);
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("realm", mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
        assertEquals(false, mov.getModel().get("is_sandbox"));
    }
    
    @Test
    public void testLoginSetupWithExistingSession() {
        loginController.setSandboxImpersonationEnabled(false);
        List<String> roles = Arrays.asList("role1", "role2");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Tenant", "mytenant");
        User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.form("SAMLRequest", "realm", httpSession);
        assertEquals("redirect_uri", ((SamlAssertion) mov.getModel().get("samlAssertion")).getRedirectUri());
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testSandboxImpersonateLoginSetup() {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "")).thenReturn(reqInfo);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        
        ModelAndView mov = loginController.form("SAMLRequest", "", httpSession);
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals(null, mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
        assertEquals(true, mov.getModel().get("is_sandbox"));
        assertEquals(1, ((Collection) mov.getModel().get("roles")).size());
    }
    
    @Test
    public void testAdminLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(false);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", null, null,
                httpSession, null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testNormalLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(false);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("realm", "userId", "password")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "realm", null, null, httpSession,
                null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testSandboxImpersonationLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        Mockito.when(attributes.get("tenant")).thenReturn("myTenant");
        
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("impersonate", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "", "impersonate", roles,
                httpSession, null);
        Mockito.verify(attributes, Mockito.times(1)).clear();
        Mockito.verify(attributes, Mockito.times(1)).put("tenant", "myTenant");
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testSandboxAdminLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", null, null,
                httpSession, null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
        
    }
}

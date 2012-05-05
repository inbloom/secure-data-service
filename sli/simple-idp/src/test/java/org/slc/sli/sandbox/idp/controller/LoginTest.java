package org.slc.sli.sandbox.idp.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.AuthRequestService.Request;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
import org.slc.sli.sandbox.idp.service.RoleService;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginTest {
    
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
    
    @Test
    public void testLoginSetup() {
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        
        ModelAndView mov = loginController.form("SAMLRequest", "realm");
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("realm", mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
    }
    
    @Test
    public void testAdminLogin() throws AuthenticationException {
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
        
        Object response = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin");
        
        assertEquals("org.springframework.web.servlet.ModelAndView", response.getClass().getName());
        ModelAndView mov = (ModelAndView) response;
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
        
    }
    
    @Test
    public void testNormalLogin() throws AuthenticationException {
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
        
        Object response = loginController.login("userId", "password", "SAMLRequest", "realm");
        
        assertEquals("org.springframework.web.servlet.ModelAndView", response.getClass().getName());
        ModelAndView mov = (ModelAndView) response;
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testSandboxImpersonationLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("realm", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        
        Object response = loginController.login("userId", "password", "SAMLRequest", "realm");
        
        assertEquals("org.springframework.web.servlet.ModelAndView", response.getClass().getName());
        ModelAndView mov = (ModelAndView) response;
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("selectUser", mov.getViewName());
        assertEquals("realm", mov.getModel().get("realm"));
        assertEquals(defaultRoles, mov.getModel().get("roles"));
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
        
        Object response = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin");
        
        assertEquals("org.springframework.web.servlet.ModelAndView", response.getClass().getName());
        ModelAndView mov = (ModelAndView) response;
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
        
    }
}

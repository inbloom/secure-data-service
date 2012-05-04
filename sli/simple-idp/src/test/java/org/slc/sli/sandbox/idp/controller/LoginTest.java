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
import org.slc.sli.sandbox.idp.controller.Login;
import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.AuthenticationException;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.RoleService;
import org.slc.sli.sandbox.idp.service.UserService;
import org.slc.sli.sandbox.idp.service.AuthRequests.Request;
import org.slc.sli.sandbox.idp.service.LoginService.SamlResponse;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginTest {
    
    @Mock
    AuthRequests authRequestService;
    
    @Mock
    LoginService loginService;
    
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
        Mockito.when(authRequestService.processRequest("SAMLRequest","realm")).thenReturn(reqInfo);
        
        
        ModelAndView mov = loginController.form("SAMLRequest", "realm");
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("realm", mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
    }
    
    
    @Test
    public void testAdminLogin() throws AuthenticationException {
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("SLI");
        Mockito.when(authRequestService.processRequest("SAMLRequest","SLI")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLI", "userId", "password")).thenReturn(user);
        
        SamlResponse samlResponse = new SamlResponse("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.login("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        Object response = loginController.login("userId", "password", "SAMLRequest", "SLI");
        
        assertEquals("org.springframework.web.servlet.ModelAndView", response.getClass().getName());
        ModelAndView mov = (ModelAndView) response;
        assertEquals("SAMLResponse", ((SamlResponse)mov.getModel().get("samlResponse")).getSamlResponse());
        assertEquals("post", mov.getViewName());
        
    }
    
    @Test
    public void testNormalLogin() throws AuthenticationException {
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest","realm")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("realm", "userId", "password")).thenReturn(user);
        
        SamlResponse samlResponse = new SamlResponse("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.login("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        Object response = loginController.login("userId", "password", "SAMLRequest", "realm");
        
        assertEquals("org.springframework.web.servlet.ModelAndView", response.getClass().getName());
        ModelAndView mov = (ModelAndView) response;
        assertEquals("SAMLResponse", ((SamlResponse)mov.getModel().get("samlResponse")).getSamlResponse());
        assertEquals("post", mov.getViewName());  
    }
    
    @Test
    public void testImpersonationLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest","realm")).thenReturn(reqInfo);
        
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
}

/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.slc.sli.sandbox.idp.service.DefaultUsersService;
import org.slc.sli.sandbox.idp.service.DefaultUsersService.Dataset;
import org.slc.sli.sandbox.idp.service.DefaultUsersService.DefaultUser;
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
    
    @Mock
    DefaultUsersService defaultUserService;
    
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
        List<Dataset> datasets = Arrays.asList(new Dataset("test1", "Test 1 Dataset"), new Dataset("test2", "Test 2 Dataset"));
        Mockito.when(defaultUserService.getAvailableDatasets()).thenReturn(datasets);
        List<DefaultUser> test1Users = Arrays.asList(new DefaultUser("user1", "User One", "role1"), new DefaultUser("user2", "User Two", "role2"));
        Mockito.when(defaultUserService.getUsers("test1")).thenReturn(test1Users);
        List<DefaultUser> test2Users = Arrays.asList(new DefaultUser("user3", "User Three", "role1"), new DefaultUser("user4", "User Four", "role3"));
        Mockito.when(defaultUserService.getUsers("test2")).thenReturn(test2Users);

        
        ModelAndView mov = loginController.form("SAMLRequest", "", httpSession);
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals(null, mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
        assertEquals(true, mov.getModel().get("is_sandbox"));
        assertEquals(1, ((Collection) mov.getModel().get("roles")).size());
        assertEquals(2, ((Collection) mov.getModel().get("datasets")).size());
        assertEquals(2, ((Collection) mov.getModel().get("test1")).size());
        assertEquals(2, ((Collection) mov.getModel().get("test2")).size());
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
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", null, null, false,
                null, null, null, httpSession, null);
        
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
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("realm", "userId", "password")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "realm", null, null, false,
                null, null, null, httpSession, null);

        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testBadLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(false);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        
        Mockito.when(userService.authenticate("realm", "userId", "password")).thenThrow(
                new AuthenticationException("Invalid User Name or password"));
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "realm", null, null, false,
                null, null, null, httpSession, null);
        assertEquals("Invalid User Name or password", (String) mov.getModel().get("msg"));
    }
    
    @Test
    public void testSandboxImpersonationLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> userAttributes = Mockito.mock(HashMap.class);
        Mockito.when(userAttributes.get("userName")).thenReturn("Test Name");
        Mockito.when(userAttributes.get("emailToken")).thenReturn("mockToken");
        Mockito.when(userAttributes.get("tenant")).thenReturn("myTenant");

        @SuppressWarnings("unchecked")
        Map<String, String> impUserAttributes = Mockito.mock(HashMap.class);
        Mockito.when(impUserAttributes.get("tenant")).thenReturn("myTenant");

        
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, userAttributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.eq("impersonate"), Mockito.eq(roles), Mockito.anyMap(), Mockito.eq(reqInfo))).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "", "impersonate", roles, false,
                null, null, null, httpSession, null);
        
        
        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user_session_key", user);
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testSandboxImpersonationLoginWithNoTenant() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        Mockito.when(attributes.get("tenant")).thenReturn("");
        
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("impersonate", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "", "impersonate", roles, false,
                null, null, null, httpSession, null);
        
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("", mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
        assertEquals(true, mov.getModel().get("is_sandbox"));
    }
    
    @Test
    public void testImpersonationLoginWithoutRoles() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        Mockito.when(attributes.get("tenant")).thenReturn("Super Tenant");
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        
        List<String> roles = new ArrayList<String>();
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("impersonate", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "", "impersonate", roles, false,
                null, null, null, httpSession, null);
        
        assertEquals(mov.getModel().get("msg"), Login.ROLE_SELECT_MESSAGE);
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("", mov.getModel().get("realm"));
        assertEquals("login", mov.getViewName());
        assertEquals(true, mov.getModel().get("is_sandbox"));
    }
    
    @Test
    public void testSandboxImpersonationDatasetUserLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "")).thenReturn(reqInfo);
        
        @SuppressWarnings("unchecked")
        Map<String, String> userAttributes = Mockito.mock(HashMap.class);
        Mockito.when(userAttributes.get("userName")).thenReturn("Test Name");
        Mockito.when(userAttributes.get("emailToken")).thenReturn("mockToken");
        Mockito.when(userAttributes.get("tenant")).thenReturn("myTenant");

        @SuppressWarnings("unchecked")
        Map<String, String> impUserAttributes = Mockito.mock(HashMap.class);
        Mockito.when(impUserAttributes.get("tenant")).thenReturn("myTenant");

        
        List<String> roles = Arrays.asList("role1");
        UserService.User user = new User("userId", roles, userAttributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.eq("dataset"), Mockito.eq(roles), Mockito.anyMap(), Mockito.eq(reqInfo))).thenReturn(samlResponse);
        
        DefaultUser defaultUser = new DefaultUser("dataset", "Dataset User", "role1");
        Mockito.when(defaultUserService.getUser("dataset", "datasetUserId")).thenReturn(defaultUser);
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "", "impersonate", roles, false,
                null, "dataset", "datasetUserId", httpSession, null);
        
        
        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user_session_key", user);
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
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        UserService.User user = new User("userId", roles, attributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", null, null, false,
                null, null, null, httpSession, null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
        
    }
    @Test
    public void testLogoutNoSaml() {
        ModelAndView mav = loginController.logout(null, "myrealm", httpSession);
        
        assertEquals("loggedOut", mav.getViewName());
        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user_session_key", null);
    }
    
    @Test
    public void testLogoutWithSaml() {
        ModelAndView mav = loginController.logout("encodedSamlRequest", "myrealm", httpSession);
        
        assertEquals("You are now logged out", mav.getModel().get("message"));
        assertEquals("login", mav.getViewName());
        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user_session_key", null);
        
    }
}

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

import junit.framework.Assert;

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
import org.springframework.ui.Model;
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
        assertEquals("Mock IDP for realm", mov.getModel().get("subTitle"));
        assertEquals("login", mov.getViewName());
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("realm", mov.getModel().get("realm"));
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
    
    @Test
    public void testAdminLogin() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(false);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(true);
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
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", httpSession, null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
 
    @Test
    public void testAdminLoginForceAuthFalse() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(false);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(false);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);

        @SuppressWarnings("unchecked")
        Map<String, String> attributes = Mockito.mock(HashMap.class);
        Mockito.when(attributes.get("userName")).thenReturn("Test Name");
        Mockito.when(attributes.get("emailToken")).thenReturn("mockToken");
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, attributes);        
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        ModelAndView mov = loginController.form("SAMLRequest", "SLIAdmin", httpSession);

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
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "realm", httpSession, null);

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
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "realm", httpSession, null);
        assertEquals("Invalid User Name or password", (String) mov.getModel().get("errorMsg"));
    }
    
    @Test
    public void testSandboxLoginSetup() {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(true);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        ModelAndView mov = loginController.form("SAMLRequest", "SLIAdmin", httpSession);
        assertEquals("Developer Sandbox", mov.getModel().get("subTitle"));
        assertEquals("login", mov.getViewName());
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("SLIAdmin", mov.getModel().get("realm"));
    }
    
    @Test
    public void testSandboxLoginSetupWithExistingSessionForceAuth() {
        loginController.setSandboxImpersonationEnabled(true);
        List<String> roles = Arrays.asList("role1", "role2");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Tenant", "mytenant");
        User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(true);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.form("SAMLRequest", "SLIAdmin", httpSession);
        assertEquals("impersonate", mov.getViewName());
        assertEquals("SAMLRequest", mov.getModel().get("SAMLRequest"));
        assertEquals("SLIAdmin", mov.getModel().get("realm"));
    }
    
    @Test
    public void testSandboxLoginSetupWithExistingSessionNoForceAuthN() {
        loginController.setSandboxImpersonationEnabled(true);
        List<String> roles = Arrays.asList("role1", "role2");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Tenant", "mytenant");
        User user = new User("userId", roles, attributes);
        user.setImpersonationUser(new User("linda.kim", roles, null));
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(false);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.matches("linda.kim"), Mockito.same(roles), Mockito.anyMap(), Mockito.same(reqInfo))).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.form("SAMLRequest", "SLIAdmin", httpSession);
        assertEquals("post", mov.getViewName());
        SamlAssertion saml = (SamlAssertion) mov.getModel().get("samlAssertion");
        assertEquals("SAMLResponse", saml.getSamlResponse());
    }
    
    @Test
    public void testSandboxAdminLoginSetupWithExistingSessionForceAuthN() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        List<String> roles = Arrays.asList("role1", "role2");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Tenant", "mytenant");
        User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(true);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        ModelAndView mov = loginController.form("SAMLRequest", "SLIAdmin", httpSession);
        assertEquals("impersonate", mov.getViewName());
    }

    @Test
    public void testSandboxAdminLoginSetupWithExistingSessionNoForceAuthN() throws AuthenticationException {
        loginController.setSandboxImpersonationEnabled(true);
        List<String> roles = Arrays.asList("role1", "role2");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Tenant", "mytenant");
        User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("SLIAdmin");
        Mockito.when(reqInfo.isForceAuthn()).thenReturn(false);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.matches("userId"), Mockito.same(roles), Mockito.anyMap(), Mockito.same(reqInfo))).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.form("SAMLRequest", "SLIAdmin", httpSession);
        assertEquals("post", mov.getViewName());
        SamlAssertion saml = (SamlAssertion) mov.getModel().get("samlAssertion");
        assertEquals("SAMLResponse", saml.getSamlResponse());
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
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.matches("userId"), Mockito.same(roles), Mockito.anyMap(), Mockito.same(reqInfo))).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", httpSession, null);
        assertEquals("impersonate", mov.getViewName());
        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user_session_key", user);
        
        mov = loginController.admin("SAMLRequest", "SLIAdmin", httpSession);
        
        SamlAssertion saml = (SamlAssertion) mov.getModel().get("samlAssertion");
        assertEquals("SAMLResponse", saml.getSamlResponse());
        assertEquals("post", mov.getViewName());
    }

    @Test
    public void testLogoutNoSaml() {
        ModelAndView mav = loginController.logout(null, null, httpSession);
        Mockito.verify(httpSession, Mockito.times(1)).removeAttribute("user_session_key");
        assertEquals("loggedOut", mav.getViewName());
    }
    
    @Test
    public void testLogoutWithSaml() {
        ModelAndView mav = loginController.logout("SAMLRequest", "realm", httpSession);
        
        assertEquals("You are now logged out", mav.getModel().get("msg"));
        assertEquals("login", mav.getViewName());
        Mockito.verify(httpSession, Mockito.times(1)).removeAttribute("user_session_key");
    }
    
    @Test
    public void testSandboxImpersonationLogin() throws Exception {
        loginController.setSandboxImpersonationEnabled(true);
        
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        HashMap<String, String> userAttributes = new HashMap<String, String>();
        userAttributes.put("userName", "Test Name");
        userAttributes.put("emailToken","mockToken");
        userAttributes.put("tenant", "myTenant");

        @SuppressWarnings("unchecked")
        Map<String, String> impUserAttributes = Mockito.mock(HashMap.class);
        Mockito.when(impUserAttributes.get("tenant")).thenReturn("myTenant");

        List<Dataset> datasets = Arrays.asList(new Dataset("test1", "Test 1 Dataset"), new Dataset("test2", "Test 2 Dataset"));
        Mockito.when(defaultUserService.getAvailableDatasets()).thenReturn(datasets);
        List<DefaultUser> test1Users = Arrays.asList(new DefaultUser("user1", "User One", "role1", "school1"), new DefaultUser("user2", "User Two", "role2", "SEA"));
        Mockito.when(defaultUserService.getUsers("test1")).thenReturn(test1Users);
        List<DefaultUser> test2Users = Arrays.asList(new DefaultUser("user3", "User Three", "role1", "school2"), new DefaultUser("user4", "User Four", "role3", "LEA"));
        Mockito.when(defaultUserService.getUsers("test2")).thenReturn(test2Users);
        
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, userAttributes);
        
        Mockito.when(userService.authenticate("SLIAdmin", "userId", "password")).thenReturn(user);
        List<RoleService.Role> defaultRoles = new ArrayList<RoleService.Role>();
        defaultRoles.add(new RoleService.Role("roleName"));
        Mockito.when(roleService.getAvailableRoles()).thenReturn(defaultRoles);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.eq("impersonate"), Mockito.eq(roles), Mockito.anyMap(), Mockito.eq(reqInfo))).thenReturn(samlResponse);
        
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        ModelAndView mov = loginController.login("userId", "password", "SAMLRequest", "SLIAdmin", httpSession, null);
        assertEquals("impersonate", mov.getViewName());
        assertEquals(1, ((Collection) mov.getModel().get("roles")).size());
        assertEquals(2, ((Collection) mov.getModel().get("datasets")).size());
        assertEquals(2, ((Collection) mov.getModel().get("test1")).size());
        assertEquals(2, ((Collection) mov.getModel().get("test2")).size());
        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user_session_key", user);
        
        mov = loginController.impersonate("SAMLRequest", "SLIAdmin", "impersonate", roles, null, null, null, true, httpSession, null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }
    
    @Test
    public void testSandboxImpersonationLoginWithNoTenant() throws Exception {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", "Test Name");
        attributes.put("emailToken","mockToken");
        attributes.put("tenant", "");
        List<String> roles = Arrays.asList("role1", "role2");
        UserService.User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("impersonate", roles, attributes, reqInfo)).thenReturn(samlResponse);
        

        ModelAndView mov = loginController.impersonate("SAMLRequest", "SLIAdmin", "impersonate", roles, null, null, null, true, httpSession, null);
        
        assertEquals("impersonate", mov.getViewName());
        assertEquals("User account not properly configured for impersonation.", mov.getModel().get("errorMsg"));
    }
    
    @Test
    public void testImpersonationLoginWithoutRoles() throws Exception {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", "Test Name");
        attributes.put("emailToken","mockToken");
        attributes.put("tenant", "Super Tenant");
        List<String> roles = new ArrayList<String>();
        UserService.User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("impersonate", roles, attributes, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = loginController.impersonate("SAMLRequest", "SLIAdmin", "impersonate", roles, null, null, null, true, httpSession, null);
        
        assertEquals("Please select or enter one role to impersonate.", mov.getModel().get("errorMsg"));
        assertEquals("impersonate", mov.getViewName());
    }
    
    @Test
    public void testSandboxImpersonationDatasetUserLogin() throws Exception {
        loginController.setSandboxImpersonationEnabled(true);
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRealm()).thenReturn(null);
        Mockito.when(authRequestService.processRequest("SAMLRequest", "SLIAdmin")).thenReturn(reqInfo);
        
        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", "Test Name");
        attributes.put("emailToken","mockToken");
        attributes.put("tenant", "myTenant");
        
        List<String> roles = Arrays.asList("role1");
        UserService.User user = new User("userId", roles, attributes);
        Mockito.when(httpSession.getAttribute("user_session_key")).thenReturn(user);
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion(Mockito.eq("dataset"), Mockito.eq(roles), Mockito.anyMap(), Mockito.eq(reqInfo))).thenReturn(samlResponse);
        
        DefaultUser defaultUser = new DefaultUser("dataset", "Dataset User", "role1", "school");
        Mockito.when(defaultUserService.getUser("dataset", "datasetUserId")).thenReturn(defaultUser);
        
        ModelAndView mov = loginController.impersonate("SAMLRequest", "SLIAdmin", "impersonate", roles, null, "dataset", "datasetUserId", false, httpSession, null);
        
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("post", mov.getViewName());
    }

    @Test(expected=IllegalStateException.class)
    public void testImpersonationInProdMode() throws IllegalStateException {
        loginController.setSandboxImpersonationEnabled(false);
        
        loginController.impersonate(null, null, null, null, null, null, null, false, httpSession, null);

    }
}

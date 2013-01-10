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


package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.UserService.GroupContextMapper;
import org.slc.sli.sandbox.idp.service.UserService.PersonContextMapper;
import org.slc.sli.sandbox.idp.service.UserService.User;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class UsersTest {
    @Mock
    LdapTemplate ldapTemplate;
    
    @InjectMocks
    UserService userService = new UserService("uid", "person", "memberuid", "posixGroup");
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testAuthenticate() throws AuthenticationException {
        DistinguishedName dn = new DistinguishedName("ou=SLIAdmin");
        Mockito.when(
                ldapTemplate.authenticate(Mockito.eq(dn), Mockito.eq("(&(objectclass=person)(uid=testuser))"),
                        Mockito.eq("testuser1234"), Mockito.any(AuthenticationErrorCallback.class))).thenReturn(true);
        User mockUser = new User();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", "Test User");
        mockUser.attributes = attributes;
        mockUser.userId = "testuser";
        Mockito.when(
                ldapTemplate.searchForObject(Mockito.eq(dn), Mockito.eq("(&(objectclass=person)(uid=testuser))"),
                        Mockito.any(ContextMapper.class))).thenReturn(mockUser);
        List<String> mockGroups = new ArrayList<String>();
        mockGroups.add("TestGroup1");
        mockGroups.add("TestGroup2");
        Mockito.when(
                ldapTemplate.search(Mockito.eq(dn), Mockito.eq("(&(objectclass=posixGroup)(memberuid=testuser))"),
                        Mockito.any(GroupContextMapper.class))).thenReturn(mockGroups);
        
        UserService.User user = userService.authenticate("SLIAdmin", "testuser", "testuser1234");
        assertEquals("testuser", user.getUserId());
        assertEquals("Test User", user.getAttributes().get("userName"));
        assertEquals(2, user.getRoles().size());
        assertEquals("TestGroup1", user.getRoles().get(0));
        assertEquals("TestGroup2", user.getRoles().get(1));
    }
    
    @Test
    public void testSandboxAuthenticate() throws AuthenticationException {
        DistinguishedName dn = new DistinguishedName("ou=SLIAdmin");
        Mockito.when(
                ldapTemplate.authenticate(Mockito.eq(dn), Mockito.eq("(&(objectclass=person)(uid=testuser))"),
                        Mockito.eq("testuser1234"), Mockito.any(AuthenticationErrorCallback.class))).thenReturn(true);
        User mockUser = new User();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", "Test User");
        attributes.put("Tenant", "mytenant");
        mockUser.attributes = attributes;
        mockUser.userId = "testuser";
        Mockito.when(
                ldapTemplate.searchForObject(Mockito.eq(dn), Mockito.eq("(&(objectclass=person)(uid=testuser))"),
                        Mockito.any(ContextMapper.class))).thenReturn(mockUser);
        List<String> mockGroups = new ArrayList<String>();
        mockGroups.add("TestGroup1");
        mockGroups.add("TestGroup2");
        Mockito.when(
                ldapTemplate.search(Mockito.eq(dn), Mockito.eq("(&(objectclass=posixGroup)(memberuid=testuser))"),
                        Mockito.any(GroupContextMapper.class))).thenReturn(mockGroups);
        
        UserService.User user = userService.authenticate("SLIAdmin", "testuser", "testuser1234");
        assertEquals("testuser", user.getUserId());
        assertEquals("Test User", user.getAttributes().get("userName"));
        assertEquals("mytenant", user.getAttributes().get("Tenant"));
        assertEquals(2, user.getRoles().size());
        assertEquals("TestGroup1", user.getRoles().get(0));
        assertEquals("TestGroup2", user.getRoles().get(1));
    }
    
    @Test
    public void testAttributeExtractionCommas() {
        String desc = "tenant=myTenantId,edOrg=myEdorgId";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("cn")).thenReturn("Full Name");
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        
        assertEquals("Full Name", user.getAttributes().get("userName"));
        assertEquals("myTenantId", user.getAttributes().get("tenant"));
        assertEquals("myEdorgId", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(6, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionNewlines() {
        String desc = "tenant=myTenantId\nedOrg=myEdorgId\n";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals("myTenantId", user.getAttributes().get("tenant"));
        assertEquals("myEdorgId", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(6, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionSpaces() {
        String desc = "tenant=myTenantId edOrg=myEdorgId";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals("myTenantId", user.getAttributes().get("tenant"));
        assertEquals("myEdorgId", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(6, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionCommasWithSpaces() {
        String desc = "tenant=myTenantId,edOrg=My Edorg Id";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals("myTenantId", user.getAttributes().get("tenant"));
        assertEquals("My Edorg Id", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(6, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionNewLinesWithSpaces() {
        String desc = "tenant=myTenantId\nedOrg=My Edorg Id\n";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals("myTenantId", user.getAttributes().get("tenant"));
        assertEquals("My Edorg Id", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(6, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionNewLinesWithBlanks() {
        String desc = "tenant=\nedOrg=My Edorg Id\n";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals(null, user.getAttributes().get("tenant"));
        assertEquals("My Edorg Id", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(5, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionNewLinesWithBlanks2() {
        String desc = "tenant=\nedOrg=\n";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals(null, user.getAttributes().get("tenant"));
        assertEquals(null, user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(4, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionCommasWithBlanks() {
        String desc = "tenant=,edOrg=My Edorg Id,";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals(null, user.getAttributes().get("tenant"));
        assertEquals("My Edorg Id", user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(5, user.getAttributes().size());
    }
    
    @Test
    public void testAttributeExtractionCommasWithBlanks2() {
        String desc = "tenant=,edOrg=,";
        PersonContextMapper mapper = new PersonContextMapper();
        DirContextAdapter context = Mockito.mock(DirContextAdapter.class);
        Mockito.when(context.getStringAttribute("description")).thenReturn(desc);
        User user = (User) mapper.mapFromContext(context);
        assertEquals(null, user.getAttributes().get("tenant"));
        assertEquals(null, user.getAttributes().get("edOrg"));
        assertEquals(null, user.getAttributes().get("vendor"));
        assertEquals(null, user.getAttributes().get("givenName"));
        assertEquals(null, user.getAttributes().get("sn"));
        assertEquals(4, user.getAttributes().size());
    }
}

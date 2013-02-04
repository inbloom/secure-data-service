/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
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
    
    @Mock Repository<Entity> repo;
    
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
        ArrayList<String> rights = new ArrayList<String>();
        rights.add(Right.ADMIN_ACCESS.toString());
        rights.add(Right.CRUD_LEA_ADMIN.toString());
        endpoint.put("rights", rights);
        endpoints.add(endpoint);
        adminApp.put("endpoints", endpoints);
        adminApp.put("created_by", "slcdeveloper");
        adminApp.put("name", "Admin App");
        adminApp.put("admin_visible", true);
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
        List<Entity> appList = new ArrayList<Entity>();
        appList.add(new MongoEntity("application", adminApp));
        appList.add(new MongoEntity("application", userApp));
        appList.add(new MongoEntity("application", installedApp));
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(appList);
    }
    
    @Test (expected = InsufficientAuthenticationException.class)
    public void testNotLoggedIn() {
        SecurityContextHolder.clearContext();
        resource.getApplications("");
    }
        
    private boolean isInEntityList(EntityBody app, List<EntityBody> ents) {
        for (EntityBody ent : ents) {
            if (app.get("name").equals(ent.get("name"))) {
                return true;
            }
        }
        return false;
    }

 /*   @Test
    public void testEndpointFilteringNoRoles() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        setupAuth(Right.AGGREGATE_READ, null);
        Response resp = resource.getApplications("");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertFalse("Admin should be filtered out since no endpoints are applicable", isInEntityList(adminApp, ents));
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
            
            if (body.get("name").equals(adminApp.get("name"))) {
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
            if (body.get("name").equals(adminApp.get("name"))) {
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
        assertFalse(ents.contains(userApp));
        assertFalse(ents.contains(installedApp));
    }
    
    @Test
    public void testAdminUserFilterAdmin() {
        Mockito.when(appValidator.getAuthorizedApps(Mockito.any(SLIPrincipal.class))).thenReturn(
                Arrays.asList("adminAppId", "userAppId", "disabledAppId"));
        setupAuth(Right.ADMIN_ACCESS, Arrays.asList("LEA Administrator"));
        Response resp = resource.getApplications("true");
        List<EntityBody> ents = (List<EntityBody>) resp.getEntity();
        assertTrue(isInEntityList(adminApp, ents));
        assertFalse(isInEntityList(userApp, ents));
        assertFalse(isInEntityList(installedApp, ents));
    }   */
    
    
    private void setupAuth(GrantedAuthority auth, List<String> roles) {
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
        principal.setRealm("someRealm");
        Map realmBody = new HashMap();

        if (auth == Right.ADMIN_ACCESS) {
            realmBody.put("admin", true);
        } else {
            realmBody.put("admin", false);
        }
        MongoEntity realm = new MongoEntity("realm", realmBody);
        Mockito.when(repo.findById("realm", "someRealm")).thenReturn(realm);
        Mockito.when(mockAuth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        
        
    }

}

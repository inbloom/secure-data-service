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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests for CustomRoleResource
 * @author jnanney
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CustomRoleResourceTest {
    
    public static final String REALM_ID = "867-5309";
    @Autowired
    @InjectMocks private CustomRoleResource resource;
    
    @Mock RealmHelper realmHelper;
    
    @Mock EntityService service;

    @Mock Repository<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;

    UriInfo uriInfo;
    
    @Before
    public void setUp() throws Exception {
        injector.setRealmAdminContext();
        MockitoAnnotations.initMocks(this);
        uriInfo = ResourceTestUtil.buildMockUriInfo("");
        
    }
    
    private void setRealms(String ... realmIds) {
        HashSet<String> ids = new HashSet<String>();
        for (String id : realmIds) {
            ids.add(id);
        }
        Mockito.when(realmHelper.getAssociatedRealmIds()).thenReturn(ids);
    }

    @Test
    public void testValidCreate() throws URISyntaxException {
        setRealms(REALM_ID);
        EntityBody body = getValidRoleDoc();
        
        mockGetRealmId();
        Mockito.when(service.create(body)).thenReturn("new-role-id");
        
        
        Response res = resource.createCustomRole(body, getMockUriInfo());
        Assert.assertEquals(201, res.getStatus());
    }

    @Test
    public void testValidUpdate() {
        setRealms(REALM_ID);
        EntityBody body = getValidRoleDoc();
        mockGetRealmId();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        
        body.put("roles", new ArrayList<Map<String, List<String>>>());
        Mockito.when(service.update(id, body)).thenReturn(true);
        Response res = resource.updateCustomRole(id, body, uriInfo);
        Assert.assertEquals(204, res.getStatus());
    }
    
    @Test
    public void testReadAll() {
        setRealms(REALM_ID);
        EntityBody body = getValidRoleDoc();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        mockGetRealmId();
        
        Entity mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getEntityId()).thenReturn("mock-id");
        Mockito.when(service.get("mock-id")).thenReturn(body);
        
        NeutralQuery customRoleQuery = new NeutralQuery();
        customRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.CRITERIA_IN, new HashSet<String>(Arrays.asList(REALM_ID))));
        Mockito.when(repo.findAllIds("customRole", customRoleQuery)).thenReturn(Arrays.asList("mock-id"));
        Response res = resource.readAll(uriInfo, "");
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(Arrays.asList(body), res.getEntity());
    }
    
    @Test
    public void testReadAllWithBadRealmIdParam() {
        setRealms(REALM_ID);
        EntityBody body = getValidRoleDoc();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        mockGetRealmId();
        
        Entity mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getEntityId()).thenReturn("mock-id");
        Mockito.when(service.get("mock-id")).thenReturn(body);
        
        NeutralQuery customRoleQuery = new NeutralQuery();
        customRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.OPERATOR_EQUAL, REALM_ID));
        Mockito.when(repo.findAllIds("customRole", customRoleQuery)).thenReturn(Arrays.asList("mock-id"));

        Response res = resource.readAll(uriInfo, "BAD_REALM_ID");
        Assert.assertEquals(400, res.getStatus());
    }
    
    @Test
    public void testReadAllWithMultipleRealmsAndNoRealmIdParam() {
        setRealms(REALM_ID, "REALM2");
        EntityBody body = getValidRoleDoc();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        mockGetRealmId();
        
        Entity mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getEntityId()).thenReturn("mock-id");
        Mockito.when(service.get("mock-id")).thenReturn(body);
        
        NeutralQuery customRoleQuery = new NeutralQuery();
        customRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.CRITERIA_IN, new HashSet<String>(Arrays.asList(REALM_ID, "REALM2"))));
        Mockito.when(repo.findAllIds("customRole", customRoleQuery)).thenReturn(Arrays.asList("mock-id"));

        Response res = resource.readAll(uriInfo, "");
        Assert.assertEquals(200, res.getStatus());
    }
    
    @Test
    public void testReadAllWithMultipleRealmsAndValidRealmIdParam() {
        setRealms(REALM_ID, "REALM2");
        EntityBody body = getValidRoleDoc();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        mockGetRealmId();
        
        Entity mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getEntityId()).thenReturn("mock-id");
        Mockito.when(service.get("mock-id")).thenReturn(body);
        
        NeutralQuery customRoleQuery = new NeutralQuery();
        customRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.CRITERIA_IN, new HashSet<String>(Arrays.asList(REALM_ID))));
        Mockito.when(repo.findAllIds("customRole", customRoleQuery)).thenReturn(Arrays.asList("mock-id"));

        Response res = resource.readAll(uriInfo, REALM_ID);
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(Arrays.asList(body), res.getEntity());
    }
    
    @Test
    public void testReadAccessible() {
        setRealms(REALM_ID);
        EntityBody body = getValidRoleDoc();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        mockGetRealmId();
        Response res = resource.read(id, uriInfo);
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(body, res.getEntity());

    }
    
    @Test
    public void testReadInaccessible() {
        String inaccessibleId = "inaccessible-id";
        EntityBody body = getValidRoleDoc();
        body.put("realmId", "BAD-REALM");
        Mockito.when(service.get(inaccessibleId)).thenReturn(body);
        Response res = resource.read(inaccessibleId, uriInfo);
        Assert.assertEquals(403, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_FORBIDDEN, res.getEntity());
    }
    
    @Test
    public void testUpdateWithDuplicateRoles() {
        EntityBody body = getRoleDocWithDuplicateRole();
        body.put("customRights", Arrays.asList(new String[] {"Something", "Else"}));
        mockGetRealmId();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        
        Mockito.when(service.update(id, body)).thenReturn(true);
        Response res = resource.updateCustomRole(id, body, uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_DUPLICATE_ROLE, res.getEntity());
    }
    
    @Test
    public void testUpdateWithInvalidRight() {
        EntityBody body = getRoleDocWithInvalidRight();
        body.put("customRights", Arrays.asList(new String[] {"Something", "Else"}));
        mockGetRealmId();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        
        Mockito.when(service.update(id, body)).thenReturn(true);
        Response res = resource.updateCustomRole(id, body, uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_INVALID_RIGHT, res.getEntity());

    }
    
    @Test
    public void testUpdateWithInvalidRealmId() {
        EntityBody body = getRoleDocWithInvalidRealm();
        body.put("customRights", Arrays.asList(new String[] {"Something", "Else"}));
        mockGetRealmId();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        
        Mockito.when(service.update(id, body)).thenReturn(true);
        Response res = resource.updateCustomRole(id, body, uriInfo);
        Assert.assertEquals(403, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_INVALID_REALM, res.getEntity());

    }
    
    @Test
    public void testUpdateWithDuplicateRights() {
        EntityBody body = getRoleDocWithDuplicateRights();
        
        body.put("customRights", Arrays.asList(new String[] {"Something", "Else"}));
        mockGetRealmId();
        String id = "old-id";
        
        Mockito.when(service.get(id)).thenReturn((EntityBody) body.clone());
        
        Mockito.when(service.update(id, body)).thenReturn(true);
        Response res = resource.updateCustomRole(id, body, uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_DUPLICATE_RIGHTS, res.getEntity());
    }
    
    
    @Test
    public void testCreateWithDuplicateRoles() {
        EntityBody body = getRoleDocWithDuplicateRole();
        
        mockGetRealmId();
        Mockito.when(service.create(body)).thenReturn("new-role-id");
        
        
        Response res = resource.createCustomRole(body, uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_DUPLICATE_ROLE, res.getEntity());
    }
    
    @Test
    public void testCreateWithInvalidRight() {
        EntityBody body = getRoleDocWithInvalidRight();
        
        mockGetRealmId();
        Mockito.when(service.create(body)).thenReturn("new-role-id");
        
        
        Response res = resource.createCustomRole(body, uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_INVALID_RIGHT, res.getEntity());
    }
    
    @Test
    public void testCreateWithInvalidRealmId() {
        EntityBody body = getRoleDocWithInvalidRealm();
        
        mockGetRealmId();
        Mockito.when(service.create(body)).thenReturn("new-role-id");
        
        
        Response res = resource.createCustomRole(body, uriInfo);
        Assert.assertEquals(403, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_INVALID_REALM, res.getEntity());
    }
    
    @Test
    public void testCreateWithDuplicateRights() {
        EntityBody body = getRoleDocWithDuplicateRights();
        
        mockGetRealmId();
        Mockito.when(service.create(body)).thenReturn("new-role-id");
        
        
        Response res = resource.createCustomRole(body, uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_DUPLICATE_RIGHTS, res.getEntity());
    }
    
    @Test
    public void testCreateDuplicate() {
        setRealms(REALM_ID);
        NeutralQuery existingCustomRoleQuery = new NeutralQuery();
        existingCustomRoleQuery.addCriteria(new NeutralCriteria("realmId", NeutralCriteria.OPERATOR_EQUAL, REALM_ID));
        
        Entity mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getEntityId()).thenReturn("fake-id");
        Mockito.when(repo.findOne(CustomRoleResource.RESOURCE_NAME, existingCustomRoleQuery)).thenReturn(mockEntity);
        mockGetRealmId();

        Response res = resource.createCustomRole(getValidRoleDoc(), uriInfo);
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(CustomRoleResource.ERROR_MULTIPLE_DOCS, res.getEntity());

    }

    private EntityBody getValidRoleDoc() {
        EntityBody body = new EntityBody();
        body.put("realmId", REALM_ID);
        body.put("customRights", new ArrayList<String>());
        body.put("tenantId", SecurityContextInjector.TENANT_ID);

        List<Map<String, List<String>>> roles = new ArrayList<Map<String, List<String>>>();
        Map<String, List<String>> role1 = new HashMap<String, List<String>>();
        role1.put("names", Arrays.asList(new String[]{ "Role1", "Role2"}));
        role1.put("rights", Arrays.asList(new String[]{"READ_GENERAL", "WRITE_GENERAL"}));
        Map<String, List<String>> role2 = new HashMap<String, List<String>>();
        role2.put("names", Arrays.asList(new String[]{ "Role3", "Role4"}));
        role2.put("rights", Arrays.asList(new String[]{"READ_GENERAL", "READ_RESTRICTED"}));
        roles.add(role1);
        roles.add(role2);
        body.put("roles", roles);
        
        return body;
    }
    
    private EntityBody getRoleDocWithDuplicateRole() {
        EntityBody body = getValidRoleDoc();
        List<Map<String, List<String>>> roles = new ArrayList<Map<String, List<String>>>();
        Map<String, List<String>> role1 = new HashMap<String, List<String>>();
        role1.put("names", Arrays.asList(new String[]{ "Role1", "Role2"}));
        role1.put("rights", Arrays.asList(new String[]{"READ_GENERAL", "WRITE_GENERAL"}));
        Map<String, List<String>> role2 = new HashMap<String, List<String>>();
        role2.put("names", Arrays.asList(new String[]{ "Role3", "Role1"}));
        role2.put("rights", Arrays.asList(new String[]{"READ_GENERAL", "READ_RESTRICTED"}));
        roles.add(role1);
        roles.add(role2);
        body.put("roles", roles);
        return body;
    }
    
    private EntityBody getRoleDocWithInvalidRight() {
        EntityBody body = getValidRoleDoc();
        List<Map<String, List<String>>> roles = new ArrayList<Map<String, List<String>>>();
        Map<String, List<String>> role1 = new HashMap<String, List<String>>();
        role1.put("names", Arrays.asList(new String[]{ "Role1", "Role2"}));
        role1.put("rights", Arrays.asList(new String[]{"RIGHT_TO_REMAIN_SILENT", "WRITE_GENERAL"}));
        Map<String, List<String>> role2 = new HashMap<String, List<String>>();
        role2.put("names", Arrays.asList(new String[]{ "Role3", "Role1"}));
        role2.put("rights", Arrays.asList(new String[]{"READ_GENERAL", "READ_RESTRICTED"}));
        roles.add(role1);
        roles.add(role2);
        body.put("roles", roles);
        return body;
    }
    
    private EntityBody getRoleDocWithDuplicateRights() {
        EntityBody body = getValidRoleDoc();
        List<Map<String, List<String>>> roles = new ArrayList<Map<String, List<String>>>();
        Map<String, List<String>> role1 = new HashMap<String, List<String>>();
        role1.put("names", Arrays.asList(new String[]{ "Role1", "Role2"}));
        role1.put("rights", Arrays.asList(new String[]{"WRITE_GENERAL", "WRITE_GENERAL"}));
        Map<String, List<String>> role2 = new HashMap<String, List<String>>();
        role2.put("names", Arrays.asList(new String[]{ "Role3", "Role1"}));
        role2.put("rights", Arrays.asList(new String[]{"READ_GENERAL", "READ_RESTRICTED"}));
        roles.add(role1);
        roles.add(role2);
        body.put("roles", roles);
        return body;

    }
    
    private EntityBody getRoleDocWithInvalidRealm() {
        EntityBody body = getValidRoleDoc();
        body.put("realmId", "Four-Leaf-Clover");
        return body;
    }
    
    private void mockGetRealmId() {
        NeutralQuery realmQuery = new NeutralQuery();
        realmQuery.addCriteria(new NeutralCriteria("edOrg", NeutralCriteria.OPERATOR_EQUAL, "fake-ed-org"));
        Entity realmEntity = Mockito.mock(Entity.class);
        Mockito.when(realmEntity.getEntityId()).thenReturn(REALM_ID);
        Mockito.when(repo.findOne("realm", realmQuery)).thenReturn(realmEntity);

    }
    
    private UriInfo getMockUriInfo() throws URISyntaxException {
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://foo.com"));
        Mockito.when(uriInfo.getPath()).thenReturn("/dont/start");
        return uriInfo;
    }
}

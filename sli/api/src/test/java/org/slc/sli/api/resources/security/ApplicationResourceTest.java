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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slc.sli.api.resources.security.ApplicationResource.APPROVAL_DATE;
import static org.slc.sli.api.resources.security.ApplicationResource.CLIENT_ID;
import static org.slc.sli.api.resources.security.ApplicationResource.CLIENT_SECRET;
import static org.slc.sli.api.resources.security.ApplicationResource.REGISTRATION;
import static org.slc.sli.api.resources.security.ApplicationResource.REQUEST_DATE;
import static org.slc.sli.api.resources.security.ApplicationResource.STATUS;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.UnversionedResource;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.context.validator.ValidatorTestHelper;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author pwolf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApplicationResourceTest {

    @Autowired
    private ApplicationResource resource;

    @Autowired
    @Qualifier("unversionedResource")
    private UnversionedResource unversionedResource;

    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private MockRepo repo;

    UriInfo uriInfo = null;
    HttpHeaders headers = null;

    private static final int STATUS_CREATED = 201;
    private static final int STATUS_DELETED = 204;
    private static final int STATUS_NO_CONTENT = 204;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_FOUND = 200;
    private static final int STATUS_BAD_REQUEST = 400;


    @Before
    public void setUp() throws Exception {

        uriInfo = ResourceTestUtil.buildMockUriInfo("");
        injector.setDeveloperContext();
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        resource.setAutoRegister(false);
        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
        when(uriInfo.getQueryParameters()).thenReturn(new MultivaluedMapImpl());
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();

        repo.deleteAll("application", null);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGoodCreate() throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));

        EntityBody app = getNewApp();

        Response resp = resource.post(app, uriInfo);
        assertEquals(STATUS_CREATED, resp.getStatus());
        assertTrue("Client id set", app.get(CLIENT_ID).toString().length() == 10);
        assertTrue("Client secret set", app.get(CLIENT_SECRET).toString().length() == 48);
        Map reg = (Map) app.get(REGISTRATION);
        assertEquals("Reg is pending", "PENDING", reg.get(STATUS));
        assertTrue("request date set", reg.containsKey(REQUEST_DATE));
        assertFalse("approval date not set", reg.containsKey(APPROVAL_DATE));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGoodCreateWithSandbox() throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));
        EntityBody app = getNewApp();

        // test create during dup check
        // Mockito.when(
        // service.listIds(any(NeutralQuery.class)))
        // .thenReturn(new ArrayList<String>());

        resource.setAutoRegister(true);
        Response resp = resource.post(app, uriInfo);
        assertEquals(STATUS_CREATED, resp.getStatus());
        Map reg = (Map) app.get(REGISTRATION);
        assertTrue("Autoregistered", reg.get(STATUS).toString().equals("APPROVED"));
    }

    @Test
    public void testBadCreate1() throws URISyntaxException {   //include id in POST
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));
        EntityBody app = getNewApp();
        app.put("id", "123");

        Response resp = resource.post(app, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }


    @Test
    public void testBadCreate2() throws URISyntaxException {   //include client_id in POST
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));
        EntityBody app = getNewApp();
        app.put(CLIENT_ID, "123");

        Response resp = resource.post(app, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadCreate3() throws URISyntaxException {   // include client_secret in POST
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));
        EntityBody app = getNewApp();
        app.put(CLIENT_SECRET, "123");

        Response resp = resource.post(app, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadAsAdmin() throws URISyntaxException {   // include client_secret in POST
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        EntityBody app = getNewApp();
        app.put(CLIENT_SECRET, "123");

        Response resp = resource.post(app, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    private EntityBody getNewApp() {
        EntityBody app = new EntityBody();
        app.put("client_type", "PUBLIC");
        app.put("redirect_uri", "https://slidev.org");
        app.put("description", "blah blah blah");
        app.put("name", "TestApp");

        app.put("client_type", "PUBLIC");
        app.put("behavior", "Full Window App");
        app.put("is_admin", false);
        app.put("application_url", "https://slidev.org");
        app.put("administration_uri", "https://slidev.org");
        Map<String, Object> developer = new HashMap<String, Object>();
        developer.put("organization", "Acme");
        developer.put("license_accepted", true);
        Map<String, Object> registration = new HashMap<String, Object>();
        registration.put("status", ApplicationResource.STATUS_APPROVED);
        app.put("registration", registration);
        app.put("developer_info", developer);
        app.put("installed", false);
        return app;
    }

    @Test
    public void testGoodDelete() throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps"));
        EntityBody toDelete = getNewApp();
        Response created = resource.post(toDelete, uriInfo);
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Response resp = resource.delete(uuid, uriInfo);
        assertEquals(STATUS_DELETED, resp.getStatus());
    }

    @Test
    public void testBadDelete() throws URISyntaxException {
        String uuid = "9999999999";
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        try {
            @SuppressWarnings("unused")
            Response resp = resource.delete(uuid, uriInfo);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testGoodGet() throws URISyntaxException {
        String uuid = createApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Response resp = resource.getWithId(uuid, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
    }

    @Test
    public void testGoodGetAsOperator() throws URISyntaxException {
        EntityBody toGet = getNewApp();

        // Mock repo can't do real queries for arrays.

        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(toGet, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        toGet.put(ApplicationResource.AUTHORIZED_ED_ORGS, SecurityContextInjector.ED_ORG_ID);
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        created = unversionedResource.put(uuid, toGet, uriInfo);
        assertEquals(STATUS_NO_CONTENT, created.getStatus());
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response resp = resource.getAll(uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue(bodies.size() >= 1);
    }

    @Test
    public void testGoodGetAsDeveloper() throws URISyntaxException {
        EntityBody toGet = getNewApp();
        // Mock repo can't do real queries for arrays.

        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(toGet, uriInfo);
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_CREATED, created.getStatus());
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response resp = resource.getAll(uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue(bodies.size() == 1);
        assertTrue(bodies.get(0).get("id").equals(uuid));
    }

    @Test
    public void testEmptyGetAsAdmin() throws URISyntaxException {
        EntityBody toGet = getNewApp();
        // Mock repo can't do real queries for arrays.
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(toGet, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        toGet.put(ApplicationResource.AUTHORIZED_ED_ORGS, "3333-3333-3333");
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        created = unversionedResource.put(uuid, toGet, uriInfo);
        assertEquals(STATUS_NO_CONTENT, created.getStatus());
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response resp = resource.getAll(uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue(bodies.size() == 0);
    }

    @Test
    public void testGoodGetAsAdmin() throws URISyntaxException {
        EntityBody toGet = getNewApp();
        // Mock repo can't do real queries for arrays.
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(toGet, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        toGet.put(ApplicationResource.AUTHORIZED_ED_ORGS, SecurityContextInjector.ED_ORG_ID);
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        created = unversionedResource.put(uuid, toGet, uriInfo);
        assertEquals(STATUS_NO_CONTENT, created.getStatus());
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response resp = resource.getAll(uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue("expected entity response to contain 1 entity, received " + bodies.size(), bodies.size() == 1);
    }

    @Test
    public void testBadGet() throws URISyntaxException {
        String uuid = "9999999999";
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        try {
            Response resp = resource.getWithId(uuid, uriInfo);
            assertEquals(STATUS_NOT_FOUND, resp.getStatus());
        } catch (EntityNotFoundException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdate() throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        EntityBody app = getNewApp();
        Response created = resource.post(app, uriInfo);

        //switch to operator and approve
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());

        //switch back to developer and try to update
        SecurityContextHolder.clearContext();
        injector.setDeveloperContext();
        app.put("description", "coolest app ever.");
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());

    }


    @Test
    public void testSandboxAutoAuthorize() throws Exception {
        resource.setAutoRegister(true);
        String uuid = createApp();
        EntityBody app = getNewApp();
        app.put(ApplicationResource.AUTHORIZED_ED_ORGS, "12341234");
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Response updated = unversionedResource.put(uuid, app, uriInfo);
        assertEquals(STATUS_NO_CONTENT, updated.getStatus());
    }
    
    @Test
    public void testSandboxUpdateShouldSanitizeBadEdorgIds() throws Exception {
        resource.setAutoRegister(true);
        resource.setSandboxEnabled(true);
        injector.setDeveloperContext();
        String uuid = createApp();
        String edorgId = helper.generateEdorgWithParent(null).getEntityId();
        String edorgId2 = helper.generateEdorgWithParent(null).getEntityId();
        Map<String, Object> appBody = repo.findById("application", uuid).getBody();
        appBody.put(ApplicationResource.AUTHORIZED_ED_ORGS, Arrays.asList(edorgId, edorgId2, "Waffles"));
        EntityBody body = new EntityBody(appBody);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Response updated = resource.put(uuid, body, uriInfo);
        assertEquals(STATUS_NO_CONTENT, updated.getStatus());
        appBody = repo.findById("application", uuid).getBody();
        assertEquals(((List) appBody.get(ApplicationResource.AUTHORIZED_ED_ORGS)).size(), 2);
    }
    
    @Test
    public void testSandboxAutoAuthorizeWithNoDuplicates() throws Exception {
        resource.setAutoRegister(true);
        resource.setSandboxEnabled(true);
        injector.setDeveloperContext();
        String uuid = createApp();
        String edorgId = helper.generateEdorgWithParent(null).getEntityId();
        String edorgId2 = helper.generateEdorgWithParent(null).getEntityId();
        Map<String, Object> appBody = repo.findById("application", uuid).getBody();
        appBody.put(ApplicationResource.AUTHORIZED_ED_ORGS, Arrays.asList(edorgId, edorgId, edorgId2));
        EntityBody body = new EntityBody(appBody);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Response updated = resource.put(uuid, body, uriInfo);
        assertEquals(STATUS_NO_CONTENT, updated.getStatus());
        appBody = repo.findById("application", uuid).getBody();
        assertEquals(((List) appBody.get(ApplicationResource.AUTHORIZED_ED_ORGS)).size(), 2);
    }

    private String createApp() throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        EntityBody app = getNewApp();
        Response created = resource.post(app, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        return parseIdFromLocation(created);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testUpdateRegistrationAsDeveloper() throws URISyntaxException {
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_BAD_REQUEST, resource.put(uuid, app, uriInfo).getStatus());
    }

    @Test
    public void testUpdateWhilePending() throws URISyntaxException {
        //a pending application is read-only accept for operator changing registration status
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_BAD_REQUEST, resource.put(uuid, app, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateApprovalDate() throws URISyntaxException {
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response resp = resource.post(app, uriInfo);
        String uuid = parseIdFromLocation(resp);
        Map reg = getRegistrationDataForApp(uuid);
        reg.put(APPROVAL_DATE, 2343L);
        app.put(REGISTRATION, reg);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_BAD_REQUEST, resource.put(uuid, app, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateRequestDate() throws URISyntaxException {
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response resp = resource.post(app, uriInfo);
        String uuid = parseIdFromLocation(resp);
        Map reg = getRegistrationDataForApp(uuid);
        reg.put(REQUEST_DATE, 2343L);
        app.put(REGISTRATION, reg);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_BAD_REQUEST, resource.put(uuid, app, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateRegistrationAsOperator() throws URISyntaxException {
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        String uuid = parseIdFromLocation(created);
        // Switch to operator
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateAppAsOperator() throws URISyntaxException {
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        // Switch to operator
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        //app.put("registered", false);
        app.put("name", "Super mega awesome app!");
        String uuid = parseIdFromLocation(created);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_BAD_REQUEST, resource.put(uuid, app, uriInfo).getStatus());

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void denyApplication() throws URISyntaxException {
        // Create - Deny
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "DENIED");
        app.put(REGISTRATION, registration);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void approveApplication() throws URISyntaxException {
        //Create - Approve
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());
        Map reg = getRegistrationDataForApp(uuid);
        assertTrue("approval date set", reg.containsKey(APPROVAL_DATE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getRegistrationDataForApp(String uuid) throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        Response resp = resource.getWithId(uuid, uriInfo);
        Map data = (Map) resp.getEntity();
        Map toReturn = new HashMap();
        toReturn.putAll((Map) ((Map) data.get("application")).get(REGISTRATION));
        return toReturn;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void unregisterApplication() throws URISyntaxException {
        //Create - Approve - Unregister
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());

        registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "UNREGISTERED");
        app.put(REGISTRATION, registration);

        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());
        Map reg = getRegistrationDataForApp(uuid);
        assertFalse("approval date not set", reg.containsKey(APPROVAL_DATE));
        assertFalse("request date not set", reg.containsKey(REQUEST_DATE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void resubmitDeniedApplication() throws URISyntaxException {
        //Create - Deny - Dev Update
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "DENIED");
        app.put(REGISTRATION, registration);
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());

        SecurityContextHolder.clearContext();
        injector.setDeveloperContext();
        app.put("name", "My new app name");
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/" + uuid));
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());
        Response resp = resource.getWithId(uuid, uriInfo);
        Map data = (Map) resp.getEntity();
        Map reg = (Map) ((Map) data.get("application")).get(REGISTRATION);
        assertEquals("back to pending", "PENDING", reg.get(STATUS));
        assertTrue("request date set", reg.containsKey(REQUEST_DATE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void unregisterDeniedApplication() throws URISyntaxException {
        //Create - Deny - Unregister
        EntityBody app = getNewApp();
        when(uriInfo.getRequestUri()).thenReturn(new URI("http://some.net/api/rest/apps/"));
        Response created = resource.post(app, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "DENIED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.put(uuid, app, uriInfo).getStatus());

        registration = new HashMap();
        registration.put(STATUS, "UNREGISTERED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_BAD_REQUEST, resource.put(uuid, app, uriInfo).getStatus());
    }


    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        Assert.assertNotNull(locationHeaders);
        Assert.assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/([\\w-]+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        Assert.assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }


}

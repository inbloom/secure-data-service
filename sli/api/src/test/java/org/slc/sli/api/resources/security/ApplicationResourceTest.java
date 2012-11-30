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
import static org.slc.sli.api.resources.security.ApplicationResource.RESOURCE_NAME;
import static org.slc.sli.api.resources.security.ApplicationResource.STATUS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;

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
public class ApplicationResourceTest {

    @Autowired
    private ApplicationResource resource;

    @Autowired
    private SecurityContextInjector injector;

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
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();

        repo.deleteAll("application", null);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGoodCreate() {
        EntityBody app = getNewApp();

        Response resp = resource.createApplication(app, headers, uriInfo);
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
    public void testGoodCreateWithSandbox() {
        EntityBody app = getNewApp();

        // test create during dup check
        // Mockito.when(
        // service.listIds(any(NeutralQuery.class)))
        // .thenReturn(new ArrayList<String>());

        resource.setAutoRegister(true);
        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_CREATED, resp.getStatus());
        Map reg = (Map) app.get(REGISTRATION);
        assertTrue("Autoregistered", reg.get(STATUS).toString().equals("APPROVED"));
    }

    @Test
    public void testBadCreate1() {   //include id in POST
        EntityBody app = getNewApp();
        app.put("id", "123");

        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }


    @Test
    public void testBadCreate2() {   //include client_id in POST
        EntityBody app = getNewApp();
        app.put(CLIENT_ID, "123");

        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadCreate3() {   // include client_secret in POST
        EntityBody app = getNewApp();
        app.put(CLIENT_SECRET, "123");

        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadAsAdmin() {   // include client_secret in POST
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        EntityBody app = getNewApp();
        app.put(CLIENT_SECRET, "123");

        Response resp = resource.createApplication(app, headers, uriInfo);
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
        app.put("developer_info", developer);
        app.put("installed", false);
        return app;
    }

    @Test
    public void testGoodDelete() {
        EntityBody toDelete = getNewApp();
        Response created = resource.createApplication(toDelete, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        Response resp = resource.deleteApplication(uuid, headers, uriInfo);
        assertEquals(STATUS_DELETED, resp.getStatus());
    }

    @Test
    public void testBadDelete() {
        String uuid = "9999999999";
        try {
            @SuppressWarnings("unused")
            Response resp = resource.deleteApplication(uuid, headers, uriInfo);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testGoodGet() {
        String uuid = createApp();
        Response resp = resource.getApplication(uuid, headers, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
    }

    @Test
    public void testGoodGetAsOperator() {
        EntityBody toGet = getNewApp();

        // Mock repo can't do real queries for arrays.

        Response created = resource.createApplication(toGet, headers, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        toGet.put(ApplicationResource.AUTHORIZED_ED_ORGS, SecurityContextInjector.ED_ORG_ID);
        String uuid = parseIdFromLocation(created);
        created = resource.update(uuid, toGet, headers, uriInfo);
        assertEquals(STATUS_NO_CONTENT, created.getStatus());
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        Response resp = resource.getApplications(0, 50, headers, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue(bodies.size() >= 1);
    }

    @Test
    public void testGoodGetAsDeveloper() {
        EntityBody toGet = getNewApp();
        // Mock repo can't do real queries for arrays.

        Response created = resource.createApplication(toGet, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        assertEquals(STATUS_CREATED, created.getStatus());
        Response resp = resource.getApplications(0, 50, headers, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue(bodies.size() == 1);
        assertTrue(bodies.get(0).get("id").equals(uuid));
    }

    @Test
    public void testEmptyGetAsAdmin() {
        EntityBody toGet = getNewApp();
        // Mock repo can't do real queries for arrays.
        Response created = resource.createApplication(toGet, headers, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        toGet.put(ApplicationResource.AUTHORIZED_ED_ORGS, "3333-3333-3333");
        String uuid = parseIdFromLocation(created);
        created = resource.update(uuid, toGet, headers, uriInfo);
        assertEquals(STATUS_NO_CONTENT, created.getStatus());
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        Response resp = resource.getApplications(0, 50, headers, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue(bodies.size() == 0);
    }

    @Test
    public void testGoodGetAsAdmin() {
        EntityBody toGet = getNewApp();
        // Mock repo can't do real queries for arrays.
        Response created = resource.createApplication(toGet, headers, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        toGet.put(ApplicationResource.AUTHORIZED_ED_ORGS, SecurityContextInjector.ED_ORG_ID);
        String uuid = parseIdFromLocation(created);
        created = resource.update(uuid, toGet, headers, uriInfo);
        assertEquals(STATUS_NO_CONTENT, created.getStatus());
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        Response resp = resource.getApplications(0, 50, headers, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
        EntityResponse entityResponse = (EntityResponse) resp.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> bodies = (List<EntityBody>) entityResponse.getEntity();
        assertTrue("expected entity response to contain 1 entity, received " + bodies.size(), bodies.size() == 1);
    }

    @Test
    public void testBadGet() {
        String uuid = "9999999999";
        try {
            Response resp = resource.getApplication(uuid, headers, uriInfo);
            assertEquals(STATUS_NOT_FOUND, resp.getStatus());
        } catch (EntityNotFoundException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }




    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdate() {
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);

        //switch to operator and approve
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

        //switch back to developer and try to update
        SecurityContextHolder.clearContext();
        injector.setDeveloperContext();
        app.put("description", "coolest app ever.");
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

    }

    @Test
    public void testSandboxAutoAuthorize() throws Exception {
        resource.setAutoRegister(true);
        String uuid = createApp();
        EntityBody app = getNewApp();
        app.put(ApplicationResource.AUTHORIZED_ED_ORGS, "12341234");
        Response updated = resource.update(uuid, app, headers, uriInfo);
        assertEquals(STATUS_NO_CONTENT, updated.getStatus());
    }

    private String createApp() {
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        return parseIdFromLocation(created);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testUpdateRegistrationAsDeveloper() {
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_BAD_REQUEST, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
    }

    @Test
    public void testUpdateWhilePending() {
        //a pending application is read-only accept for operator changing registration status
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        assertEquals(STATUS_BAD_REQUEST, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateApprovalDate() {
        EntityBody app = getNewApp();
        Response resp = resource.createApplication(app, headers, uriInfo);
        String uuid = parseIdFromLocation(resp);
        Map reg = getRegistrationDataForApp(uuid);
        reg.put(APPROVAL_DATE, 2343L);
        app.put(REGISTRATION, reg);
        assertEquals(STATUS_BAD_REQUEST, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateRequestDate() {
        EntityBody app = getNewApp();
        Response resp = resource.createApplication(app, headers, uriInfo);
        String uuid = parseIdFromLocation(resp);
        Map reg = getRegistrationDataForApp(uuid);
        reg.put(REQUEST_DATE, 2343L);
        app.put(REGISTRATION, reg);
        assertEquals(STATUS_BAD_REQUEST, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateRegistrationAsOperator() {
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        // Switch to operator
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateAppAsOperator() {
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        // Switch to operator
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        //app.put("registered", false);
        app.put("name", "Super mega awesome app!");
        String uuid = parseIdFromLocation(created);
        assertEquals(STATUS_BAD_REQUEST, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void denyApplication() {
        // Create - Deny
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "DENIED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void approveApplication() {
        //Create - Approve
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
        Map reg = getRegistrationDataForApp(uuid);
        assertTrue("approval date set", reg.containsKey(APPROVAL_DATE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getRegistrationDataForApp(String uuid) {
        Response resp = resource.getApplication(uuid, headers, uriInfo);
        Map data = (Map) resp.getEntity();
        Map toReturn = new HashMap();
        toReturn.putAll((Map) ((Map) data.get(RESOURCE_NAME)).get(REGISTRATION));
        return toReturn;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void unregisterApplication() {
        //Create - Approve - Unregister
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "APPROVED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

        registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "UNREGISTERED");
        app.put(REGISTRATION, registration);

        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
        Map reg = getRegistrationDataForApp(uuid);
        assertFalse("approval date not set", reg.containsKey(APPROVAL_DATE));
        assertFalse("request date not set", reg.containsKey(REQUEST_DATE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void resubmitDeniedApplication() {
        //Create - Deny - Dev Update
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "DENIED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

        SecurityContextHolder.clearContext();
        injector.setDeveloperContext();
        app.put("name", "My new app name");
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
        Response resp = resource.getApplication(uuid, headers, uriInfo);
        Map data = (Map) resp.getEntity();
        Map reg = (Map) ((Map) data.get("application")).get(REGISTRATION);
        assertEquals("back to pending", "PENDING", reg.get(STATUS));
        assertTrue("request date set", reg.containsKey(REQUEST_DATE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void unregisterDeniedApplication() {
        //Create - Deny - Unregister
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        SecurityContextHolder.clearContext();
        injector.setOperatorContext();
        String uuid = parseIdFromLocation(created);
        Map registration = getRegistrationDataForApp(uuid);
        registration.put(STATUS, "DENIED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

        registration = new HashMap();
        registration.put(STATUS, "UNREGISTERED");
        app.put(REGISTRATION, registration);
        assertEquals(STATUS_BAD_REQUEST, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());
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

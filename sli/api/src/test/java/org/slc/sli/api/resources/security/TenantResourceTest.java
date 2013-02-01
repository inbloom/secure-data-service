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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.security.TenantResource.TenantResourceCreationException;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Unit tests for the resource representing a tenant
 *
 * @author srichards
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TenantResourceTest {

    @Autowired
    private TenantResourceImpl tenantResource; // class under test

    @Autowired
    private SecurityContextInjector injector;

    private UriInfo uriInfo;
    private SecurityUtilProxy secUtil;

    private static final String TENANT_1 = "IL";
    private static final String TENANT_2 = "NC";
    private static final String TENANT_3 = "NY";
    private static final String ED_ORG_1 = "STANDARD-SEA";
    private static final String ED_ORG_2 = "Sunset";
    private static final String MSDS_ED_ORG = "CAP0";

    @Autowired
    private Repository<Entity> repo;

    @Before
    public void setup() throws Exception {

        uriInfo = ResourceTestUtil.buildMockUriInfo(null);

        // inject administrator security context for unit testing
        injector.setRealmAdminContext();

        secUtil = mock(SecurityUtilProxy.class);
        tenantResource.setSecUtil(secUtil);
        when(uriInfo.getQueryParameters()).thenReturn(new MultivaluedMapImpl());

        tenantResource.setIngestionServerList(Arrays.asList("FIRST", "Second", "third"));
    }

    private Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(TenantResourceImpl.TENANT_ID, TENANT_1);

        Map<String, Object> landingZone = new HashMap<String, Object>();
        landingZone.put(TenantResourceImpl.LZ_INGESTION_SERVER, "example.com");
        landingZone.put(TenantResourceImpl.LZ_EDUCATION_ORGANIZATION, ED_ORG_1);
        landingZone.put(TenantResourceImpl.LZ_DESC, "Landing zone for IL_DAYBREAK");
        landingZone.put(TenantResourceImpl.LZ_PATH,
                "C:\\code\\sli\\sli\\ingestion\\ingestion-service\\target\\ingestion\\lz\\inbound\\IL-STATE-DAYBREAK");

        Map<String, Object> preload = new HashMap<String, Object>();
        preload.put(TenantResourceImpl.LZ_PRELOAD_STATUS, TenantResourceImpl.LZ_PRELOAD_STATUS_READY);
        preload.put(TenantResourceImpl.LZ_PRELOAD_FILES, Arrays.asList("small_sample_dataset", "medium_sample_dataset"));
        landingZone.put(TenantResourceImpl.LZ_PRELOAD, preload);

        List<Map<String, Object>> landingZones = new ArrayList<Map<String, Object>>();
        landingZones.add(landingZone);
        entity.put(TenantResourceImpl.LZ, landingZones);
        return entity;
    }

    private Map<String, Object> createTestAppendEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(TenantResourceImpl.TENANT_ID, TENANT_1);
        Map<String, Object> landingZone = new HashMap<String, Object>();
        landingZone.put(TenantResourceImpl.LZ_INGESTION_SERVER, "example.com");
        landingZone.put(TenantResourceImpl.LZ_EDUCATION_ORGANIZATION, ED_ORG_2);
        landingZone.put(TenantResourceImpl.LZ_DESC, "Landing zone for IL_SUNSET");
        landingZone.put(TenantResourceImpl.LZ_PATH,
                "C:\\code\\sli\\sli\\ingestion\\ingestion-service\\target\\ingestion\\lz\\inbound\\IL-STATE-SUNSET");

        Map<String, Object> preload = new HashMap<String, Object>();
        preload.put(TenantResourceImpl.LZ_PRELOAD_STATUS, TenantResourceImpl.LZ_PRELOAD_STATUS_READY);
        preload.put(TenantResourceImpl.LZ_PRELOAD_FILES, Arrays.asList("large_sample_dataset"));
        landingZone.put(TenantResourceImpl.LZ_PRELOAD, preload);

        List<Map<String, Object>> landingZones = new ArrayList<Map<String, Object>>();
        landingZones.add(landingZone);
        entity.put(TenantResourceImpl.LZ, landingZones);
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(TenantResourceImpl.TENANT_ID, TENANT_2);
        Map<String, Object> landingZone = new HashMap<String, Object>();
        landingZone.put(TenantResourceImpl.LZ_INGESTION_SERVER, "example.com");
        landingZone.put(TenantResourceImpl.LZ_EDUCATION_ORGANIZATION, ED_ORG_2);
        landingZone.put(TenantResourceImpl.LZ_DESC, "Landing zone for IL_SUNSET");
        landingZone.put(TenantResourceImpl.LZ_PATH,
                "C:\\code\\sli\\sli\\ingestion\\ingestion-service\\target\\ingestion\\lz\\inbound\\IL-STATE-SUNSET");
        List<Map<String, Object>> landingZones = new ArrayList<Map<String, Object>>();
        landingZones.add(landingZone);
        entity.put(TenantResourceImpl.LZ, landingZones);
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put(TenantResourceImpl.TENANT_ID, TENANT_3);
        Map<String, Object> landingZone = new HashMap<String, Object>();
        landingZone.put(TenantResourceImpl.LZ_INGESTION_SERVER, "example.com");
        landingZone.put(TenantResourceImpl.LZ_EDUCATION_ORGANIZATION, "NYC");
        landingZone.put(TenantResourceImpl.LZ_DESC, "Landing zone for NY");
        landingZone.put(TenantResourceImpl.LZ_PATH,
                "C:\\code\\sli\\sli\\ingestion\\ingestion-service\\target\\ingestion\\lz\\inbound\\NY-STATE-NYC");

        Map<String, Object> preload = new HashMap<String, Object>();
        preload.put(TenantResourceImpl.LZ_PRELOAD_STATUS, TenantResourceImpl.LZ_PRELOAD_STATUS_READY);
        preload.put(TenantResourceImpl.LZ_PRELOAD_FILES, Arrays.asList("huge_sample_dataset"));
        landingZone.put(TenantResourceImpl.LZ_PRELOAD, preload);

        List<Map<String, Object>> landingZones = new ArrayList<Map<String, Object>>();
        landingZones.add(landingZone);
        entity.put(TenantResourceImpl.LZ, landingZones);
        return entity;
    }

    @Test
    public void testCreate() throws URISyntaxException {
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants"));
        Response response = tenantResource.post(new EntityBody(createTestEntity()), uriInfo);
        assertEquals("Status code should be 403", Status.FORBIDDEN.getStatusCode(), response.getStatus());

        // String id = ResourceTestUtil.parseIdFromLocation(response);
        // assertNotNull("ID should not be null", id);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAppends() throws URISyntaxException {

        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants"));

        String id1 = createLandingZone(new EntityBody(createTestEntity()));
        String id2 = createLandingZone(new EntityBody(createTestAppendEntity()));

        assertEquals("Both creates should return same id", id1, id2);

        // try to get it
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + id1));
        Response getResponse = tenantResource.getWithId(id1, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        List<Map<String, Object>> landingZones = (List<Map<String, Object>>) body.get(TenantResourceImpl.LZ);
        assertEquals("Should have 2 landing zones", 2, landingZones.size());

    }

    @Test
    public void testRead() throws URISyntaxException {
        // create one entity
        String id = createLandingZone(new EntityBody(createTestEntity()));

        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + id));
        Response response = tenantResource.getWithId(id, uriInfo);

        Object responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
        } else {
            fail("Response entity not recognized: " + response);
        }
    }

    @Test (expected = EntityNotFoundException.class)
    public void testDelete() throws URISyntaxException {
        // create one entity
        String id = createLandingZone(new EntityBody(createTestEntity()));

        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + id));
        // delete it
        Response response = tenantResource.delete(id, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Response getResponse = tenantResource.getWithId(id, uriInfo);
    }

    @Test
    public void testUpdate() throws URISyntaxException {
        // create one entity
        String id = createLandingZone(new EntityBody(createTestEntity()));
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + id));

        // update it
        Response response = tenantResource.put(id, new EntityBody(createTestUpdateEntity()), uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // try to get it
        Response getResponse = tenantResource.getWithId(id, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(TenantResourceImpl.TENANT_ID + " should be " + TENANT_2, body.get(TenantResourceImpl.TENANT_ID),
                TENANT_2);
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() throws URISyntaxException {
        // create two entities
        createLandingZone(new EntityBody(createTestEntity()));
        createLandingZone(new EntityBody(createTestSecondaryEntity()));
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants"));

        // read everything
        Response response = tenantResource.getAll(uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testReadCommaSeparatedResources() throws URISyntaxException {
        String idList = getIDList("tenants");
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + idList));
        Response response = tenantResource.getWithId(idList, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", results.size(), 2);

        // Order is not guaranteed
        for (EntityBody body : results) {
            assertNotNull("Should not be null", body);
            assertNotNull("Should include links", body.get(ResourceConstants.LINKS));

            assertTrue(TenantResourceImpl.TENANT_ID + " should be in {" + TENANT_1 + ", " + TENANT_3 + "}",
                    (TENANT_1.equals(body.get(TenantResourceImpl.TENANT_ID)) || TENANT_3.equals(body
                            .get(TenantResourceImpl.TENANT_ID))));
        }
    }

    @Test
    public void testIngestionServerAssignment() throws URISyntaxException {
        createLandingZone(new EntityBody(createTestEntity()));
        createLandingZone(new EntityBody(createTestAppendEntity()));
        createLandingZone(new EntityBody(createTestSecondaryEntity()));
        // read everything
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants"));
        Response response = tenantResource.getAll(uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        @SuppressWarnings("unchecked")
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);

        Map<String, Integer> serverCounts = new HashMap<String, Integer>();
        for (EntityBody body : results) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> landingZones = (List<Map<String, Object>>) body.get(TenantResourceImpl.LZ);
            for (Map<String, Object> lz : landingZones) {
                String server = (String) lz.get(TenantResourceImpl.LZ_INGESTION_SERVER);
                Integer count = serverCounts.get(server);
                if (null == count) {
                    serverCounts.put(server, new Integer(1));
                } else {
                    serverCounts.put(server, new Integer(count + 1));
                }
            }
        }
        assertTrue("Should have used all ingestion servers", 3 <= serverCounts.size());
    }

    private String getIDList(String resource) {
        String id1 = createLandingZone(new EntityBody(createTestEntity()));
        String id2 = createLandingZone(new EntityBody(createTestSecondaryEntity()));

        return id1 + "," + id2;
    }

    private String createLandingZone(EntityBody entity) {
        Object response = null;
        try {
            response = tenantResource.createLandingZone(entity, false);
        } catch (TenantResourceCreationException e) {
            error(e.getMessage());
        }
        assertNotNull(response);
        assertTrue(response instanceof String);
        String id = (String) response;
        return id;
    }

    @Test
    public void testPreloadSmall() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        injector.setDeveloperContext();
        Map<String, Object> entity = createTestEntity();
        entity.remove(TenantResourceImpl.LZ_PRELOAD);
        String id = createLandingZone(new EntityBody(entity));
        when(secUtil.getTenantId()).thenReturn(TENANT_1);
        tenantResource.setSandboxEnabled(true);
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + id + "/preload"));
        Response r = tenantResource.preload(id, "small", uriInfo);
        assertEquals(Status.CREATED.getStatusCode(), r.getStatus());
        Map<String, Object> e = repo.findById("tenant", id).getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> landingZone = ((List<Map<String, Object>>) e.get("landingZone")).get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> preload = (Map<String, Object>) landingZone.get("preload");
        assertEquals(Arrays.asList("small"), preload.get("files"));
        assertEquals("ready", preload.get("status"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testPreloadMedium() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        injector.setDeveloperContext();
        Map<String, Object> entity = createTestEntity();
        entity.remove(TenantResourceImpl.LZ_PRELOAD);
        ((List<Map<String,Object>>)entity.get(TenantResourceImpl.LZ)).get(0).put(TenantResourceImpl.LZ_EDUCATION_ORGANIZATION, MSDS_ED_ORG);
        String id = createLandingZone(new EntityBody(entity));
        when(secUtil.getTenantId()).thenReturn(TENANT_1);
        tenantResource.setSandboxEnabled(true);
        when(uriInfo.getRequestUri()).thenReturn(new URI("/rest/tenants/" + id + "/preload"));
        Response r = tenantResource.preload(id, "medium", uriInfo);
        assertEquals(Status.CREATED.getStatusCode(), r.getStatus());
        Map<String, Object> e = repo.findById("tenant", id).getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> landingZone = ((List<Map<String, Object>>) e.get("landingZone")).get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> preload = (Map<String, Object>) landingZone.get("preload");
        assertEquals(Arrays.asList("medium"), preload.get("files"));
        assertEquals("ready", preload.get("status"));
    }

    @Test
    public void testLandingZoneLocked() {
        injector.setDeveloperContext();
        String id = createLandingZone(new EntityBody(createTestEntity()));
        Map<String, Object> entity = repo.findById("tenant", id).getBody();
        @SuppressWarnings("unchecked")
        Repository<Entity> mockRepo = mock(Repository.class);
        IngestionOnboardingLockChecker lockChecker = new IngestionOnboardingLockChecker(mockRepo);

        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", "=", entity.get("tenantId")));
        query.addCriteria(new NeutralCriteria("tenantIsReady", "=", false));
        when(mockRepo.findOne("tenant", query)).thenReturn(new MongoEntity("tenant", new HashMap<String, Object>()));
        tenantResource.setLockChecker(lockChecker);
        // try {
        tenantResource.setSandboxEnabled(true);
        when(secUtil.getTenantId()).thenReturn(TENANT_1);
        Response response = tenantResource.preload(id, "small", uriInfo);
        // fail("Should block preloading data in landing zone when ingesiton is running");
        // } catch (TenantResourceCreationException e) {
        // assertEquals(Status.CONFLICT, e.getStatus());
        // }
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
    }
}

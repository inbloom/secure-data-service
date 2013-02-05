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

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.security.TenantResource.LandingZoneInfo;
import org.slc.sli.api.resources.security.TenantResource.TenantResourceCreationException;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * JUnit for Onboarding Resource
 *
 * @author nbrown
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class OnboardingResourceTest {

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    @InjectMocks
    private OnboardingResource resource;

    @Autowired
    private MockRepo repo;

    @Mock
    private TenantResource mockTenantResource;

    UriInfo uriInfo = null;
    HttpHeaders headers = null;

    String dashboardId = "";
    String databrowserId = "";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector.setDeveloperContext();
        resource.isSandboxEnabled = true;
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());

        // mockTenantResource = mock(TenantResource.class);

        // clear all related collections
        repo.deleteAll("educationOrganization", null);

    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        repo.deleteAll("educationalOrganization", null);
    }
    
    @Test
    public void testBadData() {
        Response response = resource.provision(new HashMap<String, String>(), null);
        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProvision() {
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put(OnboardingResource.STATE_EDORG_ID, "TestOrg");
        requestBody.put(ResourceConstants.ENTITY_METADATA_TENANT_ID, "12345");
        requestBody.put(OnboardingResource.PRELOAD_FILES_ID, "small_sample_dataset");

        LandingZoneInfo landingZone = new LandingZoneInfo("LANDING ZONE", "INGESTION SERVER");

        Map<String, String> tenantBody = new HashMap<String, String>();
        tenantBody.put("landingZone", "LANDING ZONE");
        tenantBody.put("ingestionServer", "INGESTION SERVER");

        // Entity tenantEntity = Mockito.mock(Entity.class);
        // when(tenantEntity.getBody()).thenReturn(tenantBody);
        try {
            when(mockTenantResource.createLandingZone(Mockito.anyString(), Mockito.eq("TestOrg"), Mockito.anyBoolean())).thenReturn(landingZone);
        } catch (TenantResourceCreationException e) {
            Assert.fail(e.getMessage());
        }

        Response res = resource.provision(requestBody, null);
        assertTrue(Status.fromStatusCode(res.getStatus()) == Status.CREATED);
        Map<String, String> result = (Map<String, String>) res.getEntity();
        assertNotNull(result.get("landingZone"));
        Assert.assertEquals("LANDING ZONE", result.get("landingZone"));
        assertNotNull(result.get("serverName"));
        Assert.assertEquals("landingZone", result.get("serverName"));

        // Attempt to create the same edorg.
        res = resource.provision(requestBody, null);
        assertEquals(Status.CREATED, Status.fromStatusCode(res.getStatus()));
    }



}

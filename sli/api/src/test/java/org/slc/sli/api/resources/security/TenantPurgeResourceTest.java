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

import static org.junit.Assert.assertEquals;
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

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * JUnit for Tenant Purge Resource
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class TenantPurgeResourceTest {

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    @InjectMocks
    private TenantPurgeResource resource;

    UriInfo uriInfo = null;
    HttpHeaders headers = null;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        injector.setSeaAdminContext();
        resource.isSandboxEnabled = false;
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testPurge() {
        injector.setSeaAdminContext();
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put(OnboardingResource.STATE_EDORG_ID, "TestOrg");
        requestBody.put(ResourceConstants.ENTITY_METADATA_TENANT_ID, "12345");

        // Verify the purge operation completed successfully.
        Response res = resource.purge(requestBody, null);
        assertEquals(Status.OK, Status.fromStatusCode(res.getStatus()));
    }

    @Test
    public void testNotAuthorized() {
        injector.setLeaAdminContext();
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put(ResourceConstants.ENTITY_METADATA_TENANT_ID, "12345");

        // Verify the user was not authorized to purge.
        Response res = resource.purge(requestBody, null);
        assertEquals(Status.FORBIDDEN, Status.fromStatusCode(res.getStatus()));
    }

}

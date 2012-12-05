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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Unit Tests for Security Event Resource class.
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SecurityEventResourceTest {

    @Autowired
    SecurityEventResource resource;

    @Autowired
    SecurityContextInjector injector;

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;
    private static boolean isExecuted = false;

    @Before
    public void setup() throws Exception {
        uriInfo = ResourceTestUtil.buildMockUriInfo(null);

        // inject SLC Operator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());

        synchronized (this) {
            if (!isExecuted) {
                isExecuted = true;

                // create entities
                resource.createSecurityEvent(new EntityBody(sampleEntity1()), httpHeaders, uriInfo);
                resource.createSecurityEvent(new EntityBody(sampleEntity2()), httpHeaders, uriInfo);
                resource.createSecurityEvent(new EntityBody(sampleEntity3()), httpHeaders, uriInfo);
                resource.createSecurityEvent(new EntityBody(sampleEntity4()), httpHeaders, uriInfo);
                resource.createSecurityEvent(new EntityBody(sampleEntity5()), httpHeaders, uriInfo);
            }
        }
    }

    private Map<String, Object> sampleEntity1() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("user", "LoginUser");

        List<String> roles = Arrays.asList(RoleInitializer.REALM_ADMINISTRATOR, RoleInitializer.SEA_ADMINISTRATOR);
        entity.put("roles", roles);

        entity.put("targetEdOrg", "some-faked-EdOrg");
        entity.put("actionUri", "/simple-idp/login");
        entity.put("appId", "SimpleIDP");
        entity.put("executedOn", "localhost.localdomain");
        entity.put("userOrigin", "127.0.0.1");
        entity.put("timeStamp", "1337185304942");
        entity.put("processNameOrId", "18325@devmegatron");
        entity.put("className", "org.slc.sli.sandbox.idp.controller.Login");
        entity.put("logLevel", "TYPE_ERROR");
        entity.put("logMessage", "Failed login to SLIAdmin by devtest.");

        return entity;
    }

    private Map<String, Object> sampleEntity2() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("user", "LoginUser");

        List<String> roles = Arrays.asList(RoleInitializer.REALM_ADMINISTRATOR);
        entity.put("roles", roles);

        entity.put("targetEdOrg", "some-faked-EdOrg");
        entity.put("actionUri", "/simple-idp/login");
        entity.put("appId", "SimpleIDP");
        entity.put("executedOn", "localhost.localdomain");
        entity.put("userOrigin", "127.0.0.1");
        entity.put("timeStamp", "1337185304942");
        entity.put("processNameOrId", "18325@devmegatron");
        entity.put("className", "org.slc.sli.sandbox.idp.controller.Login");
        entity.put("logLevel", "TYPE_ERROR");
        entity.put("logMessage", "Failed login to SLIAdmin by devtest.");

        return entity;
    }

    private Map<String, Object> sampleEntity3() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("user", "LoginUser");

        List<String> roles = Arrays.asList(RoleInitializer.SEA_ADMINISTRATOR);
        entity.put("roles", roles);

        entity.put("targetEdOrg", "some-faked-EdOrg");
        entity.put("actionUri", "/simple-idp/login");
        entity.put("appId", "SimpleIDP");
        entity.put("executedOn", "localhost.localdomain");
        entity.put("userOrigin", "127.0.0.1");
        entity.put("timeStamp", "1337185304942");
        entity.put("processNameOrId", "18325@devmegatron");
        entity.put("className", "org.slc.sli.sandbox.idp.controller.Login");
        entity.put("logLevel", "TYPE_ERROR");
        entity.put("logMessage", "Failed login to SLIAdmin by devtest.");

        return entity;
    }

    private Map<String, Object> sampleEntity4() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("user", "LoginUser");

        List<String> roles = Arrays.asList(RoleInitializer.LEA_ADMINISTRATOR);
        entity.put("roles", roles);

        entity.put("targetEdOrg", "some-faked-EdOrg");
        entity.put("actionUri", "/simple-idp/login");
        entity.put("appId", "SimpleIDP");
        entity.put("executedOn", "localhost.localdomain");
        entity.put("userOrigin", "127.0.0.1");
        entity.put("timeStamp", "1337185304942");
        entity.put("processNameOrId", "18325@devmegatron");
        entity.put("className", "org.slc.sli.sandbox.idp.controller.Login");
        entity.put("logLevel", "TYPE_ERROR");
        entity.put("logMessage", "Failed login to SLIAdmin by devtest.");

        return entity;
    }

    private Map<String, Object> sampleEntity5() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("user", "LoginUser");

        List<String> roles = Arrays.asList(RoleInitializer.SLC_OPERATOR);
        entity.put("roles", roles);

        entity.put("targetEdOrg", "some-faked-EdOrg");
        entity.put("actionUri", "/simple-idp/login");
        entity.put("appId", "SimpleIDP");
        entity.put("executedOn", "localhost.localdomain");
        entity.put("userOrigin", "127.0.0.1");
        entity.put("timeStamp", "1337185304942");
        entity.put("processNameOrId", "18325@devmegatron");
        entity.put("className", "org.slc.sli.sandbox.idp.controller.Login");
        entity.put("logLevel", "TYPE_ERROR");
        entity.put("logMessage", "Failed login to SLIAdmin by devtest.");

        return entity;
    }


    @Test
    public void testSLCOperatorOffsetGetSecurityEvents() {
        injector.setOperatorContext();

        Response response = resource.getSecurityEvents(3, 100, httpHeaders, uriInfo);

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
            assertTrue("Should have three entities, but the actual count is " + results.size(), results.size() == 2);
        } else {
            fail("Response entity not recognized: " + response);
        }

    }

    @Test
    public void testSLCOperatorLimitGetSecurityEvents() {
        injector.setOperatorContext();

        Response response = resource.getSecurityEvents(0, 2, httpHeaders, uriInfo);

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
            assertTrue("Should have two entities, but the actual count is " + results.size(), results.size() == 2);
        } else {
            fail("Response entity not recognized: " + response);
        }

    }

    @Test
    public void testSLCOperatorOffsetLimitGetSecurityEvents() {
        injector.setOperatorContext();

        Response response = resource.getSecurityEvents(1, 3, httpHeaders, uriInfo);

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
            assertTrue("Should have three entities, but the actual count is " + results.size(), results.size() == 3);
        } else {
            fail("Response entity not recognized: " + response);
        }

    }

    @Test
    public void testSLCOperatorGetSecurityEvents() {
        injector.setOperatorContext();

        Response response = resource.getSecurityEvents(0, 100, httpHeaders, uriInfo);

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
            assertTrue("Should have five entities, but the actual count is " + results.size(), results.size() == 5);
        } else {
            fail("Response entity not recognized: " + response);
        }

    }

}

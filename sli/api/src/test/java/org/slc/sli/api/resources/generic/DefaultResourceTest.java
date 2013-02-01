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
package org.slc.sli.api.resources.generic;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.resources.v1.CustomEntityResource;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultResourceTest {

    @Autowired
    private DefaultResource defaultResource;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    @Autowired
    @Qualifier("defaultResourceService")
    private DefaultResourceService resourceService;

    private Resource resource = null;
    private java.net.URI requestURI;
    private UriInfo uriInfo;

    private static final String BASE_URI = "http://some.net/api/rest/v1/students";
    private static final String URI_KEY = "v1/students";

    @Before
    public void setup() throws Exception {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        resource = new Resource("v1", "students");
    }

    private void setupMocks(String uri) throws URISyntaxException {
        requestURI = new java.net.URI(uri);

        MultivaluedMap map = new MultivaluedMapImpl();
        uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(requestURI);
        when(uriInfo.getQueryParameters()).thenReturn(map);
        when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
    }

    @Test
    public void testGetAll() throws URISyntaxException {
        setupMocks(BASE_URI);
        Response response = defaultResource.getAll(uriInfo);

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPost() throws URISyntaxException {
        setupMocks(BASE_URI);
        Response response = defaultResource.post(createTestEntity(), uriInfo);

        assertEquals("Status code should be OK", Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull("Should not be null", parseIdFromLocation(response));
    }

    @Test
    public void testGetWithId() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = defaultResource.getWithId(id, uriInfo);
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Should match", id, body.get("id"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetInvalidId() throws URISyntaxException {
        setupMocks(BASE_URI + "/1234");

        defaultResource.getWithId("1234", uriInfo);
    }

    @Test
    public void testPut() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = defaultResource.put(id, createTestUpdateEntity(), uriInfo);

        assertEquals("Status code should be OK", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Response getResponse = defaultResource.getWithId(id, uriInfo);
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), getResponse.getStatus());
        assertEquals("Should match", id, body.get("id"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
        assertEquals("Should match", "Female", body.get("sex"));
    }

    @Test
    public void testPatch() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = defaultResource.patch(id, createTestPatchEntity(), uriInfo);

        assertEquals("Status code should be OK", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Response getResponse = defaultResource.getWithId(id, uriInfo);
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), getResponse.getStatus());
        assertEquals("Should match", id, body.get("id"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
        assertEquals("Should match", "Female", body.get("sex"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDelete() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = defaultResource.delete(id, uriInfo);
        assertEquals("Status code should be OK", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        defaultResource.getWithId(id, uriInfo);
    }

    @Test
    public void testGetCustomResource() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        CustomEntityResource resource = defaultResource.getCustomResource(id, uriInfo);
        assertNotNull("Should not be null", resource);
    }

    private EntityBody createTestUpdateEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Female");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private EntityBody createTestPatchEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Female");
        return entity;
    }

    private EntityBody createTestEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/([\\w-]+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }
}

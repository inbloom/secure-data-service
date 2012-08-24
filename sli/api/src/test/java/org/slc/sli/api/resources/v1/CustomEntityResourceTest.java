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


package org.slc.sli.api.resources.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
* Tests for CustomEntityResouce
*
* @author Ryan Farris <rfarris@wgen.net>
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CustomEntityResourceTest {

    @Autowired
    @InjectMocks
    CustomEntityResource resource;

    @Mock
    EntityDefinitionStore store;

    EntityService service;
    java.net.URI requestURI;
    UriInfo uriInfo;

    private static final String URI = "http://some.net/api/rest/v1/students/1234/custom";

    @Before
    public void init() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);

        EntityDefinition entityDef = Mockito.mock(EntityDefinition.class);
        service = Mockito.mock(EntityService.class);
        Mockito.when(entityDef.getService()).thenReturn(service);

        when(store.lookupByResourceName("students")).thenReturn(entityDef);

        requestURI = new java.net.URI(URI);

        MultivaluedMap map = new MultivaluedMapImpl();
        uriInfo = mock(UriInfo.class);
        when(uriInfo.getQueryParameters()).thenReturn(map);
        when(uriInfo.getRequestUri()).thenReturn(requestURI);
        when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("/");
            }
        });
    }

    @Test
    public void testRead() {
        Response res = resource.read(uriInfo, "TEST-ID");
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
        Mockito.verify(service).getCustom("TEST-ID");

        EntityBody mockBody = Mockito.mock(EntityBody.class);
        Mockito.when(service.getCustom("TEST-ID")).thenReturn(mockBody);
        res = resource.read(uriInfo, "TEST-ID");
        assertNotNull(res);
        assertEquals(Status.OK.getStatusCode(), res.getStatus());
        Mockito.verify(service, Mockito.atLeast(2)).getCustom("TEST-ID");
    }

    @Test
    public void testCreateOrUpdatePUT() {
        EntityBody test = new EntityBody();
        Response res = resource.createOrUpdatePut(uriInfo, "TEST-ID", test);
        assertNotNull(res);
        assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
        Mockito.verify(service).createOrUpdateCustom("TEST-ID", test);
    }

    @Test
    public void testCreateOrUpdatePOST() {
        EntityBody test = new EntityBody();

        Response res = resource.createOrUpdatePost(test, uriInfo, "TEST-ID");
        assertNotNull(res);
        assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
        assertNotNull(res.getMetadata().get("Location"));
        assertEquals(1, res.getMetadata().get("Location").size());
        assertEquals("/" + PathConstants.V1 + "/TEST-ID/custom", res.getMetadata().get("Location").get(0));

        Mockito.verify(service).createOrUpdateCustom("TEST-ID", test);

    }

    @Test
    public void testDelete() {
        Response res = resource.delete(uriInfo, "TEST-ID");
        assertNotNull(res);
        assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
        Mockito.verify(service).deleteCustom("TEST-ID");
    }

    @Test
    public void test404() {
        Response res = resource.read(uriInfo, "TEST-ID");
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());

        when(store.lookupByResourceName("students")).thenReturn(null);

        res = resource.createOrUpdatePut(uriInfo, "", null);
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());

        res = resource.delete(uriInfo, "TEST-ID");
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

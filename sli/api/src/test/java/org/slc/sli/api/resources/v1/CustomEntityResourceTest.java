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

package org.slc.sli.api.resources.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
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

    CustomEntityResource resource;
    EntityService service;

    @Autowired
    private ResourceHelper resourceHelper;


    @Before
    public void init() throws URISyntaxException {
        String entityId = "TEST-ID";
        EntityDefinition entityDef = Mockito.mock(EntityDefinition.class);
        service = Mockito.mock(EntityService.class);
        Mockito.when(entityDef.getService()).thenReturn(service);
        resource = new CustomEntityResource(entityId, entityDef, resourceHelper);
    }

    @Test
    public void testRead() {
        Response res = resource.read();
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
        Mockito.verify(service).getCustom("TEST-ID");

        EntityBody mockBody = Mockito.mock(EntityBody.class);
        Mockito.when(service.getCustom("TEST-ID")).thenReturn(mockBody);
        res = resource.read();
        assertNotNull(res);
        assertEquals(Status.OK.getStatusCode(), res.getStatus());
        Mockito.verify(service, Mockito.atLeast(2)).getCustom("TEST-ID");
    }

    @Test
    public void testCreateOrUpdatePUT() {
        EntityBody test = new EntityBody();
        Response res = resource.createOrUpdatePut(test);
        assertNotNull(res);
        assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
        Mockito.verify(service).createOrUpdateCustom("TEST-ID", test);
    }

    @Test
    public void testCreateOrUpdatePOST() {
        EntityBody test = new EntityBody();
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final UriBuilder uriBuilder = Mockito.mock(UriBuilder.class);
        Mockito.when(uriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
        final StringBuilder path = new StringBuilder();
        Mockito.when(uriBuilder.path(Mockito.anyString())).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                path.append("/").append(invocation.getArguments()[0]);
                return uriBuilder;
            }
        });

        Mockito.when(uriBuilder.build()).thenAnswer(new Answer<URI>() {

            @Override
            public URI answer(InvocationOnMock invocation) throws Throwable {
                URI uri = new URI(path.toString());
                return uri;
            }
        });

        Response res = resource.createOrUpdatePost(uriInfo, test);
        assertNotNull(res);
        assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
        assertNotNull(res.getMetadata().get("Location"));
        assertEquals(1, res.getMetadata().get("Location").size());
        assertEquals("/" + PathConstants.V1 + "/TEST-ID/custom", res.getMetadata().get("Location").get(0));

        Mockito.verify(service).createOrUpdateCustom("TEST-ID", test);

    }

    @Test
    public void testDelete() {
        Response res = resource.delete();
        assertNotNull(res);
        assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
        Mockito.verify(service).deleteCustom("TEST-ID");
    }

    @Test
    public void test404() {
        resource = new CustomEntityResource("TEST-ID", null, resourceHelper);
        Response res = resource.read();
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());

        res = resource.createOrUpdatePut(null);
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());

        res = resource.delete();
        assertNotNull(res);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}

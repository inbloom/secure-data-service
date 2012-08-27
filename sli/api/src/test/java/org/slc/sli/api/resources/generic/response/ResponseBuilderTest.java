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
package org.slc.sli.api.resources.generic.response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ResponseBuilderTest {

    @Autowired
    @Qualifier("getResponseBuilder")
    protected ResponseBuilder responseBuilder;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupprtedMethods;

    private static final String URI_KEY = "v1/students";
    private static final String URI = "http://some.net/api/rest/v1/students";

    private java.net.URI requestURI;
    private UriInfo uriInfo;

    @Before
    public void setup() throws URISyntaxException {
        Set<String> methods = new HashSet<String>();
        methods.add("GET");

        resourceSupprtedMethods.put(URI_KEY, methods);

        requestURI = new URI(URI);

        uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(requestURI);
    }

    @Test(expected = MethodNotAllowedException.class)
    public void testNotSupportedMethod() {
        responseBuilder.constructAndCheckResource(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.POST);
    }

    @Test
    public void testConstructAndCheckResource() {
        Resource resourceContainer = responseBuilder.constructAndCheckResource(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET);

        assertNotNull("Should not be null", resourceContainer);
        assertEquals("Should match", "v1", resourceContainer.getNamespace());
        assertEquals("Should match", "students", resourceContainer.getResourceType());
    }
}

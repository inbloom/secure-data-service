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

package org.slc.sli.api.resources.generic.response;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.QueryParseException;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GetResponseBuilderTest {

    @Autowired
    protected GetResponseBuilder getResponseBuilder;

    private UriInfo uriInfo;
    private URI requestURI;

    private static final String URI = "http://some.net/api/rest/v1/assessments";

    public void setupMocks(String segment) throws URISyntaxException {
        uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(requestURI);

        PathSegment segment0 = mock(PathSegment.class);
        PathSegment segment1 = mock(PathSegment.class);
        when(segment1.getPath()).thenReturn(segment);

        List<PathSegment> segments = Arrays.asList(segment0, segment1);

        when(uriInfo.getPathSegments()).thenReturn(segments);
    }

    @Ignore /* assessments can have filters now. */
    @Test(expected = QueryParseException.class)
    public void testValidatePublicResourceInvalidQuery() throws URISyntaxException {
        requestURI = new URI(URI + "?someField=someValue");

        setupMocks("assessments");

        getResponseBuilder.validatePublicResourceQuery(uriInfo);
    }

    @Ignore /* assessments can have filters now. */
    @Test(expected = QueryParseException.class)
    public void testValidatePublicResourceMultiInvalidQuery() throws URISyntaxException {
        requestURI = new URI(URI + "?limit=0&someField=someValue&includeFields=somefield");

        setupMocks("assessments");

        getResponseBuilder.validatePublicResourceQuery(uriInfo);
    }

    @Test
    public void testValidatePublicResourceQueryValidQuery() throws URISyntaxException {
        try {
            requestURI = new URI(URI + "?limit=0");

            setupMocks("assessments");

            getResponseBuilder.validatePublicResourceQuery(uriInfo);
        } catch (QueryParseException e) {
            fail("Should not throw an exception");
        }
    }

    @Test
    public void testValidatePublicResourceQueryMultiValidQuery() throws URISyntaxException {
        try {
            requestURI = new URI(URI + "?limit=0&includeFields=somefield");

            setupMocks("assessments");

            getResponseBuilder.validatePublicResourceQuery(uriInfo);
        } catch (QueryParseException e) {
            fail("Should not throw an exception");
        }
    }

    @Test
    public void testValidateNonPublicResourceFilter() throws URISyntaxException {
        try {
            requestURI = new URI(URI + "?someField=someValue");

            setupMocks("students");

            getResponseBuilder.validatePublicResourceQuery(uriInfo);
        } catch (QueryParseException e) {
            fail("Should not throw an exception");
        }
    }

    @Test
    public void testValidateNonPublicResource() throws URISyntaxException {
        try {
            requestURI = new URI(URI + "?limit=0");

            setupMocks("students");

            getResponseBuilder.validatePublicResourceQuery(uriInfo);
        } catch (QueryParseException e) {
            fail("Should not throw an exception");
        }
    }
}

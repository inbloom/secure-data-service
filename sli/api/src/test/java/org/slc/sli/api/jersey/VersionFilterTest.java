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
package org.slc.sli.api.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class VersionFilterTest {

    private static final String REQUESTED_PATH = "requestedPath";

    @Autowired
    private VersionFilter versionFilter; //class under test
    private ContainerRequest containerRequest;

    @Before
    public void setup() {
        containerRequest = mock(ContainerRequest.class);
    }

    @Test
    public void testNonRewrite() {
        PathSegment segment1 = mock(PathSegment.class);
        when(segment1.getPath()).thenReturn("v2");
        PathSegment segment2 = mock(PathSegment.class);
        when(segment2.getPath()).thenReturn("students");

        List<PathSegment> segments = Arrays.asList(segment1, segment2);
        when(containerRequest.getPathSegments()).thenReturn(segments);
        when(containerRequest.getProperties()).thenReturn(new HashMap<String, Object>());

        ContainerRequest request = versionFilter.filter(containerRequest);
        verify(containerRequest, never()).setUris((URI) any(), (URI) any());
        assertNull("Should be null", request.getProperties().get(REQUESTED_PATH));
    }

    @Test
    public void testRewriteNoQuery() throws URISyntaxException {
        UriBuilder builder = mock(UriBuilder.class);
        when(builder.path(anyString())).thenReturn(builder);

        URI uri = new URI("http://api/rest/v1/students");

        PathSegment segment1 = mock(PathSegment.class);
        when(segment1.getPath()).thenReturn("v1");
        PathSegment segment2 = mock(PathSegment.class);
        when(segment2.getPath()).thenReturn("students");

        List<PathSegment> segments = new ArrayList<PathSegment>();
        segments.add(segment1);
        segments.add(segment2);

        when(containerRequest.getPathSegments()).thenReturn(segments);
        when(containerRequest.getBaseUriBuilder()).thenReturn(builder);
        when(containerRequest.getRequestUri()).thenReturn(uri);
        when(containerRequest.getPath()).thenReturn("http://api/rest/v1/students");
        when(containerRequest.getProperties()).thenReturn(new HashMap<String, Object>());

        ContainerRequest request = versionFilter.filter(containerRequest);
        verify(containerRequest).setUris((URI) any(), (URI) any());
        verify(builder).build();
        verify(builder, times(1)).path("v1.2");
        verify(builder, times(1)).path("students");
        assertEquals("Should match", "http://api/rest/v1/students", request.getProperties().get(REQUESTED_PATH));
    }

    @Test
    public void testRewriteWithQuery() throws URISyntaxException {
        UriBuilder builder = mock(UriBuilder.class);
        when(builder.path(anyString())).thenReturn(builder);

        URI uri = new URI("http://api/rest/v1/students?foo=bar");

        PathSegment segment1 = mock(PathSegment.class);
        when(segment1.getPath()).thenReturn("v1");
        PathSegment segment2 = mock(PathSegment.class);
        when(segment2.getPath()).thenReturn("students");

        List<PathSegment> segments = new ArrayList<PathSegment>();
        segments.add(segment1);
        segments.add(segment2);

        when(containerRequest.getPathSegments()).thenReturn(segments);
        when(containerRequest.getBaseUriBuilder()).thenReturn(builder);
        when(containerRequest.getRequestUri()).thenReturn(uri);

        versionFilter.filter(containerRequest);
        verify(containerRequest).setUris((URI) any(), (URI) any());
        verify(builder).replaceQuery(anyString());
        verify(builder).build();
        verify(builder, times(1)).path("v1.2");
        verify(builder, times(1)).path("students");
    }



}

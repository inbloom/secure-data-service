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

package org.slc.sli.api.security.pdp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.spi.container.ContainerRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
public class EndpointMutatorTest {

    @Autowired
    private EndpointMutator endpointMutator; //class under test

    @Test
    public void testGetResourceVersionForSearch() {
        List<PathSegment> segments = getPathSegmentList("v1/search");
        MutatedContainer mutated = mock(MutatedContainer.class);
        when(mutated.getPath()).thenReturn("/search");
        assertEquals("Should match", "v1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.0/search");
        assertEquals("Should match", "v1.0", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.1/search");
        assertEquals("Should match", "v1.1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.3/search");
        assertEquals("Should match", "v1.3", endpointMutator.getResourceVersion(segments, mutated));
    }

    @Test
    public void testGetResourceVersion() {
        List<PathSegment> segments = getPathSegmentList("v1/students");
        MutatedContainer mutated = mock(MutatedContainer.class);
        when(mutated.getPath()).thenReturn("/studentSectionAssociations/1234/students");
        assertEquals("Should match", "v1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.0/students");
        assertEquals("Should match", "v1.0", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.1/students");
        assertEquals("Should match", "v1.1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.3/students");
        assertEquals("Should match", "v1.3", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.1/students");
        when(mutated.getPath()).thenReturn(null);
        assertEquals("Should match", "v1.1", endpointMutator.getResourceVersion(segments, mutated));
    }

    @Test
    public void testGetResourceVersionForPublicResources() {
        List<PathSegment> segments = getPathSegmentList("v1/assessments");

        MutatedContainer mutated = mock(MutatedContainer.class);
        when(mutated.getPath()).thenReturn("/search/assessments");

        assertEquals("Should match", "v1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.0/assessments");
        assertEquals("Should match", "v1.1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.1/assessments");
        assertEquals("Should match", "v1.1", endpointMutator.getResourceVersion(segments, mutated));

        segments = getPathSegmentList("v1.3/assessments");
        assertEquals("Should match", "v1.3", endpointMutator.getResourceVersion(segments, mutated));
    }
    
    @Test(expected = NotFoundException.class)
    public void testNoPathSegments() throws URISyntaxException {
        // Test /api/rest with no additional path segments.
        SLIPrincipal principle = mock(SLIPrincipal.class);
        ClientToken clientToken = mock(ClientToken.class);
        when(clientToken.getClientId()).thenReturn("theAppId");
        OAuth2Authentication auth = mock(OAuth2Authentication.class);
        when(auth.getPrincipal()).thenReturn(principle);
        when(auth.getClientAuthentication()).thenReturn(clientToken);

        ContainerRequest request = mock(ContainerRequest.class);
        List<PathSegment> segments = Collections.emptyList();
        when(request.getPathSegments()).thenReturn(segments);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestUri()).thenReturn(new URI("http://not.valid.inbloom.org"));
        
        endpointMutator.mutateURI(auth, request);
    }

    private List<PathSegment> getPathSegmentList(String path) {
        String[] paths = path.split("/");
        List<PathSegment> segments = new ArrayList<PathSegment>();


        for (String part : paths) {
            PathSegment segment = mock(PathSegment.class);
            when(segment.getPath()).thenReturn(part);
            segments.add(segment);
        }

        return segments;
    }

}

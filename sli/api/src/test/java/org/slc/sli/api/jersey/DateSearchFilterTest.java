/*
 * Copyright 2013 Shared Learning Collaborative, LLC
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.slc.sli.domain.QueryParseException;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * @author: sashton
 */
public class DateSearchFilterTest {
    
    @InjectMocks
    private DateSearchFilter filter = new DateSearchFilter();
    
    @Mock
    private ResourceEndPoint mockResourceEndPoint;
    
    private Set<String> disallowedEndpoints;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        
        disallowedEndpoints = new LinkedHashSet<String>();
        Mockito.when(mockResourceEndPoint.getDateRangeDisallowedEndPoints()).thenReturn(disallowedEndpoints);
        
    }
    
    @Test(expected = QueryParseException.class)
    public void shouldDisallowVersionOneZeroSearches() {
        ContainerRequest request = createRequest("v1.0/sections/1234/studentSectionAssociations",
                "schoolYears=2011-2012");
        
        filter.filter(request);
    }
    
    @Test
    public void shouldAllowVersionOneZeroNonSearchRequests() {
        ContainerRequest request = createRequest("v1.0/sections/1234/studentSectionAssociations", "");
        
        filter.filter(request);
    }
    
    @Test
    public void shonuldAllowVersionOneOneSearches() {
        ContainerRequest request = createRequest("v1.1/sections/1234/studentSectionAssociations",
                "schoolYears=2011-2012");
        
        filter.filter(request);
    }
    
    @Test(expected = QueryParseException.class)
    public void shouldDisallowExcludedUrisWithId() {
        
        String disallowedPath = "v1.1/sections/{id}/studentSectionAssociations";
        String requestPath = "v1.1/sections/1234,23234/studentSectionAssociations";
        
        disallowedEndpoints.add(disallowedPath);
        
        ContainerRequest request = createRequest(requestPath, "schoolYears=2011-2012");
        
        filter.filter(request);
    }

    @Test(expected = QueryParseException.class)
    public void shouldDisallowTwoPartUris() {

        String requestPath = "v1.1/sessions/1234567";

        ContainerRequest request = createRequest(requestPath, "schoolYears=2011-2012");

        filter.filter(request);
    }


    @Test
    public void shouldAllowTwoPartUrisWithoutDates() {

        String requestPath = "v1.1/sessions/1234567";

        ContainerRequest request = createRequest(requestPath, "");

        filter.filter(request);
    }

    @Test(expected = QueryParseException.class)
    public void shouldDisallowExcludedUrisWithoutId() {

        String disallowedPath = "v1.1/educationOrganizations";
        String requestPath = "v1.1/educationOrganizations";

        disallowedEndpoints.add(disallowedPath);

        ContainerRequest request = createRequest(requestPath, "schoolYears=2011-2012");

        filter.filter(request);
    }

    private ContainerRequest createRequest(String requestPath, String schoolYearsQuery) {
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        String[] pathParts = requestPath.split("/");
        List<PathSegment> segments = new ArrayList<PathSegment>();
        for (String pathPart : pathParts) {
            segments.add(segmentFor(pathPart));
        }
        
        MultivaluedMap queryParameters = new MultivaluedMapImpl();
        
        String[] schoolYearParts = schoolYearsQuery.split("=");
        if (schoolYearParts.length == 2) {
            queryParameters.add(schoolYearParts[0], schoolYearParts[1]);
        }
        
        Mockito.when(request.getQueryParameters()).thenReturn(queryParameters);
        Mockito.when(request.getPathSegments()).thenReturn(segments);
        Mockito.when(request.getPath()).thenReturn(requestPath);
        return request;
    }
    
    private PathSegment segmentFor(final String token) {
        PathSegment segment = new PathSegment() {
            @Override
            public String getPath() {
                return token;
            }
            
            @Override
            public MultivaluedMap<String, String> getMatrixParameters() {
                return null;
            }
        };
        
        return segment;
    }
    
}

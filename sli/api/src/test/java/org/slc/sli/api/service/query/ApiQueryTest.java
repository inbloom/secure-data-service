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


package org.slc.sli.api.service.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;

/**
 * Neutral Query Converter Test
 *
 * @author kmyers
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ApiQueryTest {

    private UriInfo uriInfo;
    private static final String URI_STRING = "http://localhost:8080/api/v1/sections";

    @Before
    public void setup() {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void testNonNullForNull() {
        // should always return a null, so callers don't have to worry about null checking
        assertTrue(new ApiQuery() != null);
    }

    @Test
    public void testToString() throws URISyntaxException {

        //String queryString = "selector=:(field1,link1:(*,field2:false))";

        //the selector gets parsed and stored in a map so there's no concept of ordering
        List<String> equivalentStrings = new ArrayList<String>();
        equivalentStrings.add("offset=0&limit=50");
        equivalentStrings.add("offset=0&limit=50");
        equivalentStrings.add("offset=0&limit=50");
        equivalentStrings.add("offset=0&limit=50");

        URI requestUri = new URI(URI_STRING);
        when(uriInfo.getRequestUri()).thenReturn(requestUri);
        ApiQuery apiQuery = new ApiQuery(uriInfo);

        assertTrue(equivalentStrings.contains(apiQuery.toString()));
    }

    @Test (expected = QueryParseException.class)
    public void testToStringWithSelectors() throws URISyntaxException {

        String queryString = "selector=:(field1,link1:(*,field2:false))";

        URI requestUri = new URI(URI_STRING + "?" + queryString);
        when(uriInfo.getRequestUri()).thenReturn(requestUri);
        ApiQuery apiQuery = new ApiQuery(uriInfo);
    }

    @Test
    public void testFullParse() {
        this.testFullParse("ascending", NeutralQuery.SortOrder.ascending);
        this.testFullParse("descending", NeutralQuery.SortOrder.descending);
    }

    private void testFullParse(String sortOrderString, NeutralQuery.SortOrder sortOrder) {
        try {
            int offset = 5;
            int limit = 4;
            String includeFields = "field1,field2";
            String excludeFields = "field3,field4";
            String sortBy = "field5";

            String queryString = "";
            queryString += (ParameterConstants.OFFSET + "=" + offset);
            queryString += ("&");
            queryString += (ParameterConstants.LIMIT + "=" + limit);
            queryString += ("&");
            queryString += (ParameterConstants.INCLUDE_FIELDS + "=" + includeFields);
            queryString += ("&");
            queryString += (ParameterConstants.EXCLUDE_FIELDS + "=" + excludeFields);
            queryString += ("&");
            queryString += (ParameterConstants.SORT_BY + "=" + sortBy);
            queryString += ("&");
            queryString += (ParameterConstants.SORT_ORDER + "=" + sortOrderString);
            queryString += ("&");
            queryString += ("testKey" + "=" + "testValue");

            URI requestUri = new URI(URI_STRING + "?" + queryString);

            when(uriInfo.getRequestUri()).thenReturn(requestUri);

            NeutralQuery neutralQuery = new ApiQuery(uriInfo);

            // test that the value was stored in the proper variable
            assertEquals(neutralQuery.getLimit(), limit);
            assertEquals(neutralQuery.getOffset(), offset);
            assertEquals(neutralQuery.getIncludeFieldString(), includeFields);
            assertEquals(neutralQuery.getExcludeFieldString(), excludeFields);
            assertEquals(neutralQuery.getSortBy(), sortBy);
            assertEquals(neutralQuery.getSortOrder(), sortOrder);
            assertEquals(neutralQuery.getCriteria().size(), 1);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }
    }

    @Test(expected = QueryParseException.class)
    public void testInvalidOffset() {
        try {
            String queryString = "offset=four";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        new ApiQuery(uriInfo);
    }

    @Test(expected = QueryParseException.class)
    public void testInvalidLimit() {
        try {
            String queryString = "limit=four";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        new ApiQuery(uriInfo);
    }

    @Test(expected = QueryParseException.class)
    public void testQueryStringMissingKey() {
        try {
            String queryString = "=4";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        new ApiQuery(uriInfo);
    }

    @Test(expected = QueryParseException.class)
    public void testQueryStringMissingOperator() {
        try {
            String queryString = "key4";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        new ApiQuery(uriInfo);
    }

    @Test(expected = QueryParseException.class)
    public void testQueryStringMissingValue() {
        try {
            String queryString = "key=";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        new ApiQuery(uriInfo);
    }

    @Test
    public void testDefaultLimit() {
        assertTrue(new ApiQuery().getLimit() == ApiQuery.API_QUERY_DEFAULT_LIMIT);
    }

    @Test
    public void testDefaultConstructor() {
        ApiQuery apiQuery = new ApiQuery();
        assertEquals(0, apiQuery.getCriteria().size());
        assertEquals(50, apiQuery.getLimit());
        assertEquals(0, apiQuery.getOffset());
        assertEquals(null, apiQuery.getIncludeFields());
        assertEquals(null, apiQuery.getExcludeFields());
        assertEquals(null, apiQuery.getSortBy());
        assertEquals(null, apiQuery.getSortOrder());
    }
}

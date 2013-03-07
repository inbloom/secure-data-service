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
import java.util.HashMap;
import java.util.Map;

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
public class UriInfoToApiQueryConverterTest {

    private static final UriInfoToApiQueryConverter QUERY_CONVERTER = new UriInfoToApiQueryConverter();


    private UriInfo uriInfo;
    private static final String URI_STRING = "http://localhost:8080/api/v1/sections";


    @Before
    public void setup() {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void testNonNullForNull() {
        // should always return a null, so callers don't have to worry about null checking
        assertTrue(QUERY_CONVERTER.convert(null) != null);
    }

    @Test
    public void testFullParse() {
        this.testFullParse("ascending", NeutralQuery.SortOrder.ascending);
        this.testFullParse("descending", NeutralQuery.SortOrder.descending);
    }

    @Test (expected = QueryParseException.class)
    public void testPreventionOfNegativeLimit() throws URISyntaxException {
        
        String queryString = "limit=-1";
        
        URI requestUri = new URI(URI_STRING + "?" + queryString);
        
        when(uriInfo.getRequestUri()).thenReturn(requestUri);
        QUERY_CONVERTER.convert(uriInfo);
    }
    
    @Test(expected = QueryParseException.class)
    public void testPreventionOfNegativeOffset() throws URISyntaxException {
        
        String queryString = "offset=-1";
        
        URI requestUri = new URI(URI_STRING + "?" + queryString);

        when(uriInfo.getRequestUri()).thenReturn(requestUri);
        QUERY_CONVERTER.convert(uriInfo);
    }

    @Test(expected = QueryParseException.class)
    public void testSelectorShouldFail() throws URISyntaxException {
        String queryString = "selector=:(students)";
        URI requestUri = new URI(URI_STRING + "?" + queryString);
        when(uriInfo.getRequestUri()).thenReturn(requestUri);

        QUERY_CONVERTER.convert(uriInfo);
    }

    private void testFullParse(String sortOrderString, NeutralQuery.SortOrder sortOrder) {
        try {
            int offset = 5;
            int limit = 4;
            String includeFields = "field1,field2";
            String excludeFields = "field3,field4";
            String sortBy = "field5";
            //String selectorString = ":(foo:(bar),foo2:(bar2:true),foo3:(bar3:false))";
            Map<String, Object> fooMap = new HashMap<String, Object>();
            fooMap.put("bar", true);
            Map<String, Object> foo2Map = new HashMap<String, Object>();
            foo2Map.put("bar2", true);
            Map<String, Object> foo3Map = new HashMap<String, Object>();
            foo3Map.put("bar3", false);
            Map<String, Object> selectorMap = new HashMap<String, Object>();
            selectorMap.put("foo", fooMap);
            selectorMap.put("foo2", foo2Map);
            selectorMap.put("foo3", foo3Map);

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
//            queryString += ("&");
//            queryString += (ParameterConstants.SELECTOR + "=" + selectorString);
            queryString += ("&");
            queryString += ("testKey" + "=" + "testValue");

            URI requestUri = new URI(URI_STRING + "?" + queryString);

            when(uriInfo.getRequestUri()).thenReturn(requestUri);

            ApiQuery apiQuery = QUERY_CONVERTER.convert(uriInfo);

            // test that the value was stored in the proper variable
            assertEquals(apiQuery.getLimit(), limit);
            assertEquals(apiQuery.getOffset(), offset);
            assertEquals(apiQuery.getIncludeFieldString(), includeFields);
            assertEquals(apiQuery.getExcludeFieldString(), excludeFields);
            assertEquals(apiQuery.getSortBy(), sortBy);
            assertEquals(apiQuery.getSortOrder(), sortOrder);
            assertEquals(apiQuery.getSelector(), null);
            assertEquals(apiQuery.getCriteria().size(), 1);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }
    }

    @Test(expected = QueryParseException.class)
    public void testInvalidSelector() {
        try {
            String queryString = "selector=true";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
            //when(uriInfo.getRequestUri().getQuery()).thenReturn(queryString);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        QUERY_CONVERTER.convert(uriInfo);
    }

    @Test(expected = QueryParseException.class)
    public void testInvalidOffset() {
        try {
            String queryString = "offset=four";
            URI requestUri = new URI(URI_STRING + "?" + queryString);
            when(uriInfo.getRequestUri()).thenReturn(requestUri);
            //when(uriInfo.getRequestUri().getQuery()).thenReturn(queryString);
        } catch (URISyntaxException urise) {
            assertTrue(false);
        }

        QUERY_CONVERTER.convert(uriInfo);
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

        QUERY_CONVERTER.convert(uriInfo);
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

        QUERY_CONVERTER.convert(uriInfo);
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

        QUERY_CONVERTER.convert(uriInfo);
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

        QUERY_CONVERTER.convert(uriInfo);
    }
}


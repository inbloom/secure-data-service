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


package org.slc.sli.api.resources.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;

/**
 * Utils for entity resource unit tests
 *
 * @author chung
 *
 */
public class ResourceTestUtil {
    public static Map<String, Object> createTestEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        entity.put(getResourceIdName(resourceName), 1234);
        return entity;
    }

    public static Map<String, Object> createTestUpdateEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 8);
        entity.put("field2", 2);
        entity.put(getResourceIdName(resourceName), 1234);
        return entity;
    }

    public static Map<String, Object> createTestSecondaryEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 5);
        entity.put("field2", 6);
        entity.put(getResourceIdName(resourceName), 5678);
        return entity;
    }

    public static Map<String, Object> createTestAssociationEntity(String resourceName, String ep1Name, String ep1Id, String ep2Name, String ep2Id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        entity.put(getResourceIdName(resourceName), 1234);
        entity.put(getResourceIdName(ep1Name), ep1Id);
        entity.put(getResourceIdName(ep2Name), ep2Id);
        return entity;
    }

    public static String getResourceIdName(String resourceName) {
        String resId = resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
        resId = resId.replace("Resource", "Id");
        return resId;
    }

    public static EntityBody assertions(Response response) {
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        Object responseEntityObj = null;

        EntityBody body = null;
        if (response.getEntity() instanceof EntityResponse) {
           EntityResponse resp = (EntityResponse) response.getEntity();
           responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
            return body;
        }

        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
            body = (EntityBody) responseEntityObj;
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity; actually have " + results.size(), results.size() == 1);
            body = results.get(0);
        } else {
            fail("Response entity not recognized: " + response);
            return body;
        }
        assertNotNull("Should return an entity", body);
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
        return body;
    }

    public static UriInfo buildMockUriInfo(final String queryString) throws Exception {
        UriInfo mock = mock(UriInfo.class);
        when(mock.getAbsolutePathBuilder()).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("absolute");
            }
        });
        when(mock.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
        when(mock.getRequestUriBuilder()).thenAnswer(new Answer<UriBuilder>() {

            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("request");
            }
        });

        when(mock.getQueryParameters(true)).thenReturn(new MultivaluedMapImpl());
        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(new Object[] {}));
        return mock;
    }

    public static String parseIdFromLocation(Response response) {
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

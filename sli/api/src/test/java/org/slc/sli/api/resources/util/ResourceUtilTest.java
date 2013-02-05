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

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EmbeddedLink;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the static methods provided by ResourceUtil.
 * 
 * 
 * @author kmyers
 * 
 */
public class ResourceUtilTest {
    
    /*
     * Test the getApiVersion method
     */
    @Test
    public void testGetApiVersionSolo() {
        
        // test the method itself
        String version = "foo";
        String uriPath = version + "/bar";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(uriPath);
        
        assertTrue(ResourceUtil.getApiVersion(uriInfo).equals(version));
    }

    /*
     * tests how the method acts when there's nothing but the api version
     */
    @Test
    public void testGetApiVersionNothingElse() {
        
        // just a version, nothing else
        String version = "foo";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(version);
        
        assertTrue(ResourceUtil.getApiVersion(uriInfo).equals(version));
    }

    /**
     * Test that getApiVersion works from other methods as well.
     */
    @Test
    public void testGetApiVersionLinked() {
        
        // confirm its working with others
        String version = "bar";
        String uriPath = version + "/foo";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(uriPath);
        when(uriInfo.getBaseUriBuilder()).thenReturn(this.createUriBuilder());
        EntityDefinition defn = mock(EntityDefinition.class);
        String resourceName = "baz";
        when(defn.getResourceName()).thenReturn(resourceName);
        String entityId = "entityId";
        PathConstants.TEMP_MAP.put(resourceName, resourceName); // add "baz" to the map just for
                                                                // this test
        EmbeddedLink embeddedLink = ResourceUtil.getSelfLinkForEntity(uriInfo, entityId, defn);
        String href = embeddedLink.getHref();
        
        assertTrue(href.contains(version + "/" + resourceName + "/" + entityId));
    }
    
    private UriBuilder createUriBuilder() {
        return new UriBuilder() {
            
            private List<Object> objects = new ArrayList<Object>();
            
            @Override
            public UriBuilder path(String path) throws IllegalArgumentException {
                this.objects.add(path);
                return this;
            }
            
            @Override
            public URI build(Object... values) throws IllegalArgumentException, UriBuilderException {
                StringBuilder uriStringBuilder = new StringBuilder();
                
                for (Object o : this.objects) {
                    uriStringBuilder.append("/");
                    uriStringBuilder.append(o.toString());
                }
                
                try {
                    return new URI(uriStringBuilder.toString());
                } catch (URISyntaxException e) {
                    return null;
                }
            }
            
            @Override
            public UriBuilder clone() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder uri(URI uri) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder scheme(String scheme) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder userInfo(String ui) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder host(String host) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder port(int port) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder replacePath(String path) {
                throw new UnsupportedOperationException();
            }
            
            @SuppressWarnings("rawtypes")
            @Override
            public UriBuilder path(Class resource) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @SuppressWarnings("rawtypes")
            @Override
            public UriBuilder path(Class resource, String method) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder path(Method method) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder segment(String... segments) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder replaceMatrix(String matrix) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder replaceMatrixParam(String name, Object... values) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder replaceQuery(String query) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder replaceQueryParam(String name, Object... values) throws IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public UriBuilder fragment(String fragment) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public URI buildFromMap(Map<String, ? extends Object> values) throws IllegalArgumentException,
                    UriBuilderException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public URI buildFromEncodedMap(Map<String, ? extends Object> values) throws IllegalArgumentException,
                    UriBuilderException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public URI buildFromEncoded(Object... values) throws IllegalArgumentException, UriBuilderException {
                throw new UnsupportedOperationException();
            }
            
        };
    }
    
}

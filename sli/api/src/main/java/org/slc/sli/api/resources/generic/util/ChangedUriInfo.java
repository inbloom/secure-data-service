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

package org.slc.sli.api.resources.generic.util;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.server.impl.application.WebApplicationContext;

import java.net.URI;
import java.util.List;

/**
 * Class for storing changes to requested uri.
 */
public class ChangedUriInfo implements UriInfo {
    
    private URI uri;
    private UriBuilder baseUriBuilder;

    public String getOriginalUri() {
        return originalUri;
    }

    private String originalUri;
    public static final String ORIGINAL_REQUEST_KEY = "original-request";

    public ChangedUriInfo(String uri, UriInfo uriInfo) {
        this.uri = URI.create(uri);
        if (uriInfo != null) {
            this.baseUriBuilder = uriInfo.getBaseUriBuilder();
            if (uriInfo instanceof WebApplicationContext) {
                originalUri = uriInfo.getBaseUri() + ((WebApplicationContext) uriInfo).getProperties().get(ORIGINAL_REQUEST_KEY).toString();
            }
        }
    }
    
    @Override
    public String getPath() {
        String uriPath = this.uri.getPath();
        if (uriPath != null) {
            String removeString = "/rest/";
            if (uriPath.startsWith(removeString)) {
                return uriPath.substring(removeString.length());
            }
            
            return uriPath;
        }

        return null;
    }
    
    @Override
    public String getPath(boolean decode) {
        
        // No op
        return null;
    }
    
    @Override
    public List<PathSegment> getPathSegments() {
        // No op
        return null;
    }
    
    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        // No op
        return null;
    }
    
    @Override
    public URI getRequestUri() {
        return this.uri;
    }
    
    @Override
    public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(uri);
    }
    
    @Override
    public URI getAbsolutePath() {
        // No op
        return null;
    }
    
    @Override
    public UriBuilder getAbsolutePathBuilder() {
        // No op
        return null;
    }
    
    @Override
    public URI getBaseUri() {
        // No op
        return null;
    }
    
    @Override
    public UriBuilder getBaseUriBuilder() {
        return this.baseUriBuilder.clone();
    }
    
    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        // No op
        return null;
    }
    
    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        // No op
        return null;
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return new MultivaluedMapImpl();
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        // No op
        return null;
    }
    
    @Override
    public List<String> getMatchedURIs() {
        // No op
        return null;
    }
    
    @Override
    public List<String> getMatchedURIs(boolean decode) {
        // No op
        return null;
    }
    
    @Override
    public List<Object> getMatchedResources() {
        // No op
        return null;
    }
    
}

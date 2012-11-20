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

package org.slc.sli.api.resources.generic.util;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Class for storing changes to requested uri.
 */
public class ChangedUriInfo implements UriInfo {
    
    private URI uri;
    private UriBuilder baseUriBuilder;
    
    public ChangedUriInfo(String uri, UriBuilder builder) {
        this.uri = URI.create(uri);
        this.baseUriBuilder = builder;
    }
    
    @Override
    public String getPath() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getPath(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<PathSegment> getPathSegments() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public UriBuilder getAbsolutePathBuilder() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public URI getBaseUri() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public UriBuilder getBaseUriBuilder() {
        return this.baseUriBuilder.clone();
    }
    
    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return new MultivaluedHashMap<String, String>();
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<String> getMatchedURIs() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<String> getMatchedURIs(boolean decode) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Object> getMatchedResources() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
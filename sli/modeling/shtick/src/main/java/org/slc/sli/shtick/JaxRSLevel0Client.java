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

package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * @author jstokes
 */
public final class JaxRSLevel0Client implements Level0Client {
    /**
     * Header name used for specifying the bearer token.
     */
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    private static final String HEADER_NAME_LOCATION = "Location";
    
    /**
     * Header value used for specifying the bearer token.
     */
    private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";
    
    @SuppressWarnings("unused")
    private static final String HEADER_VALUE_CONTENT_TYPE = "content-type";
    
    private final Client client;
    
    public JaxRSLevel0Client() {
        this.client = ClientFactory.newClient();
    }
    
    @Override
    public String get(final String token, final URI uri, final String mediaType) throws StatusCodeException {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }
        
        final Invocation.Builder builder = createBuilder(token, uri, mediaType);
        final Response response = builder.buildGet().invoke();
        
        checkResponse(response, Response.Status.OK);
        return response.readEntity(String.class);
    }
    
    @Override
    public void delete(final String token, final URI uri, final String mediaType) throws StatusCodeException {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }
        
        final Invocation.Builder builder = createBuilder(token, uri, mediaType);
        final Response response = builder.buildDelete().invoke();
        
        checkResponse(response, Response.Status.NO_CONTENT);
    }
    
    @Override
    public URI post(final String token, final String data, final URI uri, final String mediaType)
            throws StatusCodeException {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }
        
        final Invocation.Builder builder = createBuilder(token, uri, mediaType);
        final Response response = builder.buildPost(Entity.entity(data, mediaType)).invoke();
        
        checkResponse(response, Response.Status.CREATED);
        
        try {
            return new URI(response.getHeaders().getHeader(HEADER_NAME_LOCATION));
        } catch (final URISyntaxException e) {
            throw new ClientRuntimeException(e);
        }
    }
    
    @Override
    public void put(final String token, final String data, final URI uri, final String mediaType)
            throws StatusCodeException {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }
        
        final Invocation.Builder builder = createBuilder(token, uri, mediaType);
        final Response response = builder.buildPut(Entity.entity(data, mediaType)).invoke();
        
        checkResponse(response, Response.Status.NO_CONTENT);
    }

    @Override
    public void patch(String token, String data, URI uri, String mediaType) throws IOException, StatusCodeException {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }

        final Invocation.Builder builder = createBuilder(token, uri, mediaType);
        final Response response = builder.build("PATCH", Entity.entity(data, mediaType)).invoke();

        checkResponse(response, Response.Status.NO_CONTENT);
    }

    private Invocation.Builder createBuilder(final String token, final URI uri, final String mediaType) {
        if (token == null) {
            throw new IllegalArgumentException("token");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType");
        }
        final Invocation.Builder builder = client.target(uri).request(mediaType);
        builder.header(HEADER_NAME_AUTHORIZATION, String.format(HEADER_VALUE_AUTHORIZATION_FORMAT, token));
        return builder;
    }
    
    private RestResponse checkResponse(final Response response, final Response.Status expected)
            throws StatusCodeException {
        if (response == null) {
            throw new IllegalArgumentException("response");
        }
        if (expected == null) {
            throw new IllegalArgumentException("expected");
        }
        if (response.getStatus() != expected.getStatusCode()) {
            throw new StatusCodeException(response.getStatus(), response.readEntity(String.class));
        } else {
            return responseToRestResponse(response);
        }
    }
    
    private RestResponse responseToRestResponse(final Response response) {
        if (response == null) {
            throw new IllegalArgumentException("response");
        }
        final String body = response.readEntity(String.class);
        final int statusCode = response.getStatus();
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.putAll(response.getHeaders().asMap());
        return new RestResponse(body, statusCode, headers);
    }
}

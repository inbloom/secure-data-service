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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author jstokes
 */
public final class SpringLevel0Client implements Level0Client {
    
    /**
     * Header name used for specifying the bearer token.
     */
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * Header value used for specifying the bearer token.
     */
    private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";
    
    private final RestTemplate template;
    
    public SpringLevel0Client() {
        this.template = new RestTemplate();
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
        
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_NAME_AUTHORIZATION, String.format(HEADER_VALUE_AUTHORIZATION_FORMAT, token));
        
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            final ResponseEntity<String> response = template.exchange(uri.toString(), HttpMethod.GET, entity,
                    String.class);
            return response.getBody();
        } catch (final HttpClientErrorException e) {
            throw new StatusCodeException(e, e.getStatusCode().value());
        }
    }
    
    @Override
    public void delete(final String token, final URI uri, final String mediaType) throws StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public URI post(final String token, final String data, final URI uri, final String mediaType)
            throws StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public void put(final String token, final String data, final URI uri, final String mediaType)
            throws StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void patch(final String token, final String data, final URI uri, final String mediaType)
            throws StatusCodeException {
        throw new UnsupportedOperationException("TODO");
    }
}

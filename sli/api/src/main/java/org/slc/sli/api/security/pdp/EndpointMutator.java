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

package org.slc.sli.api.security.pdp;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.PathSegment;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;

import org.slc.sli.api.constants.ResourceNames;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.security.SLIPrincipal;

/**
 * Determines whether or not the requested endpoint should be mutated.
 *
 * @author dkornishev
 */
@Component
public class EndpointMutator {

    private static final String POST = "POST";
    private static final String REQUESTED_PATH = "requestedPath";
    private static final String VERSION_1_0 = "v1.0";
    private static final String VERSION_1_1 = "v1.1";

    @Resource
    private UriMutator uriMutator;

    /**
     * Mutates the URI based on who the user is (provided in Authentication object) and what they're
     * requesting (provided in Container Request object).
     *
     * @param auth
     *            OAuth2Authentication object (contains principal for user).
     * @param request
     *            Container Request (contains path and query parameters).
     */
    public void mutateURI(Authentication auth, ContainerRequest request) {

        /*
         * Don't mutate POSTs.
         */
        if (request.getMethod().equals(POST)) {
            return;
        }

        SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
        List<PathSegment> segments = sanitizePathSegments(request);
        String parameters = request.getRequestUri().getQuery();

        if (usingVersionedApi(segments)) {
            if (!request.getProperties().containsKey(REQUESTED_PATH)) {
                request.getProperties().put(REQUESTED_PATH, request.getPath());
            }
            MutatedContainer mutated = uriMutator.mutate(segments, parameters, user.getEntity());

            if (mutated != null && mutated.isModified()) {

                String version = getResourceVersion(segments, mutated);

                if (mutated.getHeaders() != null) {
                    InBoundHeaders headers = new InBoundHeaders();
                    headers.putAll(request.getRequestHeaders());
                    for (String key : mutated.getHeaders().keySet()) {
                        headers.putSingle(key, mutated.getHeaders().get(key));
                    }
                    request.setHeaders(headers);
                }

                if (mutated.getPath() != null) {
                    if (mutated.getQueryParameters() != null && !mutated.getQueryParameters().isEmpty()) {
                        info("URI Rewrite: {}?{} --> {}?{}", new Object[] { request.getPath(), parameters, mutated.getPath(),
                                mutated.getQueryParameters() });
                        request.setUris(request.getBaseUri(),
                                request.getBaseUriBuilder().path(version).path(mutated.getPath())
                                    .replaceQuery(mutated.getQueryParameters()).build());
                    } else {
                        info("URI Rewrite: {} --> {}", new Object[] { request.getPath(), mutated.getPath() });
                        request.setUris(request.getBaseUri(),
                                request.getBaseUriBuilder().path(version).path(mutated.getPath()).build());
                    }
                }
            }
        }
    }

    /**
     * Sanitizes the path segments currently contained in the request by removing empty segments.
     * This is required because a trailing slash causes an empty segment to exist, e.g.
     * /v1/students/ produces ["v1","students", ""].
     *
     * @param request
     *            Container Request to get path segments from.
     * @return Sane set of path segments.
     */
    protected List<PathSegment> sanitizePathSegments(ContainerRequest request) {
        List<PathSegment> segments = request.getPathSegments();
        for (Iterator<PathSegment> i = segments.iterator(); i.hasNext();) {
            if (i.next().getPath().isEmpty()) {
                i.remove();
            }
        }
        return segments;
    }

    /**
     * Validates that the list of path segments contains 'v1'.
     *
     * @param segments
     *            List of path segments.
     * @return True if using the v1 API, false otherwise.
     */
    protected boolean usingVersionedApi(List<PathSegment> segments) {
        return segments.get(0).getPath().startsWith(PathConstants.V);
    }

    /**
     * Returns the version for a given resource endpoint.
     * For the big six entities - all requests are rewritten to /search/resource,
     * since search is not part of 1.0 we need to use the next available search version.
     *
     * @param segments
     * @param mutated
     * @return
     */
    protected String getResourceVersion(List<PathSegment> segments, MutatedContainer mutated) {
        String version = segments.get(0).getPath();

        if (mutated.getPath() != null && mutated.getPath().startsWith("/" + ResourceNames.SEARCH + "/") && VERSION_1_0.equals(version)) {
            return VERSION_1_1;
        }

        return version;
    }

}

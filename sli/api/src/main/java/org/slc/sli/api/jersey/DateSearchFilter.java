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

package org.slc.sli.api.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.slc.sli.domain.QueryParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.PathSegment;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pre-request date range search filter.
 * Validates that the supplied request URI is allowed
 * to query by a schoolYear range
 *
 * @author sashton
 */
@Component
public class DateSearchFilter implements ContainerRequestFilter {

    /**
     * A pattern which can be used to replace the id(s) in a URI with just {id}
     * so that the uri can be looked up in a consistent manner
     */
    private final Pattern ID_REPLACEMENT_PATTERN = Pattern.compile("([^/]+/[^/]+/)[^/]+(/.*)");

    /**
     * A pattern which can be used to replace the id(s) in a URI with just {id}
     * so that the uri can be looked up in a consistent manner
     */
    private final Pattern TWO_PART_URI_PATTERN = Pattern.compile("^[^/]+/[^/]+/[^/]+$");

    @Autowired
    private ResourceEndPoint resourceEndPoint;

    @Override
    public ContainerRequest filter(ContainerRequest request) {

        // sets the startTime in the case that an exception is thrown
        // so that the PostProcessFilter can process correctly
        request.getProperties().put("startTime", System.currentTimeMillis());

        List<String> schoolYears = request.getQueryParameters().get(ParameterConstants.SCHOOL_YEARS);

        // only check conditions if query parameter exists
        if (schoolYears != null ){

            validateNotVersionOneZero(request);
            validateDateSearchUri(request);
            validateNonTwoPartUri(request);
        }


        return request;
    }

    private void validateNonTwoPartUri(ContainerRequest request) {
        String requestPath = request.getPath();

        Matcher m = TWO_PART_URI_PATTERN.matcher(requestPath);

        if (m.find()){
            throw new QueryParseException("Date range filtering not allowed", request.getPath());
        }
    }


    /**
     * Dissallows any date range searches for v1.0 URIs
     * @param request
     */
    private void validateNotVersionOneZero(ContainerRequest request) {
        List<PathSegment> segments = request.getPathSegments();
        if (segments.size() > 0) {
            String version = segments.get(0).getPath();
            if (PathConstants.V1_0.equals(version)) {

                List<String> schoolYears = request.getQueryParameters().get(ParameterConstants.SCHOOL_YEARS);
                if (schoolYears != null && schoolYears.size() > 0){
                    throw new QueryParseException("Date range filtering not allowed", request.getPath());
                }
            }
        }
    }

    /**
     * Verifies that if a date range is specified that the request URI
     * is allowed to be searched via date ranges
     * @param request
     */
    private void validateDateSearchUri(ContainerRequest request) {

        String requestPath = request.getPath();

        Matcher m = ID_REPLACEMENT_PATTERN.matcher(requestPath);

        if (m.matches()){
            // transform requestPath from "v1.x/foo/2344,3453,5345/bar" to "v1.x/foo/{id}/bar"
            requestPath = m.group(1) + PathConstants.ID_PLACEHOLDER + m.group(2);
        }

        if (this.resourceEndPoint.getDateRangeDisallowedEndPoints().contains(requestPath)) {

            List<String> schoolYears = request.getQueryParameters().get(ParameterConstants.SCHOOL_YEARS);
            if (schoolYears != null && schoolYears.size() > 0){
                throw new QueryParseException("Date range filtering not allowed", request.getPath());
            }
        }
    }

}

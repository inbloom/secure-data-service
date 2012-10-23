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

package org.slc.sli.api.security.context;

import org.slc.sli.api.security.SLIPrincipal;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * ContextValidator
 * Determines if the principal has context to a resource.
 * Verifies the requested endpoint is accessible by the principal
 */
@Component
public class ContextValidator {

    public void validateContextToUri(ContainerRequest request, SLIPrincipal principal) {
        validateUserHasAccessToEndpoint(request, principal);
        validateUserHasContextToRequestedEntities(request, principal);
    }

    private void validateUserHasContextToRequestedEntities(ContainerRequest request, SLIPrincipal principal) {
        //TODO replace stub
    }

    private void validateUserHasAccessToEndpoint(ContainerRequest request, SLIPrincipal principal) {
        //TODO replace stub
        // make data driven from v1_resource
        // each resource will have an accessibleBy key with an array value, listing each of the user types that can accesses the resource
        // example accessibleBy: ['teacher', 'staff']

    }


}
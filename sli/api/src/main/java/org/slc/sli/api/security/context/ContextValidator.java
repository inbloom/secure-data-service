package org.slc.sli.api.security.context;

import com.sun.jersey.spi.container.ContainerRequest;
import org.slc.sli.api.security.SLIPrincipal;
import org.springframework.stereotype.Component;

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
package org.slc.sli.api.resources.generic.response;

import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Builds a default response. Used for other operations than GET.
 *
 * @author srupasinghe
 */

@Scope("request")
@Component
public class DefaultResponseBuilder extends ResponseBuilder {

    public Response build(final UriInfo uriInfo, final ResourceTemplate template,
                          final ResourceMethod method, final GenericResource.ResourceLogic logic) {

        //get the resource container
        Resource resource = constructAndCheckResource(uriInfo, template, method);

        return logic.run(resource);
    }
}

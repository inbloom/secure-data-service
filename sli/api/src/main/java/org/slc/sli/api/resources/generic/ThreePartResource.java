package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author jstokes
 */
@Component
@Scope("request")
public class ThreePartResource extends GenericResource {

    @Autowired
    private ResourceService resourceService;

    @GET
    public Response get(@Context final UriInfo uriInfo,
                        @PathParam("id") final String id) {
        return handle(uriInfo, ResourceTemplate.THREE_PART, ResourceMethod.GET, new ResourceLogic() {
            @Override
            public Response run(final String resourceName) {
                final String base = resourceHelper.getBaseName(uriInfo, ResourceTemplate.THREE_PART);
                final List<EntityBody> results = resourceService.getEntities(base, id, resourceName, uriInfo.getRequestUri());
                return Response.ok(results).build();
            }
        });
    }
}

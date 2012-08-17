package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author jstokes
 */
@Component
@Scope("request")
public class FourPartResource extends GenericResource {

    @Autowired
    private ResourceService resourceService;

    @GET
    public Response get(@Context final UriInfo uriInfo) {
        return handle(uriInfo, ResourceTemplate.FOUR_PART, ResourceMethod.GET, new ResourceLogic() {
            @Override
            public Response run(String resourceName) {
                final List<EntityBody> results = resourceService.getEntities(resourceName, uriInfo);
                return Response.ok(results).build();
            }
        });
    }
}

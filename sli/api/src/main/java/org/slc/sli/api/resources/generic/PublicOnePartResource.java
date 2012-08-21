package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Resource for handling public one part URIs
 *
 * @author srupasinghe
 */

@Component
@Scope("request")
public class PublicOnePartResource extends OnePartResource {

    @Override
    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        return handleGet(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public List<EntityBody> run(Resource resource) {

                return resourceService.getEntities(resource, uriInfo.getRequestUri(), true);
            }
        });
    }

}

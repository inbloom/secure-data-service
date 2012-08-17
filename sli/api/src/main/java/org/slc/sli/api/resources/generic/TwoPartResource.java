package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resource for handling two part URIs
 *
 * @author srupasinghe
 */
@Component
@Scope("request")
public class TwoPartResource extends GenericResource {

    @Autowired
    private ResourceService resourceService;


    @GET
    public Response getWithId(@PathParam("id") final String id,
                              @Context final UriInfo uriInfo) {

        return handle(uriInfo, ResourceTemplate.TWO_PART, ResourceMethod.GET, new GenericResource.ResourceLogic() {
            @Override
            public Response run(String resourceName) {
                List<EntityBody> results = resourceService.getEntitiesByIds(resourceName, id, uriInfo);

                return Response.ok(results).build();
            }
        });
    }

    @PUT
    public Response put(@PathParam("id") final String id,
                        final EntityBody entityBody,
                        @Context final UriInfo uriInfo) {

        return Response.ok("put").build();

    }

    @DELETE
    public Response delete(@PathParam("id") final String id,
                           @Context final UriInfo uriInfo) {

        return Response.ok("delete").build();
    }

}

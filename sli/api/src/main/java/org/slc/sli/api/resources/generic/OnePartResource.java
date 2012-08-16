package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/8/12
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("request")
public class OnePartResource extends GenericResource {

    @Autowired
    private ResourceService resourceService;

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        return handle(uriInfo, ResourceTemplate.ONE_PART_FULL, ResourceMethod.GET, new ResourceLogic() {
            @Override
            public Response run(String resourceName) {
                List<EntityBody> results = resourceService.getEntities(resourceName);

                return Response.ok(results).build();
            }
        });
    }

    @POST
    public Response post(final EntityBody entityBody,
                         @Context final UriInfo uriInfo) {
        return handle(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.POST, new ResourceLogic() {
            @Override
            public Response run(String resourceName) {
                String id = resourceService.postEntity(resourceName, entityBody);

                return Response.ok(id).build();
            }
        });
    }




}

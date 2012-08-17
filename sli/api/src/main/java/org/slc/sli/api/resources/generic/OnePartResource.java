package org.slc.sli.api.resources.generic;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


/**
 * Resource for handling one part URIs
 *
 * @author srupasinghe
 */

@Component
@Scope("request")
public class OnePartResource extends GenericResource {

    @Autowired
    private ResourceService resourceService;

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        return handle(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET, new ResourceLogic() {
            @Override
            public Response run(String resourceName) {
                List<EntityBody> results = resourceService.getEntities(resourceName, uriInfo);

                long pagingHeaderTotalCount = resourceService.getEntityCount(resourceName, uriInfo);
                return addPagingHeaders(Response.ok(new EntityResponse(resourceService.getEntityType(resourceName), results)),
                        pagingHeaderTotalCount, uriInfo).build();
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

                String uri = ResourceUtil.getURI(uriInfo, PathConstants.V1,
                        resourceName, id).toString();
                return Response.status(Response.Status.CREATED).header("Location", uri).build();
            }
        });
    }
}

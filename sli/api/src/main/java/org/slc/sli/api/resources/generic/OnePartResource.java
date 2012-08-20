package org.slc.sli.api.resources.generic;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.representation.Resource;
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
import javax.ws.rs.core.MultivaluedMap;
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

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        return handleGet(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public List<EntityBody> run(Resource resource) {

                return resourceService.getEntities(resource, uriInfo.getRequestUri(),
                        uriInfo.getQueryParameters(), false);
            }
        });
    }

    @POST
    public Response post(final EntityBody entityBody,
                         @Context final UriInfo uriInfo) {
        return handle(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.POST, new ResourceLogic() {
            @Override
            public Response run(Resource resource) {
                final String id = resourceService.postEntity(resource, entityBody);

                final String uri = ResourceUtil.getURI(uriInfo, PathConstants.V1,
                        resource.getResourceType(), id).toString();

                return Response.status(Response.Status.CREATED).header("Location", uri).build();
            }
        });
    }
}

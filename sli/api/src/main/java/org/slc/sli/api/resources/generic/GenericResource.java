package org.slc.sli.api.resources.generic;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.CustomEntityResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.List;

/**
 * Dynamic resource to handle one and two part resources
 *
 * @author srupasinghe
 */

@Component
@Scope("request")
public class GenericResource {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        final String resource = getResourceName(uriInfo, ResourceTemplate.ONE_PART);
        List<EntityBody> results = resourceService.getEntities(resource);

        return Response.ok(results).build();
    }

    @GET
    @Path("{id}")
    public Response getWithId(@PathParam("id") final String id,
                              @Context final UriInfo uriInfo) {
        final String resource = getResourceName(uriInfo, ResourceTemplate.TWO_PART);
        EntityBody result = resourceService.getEntity(resource, id);

        return Response.ok(result).build();
    }

    @Path("{id}/" + PathConstants.CUSTOM_ENTITIES)
    public CustomEntityResource getCustom(@PathParam("id") final String id,
                              @Context final UriInfo uriInfo) {
        final String resource = getResourceName(uriInfo, ResourceTemplate.CUSTOM);

        EntityDefinition entityDef = resourceService.getEntityDefinition(resource);
        return new CustomEntityResource(id, entityDef);
    }

    @POST
    public Response post(final EntityBody entityBody,
                         @Context final UriInfo uriInfo) {
        final String resource = getResourceName(uriInfo, ResourceTemplate.ONE_PART);
        String id = resourceService.postEntity(resource, entityBody);

        return Response.ok(id).build();
    }

    @PUT
    @Path("{id}")
    public Response put(@PathParam("id") final String id,
                        final EntityBody entityBody,
                        @Context final UriInfo uriInfo) {

        return null;

    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String id,
                           @Context final UriInfo uriInfo) {
        return null;
    }

    protected String getResourceName(UriInfo uriInfo, ResourceTemplate template) {
        return resourceHelper.grabResource(uriInfo.getRequestUri().toString(), template);
    }

}

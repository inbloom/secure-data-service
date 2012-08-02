package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

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
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/1/12
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("request")
public class GenericResource {

    @Autowired
    private ResourceService resourceService;

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        String resource = getResourceName(uriInfo, "/generic/{resource}");
        List<EntityBody> results = resourceService.getEntities(resource);

        return Response.ok(results).build();
    }

    @GET
    @Path("{id}")
    public Response getWithId(@PathParam("id") final String id,
                              @Context final UriInfo uriInfo) {
        String resource = getResourceName(uriInfo, "/generic/{resource}/{id}");
        EntityBody result = resourceService.getEntity(resource, id);

        return Response.ok(result).build();
    }

    @POST
    public Response post(final EntityBody newEntityBody,
                         @Context final UriInfo uriInfo) {
        return null;
    }

    @PUT
    @Path("{id}")
    public Response put(@PathParam("id") final String id,
                        final EntityBody newEntityBody,
                        @Context final UriInfo uriInfo) {

        return null;

    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String id,
                           @Context final UriInfo uriInfo) {
        return null;
    }

    protected String getResourceName(UriInfo uriInfo, String templateString) {
        Map<String, String> stringList;
        UriTemplate template = new UriTemplate(templateString);

        stringList = template.match(uriInfo.getRequestUri().toString());

        return stringList.get("resource");
    }

}

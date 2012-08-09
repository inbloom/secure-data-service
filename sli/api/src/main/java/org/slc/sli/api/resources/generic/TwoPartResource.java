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
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/8/12
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("request")
public class TwoPartResource {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupprtedMethods;

    @GET
    public Response getWithId(@PathParam("id") final String id,
                              @Context final UriInfo uriInfo) {
        final String resource = getResourceName(uriInfo, ResourceTemplate.TWO_PART);
        EntityBody result = resourceService.getEntity(resource, id);

        return Response.ok(result).build();
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

    protected String getResourceName(UriInfo uriInfo, ResourceTemplate template) {
        return resourceHelper.grabResource(uriInfo.getRequestUri().toString(), template);
    }

}

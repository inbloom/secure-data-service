package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
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
public class OnePartResource {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    @GET
    public Response getAll(@Context final UriInfo uriInfo) {
        final String key = getResourceName(uriInfo, ResourceTemplate.ONE_PART_FULL);

        Set<String> values = resourceSupportedMethods.get(key);
        if (!values.contains("GET")) {
            throw new UnsupportedOperationException("GET not supported");
        }

        final String resource = getResourceName(uriInfo, ResourceTemplate.ONE_PART);
        List<EntityBody> results = resourceService.getEntities(resource);

        return Response.ok(results).build();
    }

    @POST
    public Response post(final EntityBody entityBody,
                         @Context final UriInfo uriInfo) {
        final String resource = getResourceName(uriInfo, ResourceTemplate.ONE_PART);
        String id = resourceService.postEntity(resource, entityBody);

        return Response.ok(id).build();
    }

    protected String getResourceName(UriInfo uriInfo, ResourceTemplate template) {
        return resourceHelper.grabResource(uriInfo.getRequestUri().toString(), template);
    }
}

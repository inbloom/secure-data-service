package org.slc.sli.api.resources.generic.custom;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.resources.generic.DefaultResource;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.aggregation.CalculatedDataListingResource;
import org.slc.sli.domain.CalculatedData;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * Resource for handling public aggregate listings
 *
 * @author srupasinghe
 * @author jstokes
 */

@Component
@Scope("request")
public class PublicAggregateListableResource extends DefaultResource {

    @Override
    @GET
    public Response getAll(@Context final UriInfo uriInfo) {

        return getAllResponseBuilder.build(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {

                return resourceService.getEntities(resource, uriInfo.getRequestUri(), true);
            }
        });

    }

    @Path("{id}/" + PathConstants.AGGREGATIONS)
    public CalculatedDataListingResource<Map<String, Integer>> getAggregateResource(@PathParam("id") String id, @Context UriInfo uriInfo) {
        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.AGGREGATES);

        CalculatedData<Map<String, Integer>> data = resourceService.getAggregateData(resource, id);

        return new CalculatedDataListingResource<Map<String, Integer>>(data);
    }
}

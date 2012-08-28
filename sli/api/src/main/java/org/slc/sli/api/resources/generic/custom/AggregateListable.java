package org.slc.sli.api.resources.generic.custom;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.resources.v1.aggregation.CalculatedDataListingResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * Jersey resource for aggregate discovery
 *
 * @author pghosh@wgen.net
 *
 */

@Component
@Scope("request")
public interface AggregateListable {
    @Path("{id}/" + PathConstants.AGGREGATIONS)
    public CalculatedDataListingResource<Map<String, Integer>> getAggregateResource(@PathParam("id") String id, @Context UriInfo uriInfo);
}

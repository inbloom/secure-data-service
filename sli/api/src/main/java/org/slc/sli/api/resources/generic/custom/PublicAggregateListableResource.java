/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.resources.generic.custom;

import org.slc.sli.api.resources.generic.AggregateListable;
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
public class PublicAggregateListableResource extends DefaultResource implements AggregateListable {

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

    public CalculatedDataListingResource<Map<String, Integer>> getAggregateResource(final String id, final UriInfo uriInfo) {
        final Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.AGGREGATES);

        CalculatedData<Map<String, Integer>> data = resourceService.getAggregateData(resource, id);

        return new CalculatedDataListingResource<Map<String, Integer>>(data);
    }
}

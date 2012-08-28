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
package org.slc.sli.api.resources.v1.aggregation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.CalculatedDatum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Resource for displaying aggregate listings
 *
 * @author nbrown
 * @param <T> type of data to return
 *
 */
@Component
@Scope("request")
public class CalculatedDataListingResource<T> {

    private final CalculatedData<T> data;

    public CalculatedDataListingResource(CalculatedData<T> data) {
        this.data = data;
    }

    /**
     * Get the aggregates for a particular entity
     *
     * @param type filter by the aggregate type (assessment, attendance, etc)
     * @param window the window to look in (average, most recent, highest ever, etc)
     * @param methodology the part of the aggregated entity to pull in (ScaleScore, Percentile, etc)
     * @param name the name of the thing being aggregated (SAT, ACT, etc)
     * @return
     */
    @GET
    @Path("/")
    public Response getCalculatedValues(@QueryParam("type") final String type,
                                        @QueryParam("window") final String window,
                                        @QueryParam("method") final String methodology,
                                        @QueryParam("name") final String name) {

        final List<CalculatedDatum<T>> aggs = data.getCalculatedValues(type, window, methodology, name);
        return Response.ok(aggs).build();
    }

}

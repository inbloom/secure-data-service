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
package org.slc.sli.api.resources.aggregation;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slc.sli.domain.AggregateData;
import org.slc.sli.domain.AggregateDatum;
import org.slc.sli.domain.Entity;

/**
 * Resource for displaying aggregate listings
 *
 * @author nbrown
 *
 */
public class AggregateListingResource {

    private final AggregateData data;

    public AggregateListingResource(Entity entity) {
        super();
        this.data = entity.getAggregates();
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
    public Response getAggregates(@QueryParam("type") String type, @QueryParam("window") String window,
            @QueryParam("method") String methodology, @QueryParam("name") String name) {
        List<AggregateDatum> aggs = new ArrayList<AggregateDatum>();
        for (AggregateDatum datum : data.getAggregates()) {
            if ((type == null || type.equals(datum.getType())) && (window == null || window.equals(datum.getWindow()))
                    && (methodology == null || methodology.equals(datum.getMethodology()))
                    && (name == null || name.equals(datum.getName()))) {
                aggs.add(datum);
            }
        }
        return Response.ok(aggs).build();
    }
}

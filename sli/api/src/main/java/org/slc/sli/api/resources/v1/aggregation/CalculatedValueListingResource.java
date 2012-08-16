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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.CalculatedDatum;

/**
 * Resource for displaying aggregate listings
 *
 * @author nbrown
 *
 */
public class CalculatedValueListingResource {

    private final CalculatedData<String> data;

    public CalculatedValueListingResource(CalculatedData<String> data) {
        super();
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
    public Response getCalculatedValues(@QueryParam("type") String type, @QueryParam("window") String window,
            @QueryParam("method") String methodology, @QueryParam("name") String name) {
        List<CalculatedDatum<String>> aggs = data.getCalculatedValues(type, window, methodology, name);
        return Response.ok(aggs).build();
    }

}

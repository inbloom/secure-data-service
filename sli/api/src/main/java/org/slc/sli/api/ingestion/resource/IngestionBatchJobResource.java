/*
 * Copyright 2012-2014 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.ingestion.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.slc.sli.api.ingestion.service.IngestionBatchJobService;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A controller for getting ingestion job data from a database.
 * 
 * This endpoint supports pagination as well as sortBy and sortOrder. It requires the INGESTION_LOG_VIEW
 * to be able to access this data. It also allows for finding a job by Id using the format
 *  /api/rest/ingestionJobs/{id}. All data returned from this controller will be in JSON format.
 * 
 * @author mbrush
 */

@Component
@Scope("request") 
@Path(ResourceNames.INGESTION_JOBS)
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class IngestionBatchJobResource {

	@Autowired
	IngestionBatchJobService ingestionBatchJobService;

    @GET
    @RightsAllowed({ Right.INGESTION_LOG_VIEW })
	public @ResponseBody List<IngestionBatchJob> find(@Context HttpServletRequest request) {
    	String strLimit = request.getParameter("limit");
    	String strOffset = request.getParameter("offset");
    	String sortBy = request.getParameter("sortBy");
    	String strSortOrder = request.getParameter("sortOrder");

    	Integer limit = null;
    	Integer offset = null;
    	Order sortOrder = null;

    	if (null != strLimit && !strLimit.isEmpty()) {
    		limit = Integer.parseInt(strLimit);
    	}

    	if (null != strOffset && !strOffset.isEmpty()) {
    		offset = Integer.parseInt(strOffset);
    	}

    	if (null != strSortOrder && !strSortOrder.isEmpty()) {
    		if (strSortOrder.equals("ASC")) {
    			sortOrder = Order.ASCENDING;
    		} else if (strSortOrder.equals("DESC")) {
    			sortOrder = Order.DESCENDING;
    		}
    	}

    	return ingestionBatchJobService.find(limit, offset, sortBy, sortOrder);
	}

    @GET
    @Path("{id}")
    @RightsAllowed({ Right.INGESTION_LOG_VIEW })
    public @ResponseBody IngestionBatchJob findOne(@PathParam("id") String id) {
    	return ingestionBatchJobService.findOne(id);
    }
}
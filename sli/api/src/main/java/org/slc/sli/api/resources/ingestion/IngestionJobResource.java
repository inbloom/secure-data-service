/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.api.resources.ingestion;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.UnversionedResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.util.PATCH;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides read access to Ingestion Jobs through the /ingestionJobs path.
 *
 * @author rcook
 */
/*
 * This was copied from SecurityEventResource originally, but doesn't behave like it.
 * Ingestion Job data (i.e., the data about ingestion jobs) is put in a different table
 * from everything else, and therefore does not follow the same data storage patterns
 * as other things in the application.  At this writing, data not stored by tenant is
 * assumed by the standard data access classes to reside in the database named "sli",
 * and ingestion job data does not.  Therefore this class uses data access code that 
 * is separate from that used by the rest of the application.
 */
@Component
@Scope("request") 
@Path(ResourceNames.INGESTION_JOBS)
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class IngestionJobResource extends UnversionedResource {

    private static final Logger LOG = LoggerFactory.getLogger(IngestionJobResource.class);
    private final int INGESTION_JOB_SEARCH_LIMIT = 50;
    
    public static final String RESOURCE_NAME = ResourceNames.INGESTION_JOBS;
    @javax.annotation.Resource
    private BatchJobMongoDA batchJobMongoDA;   public void setBatchJobMongoDA(BatchJobMongoDA da) { batchJobMongoDA = da; }

    @Override
    @GET
    @RightsAllowed({ Right.INGESTION_LOG_VIEW })
    public Response getAll(@Context final UriInfo uriInfo) {
    	LOG.info("Request for Ingestion Job data");

    	// no security logging at this time.
    	//    	logSecurityEvent("Request for Ingestion Job data");
    	
    	// we currently have no need to allow paging through a large set of data, or of obtaining
    	// data older than 50 jobs back.  When we need either of these, put the logic for it here.
    	// Paging logic was built into the classes that assume we are dealing with inBloom entities,
    	// so we'll have to duplicate the logic or do without.
    	
    	List<NewBatchJob> batchJobList = batchJobMongoDA.findLatestBatchJobs(INGESTION_JOB_SEARCH_LIMIT);
    	ResponseBuilder builder = Response.ok(batchJobList);
    	return builder.build();
    	

//        return getAllResponseBuilder.build(uriInfo, getOnePartTemplate(), ResourceMethod.GET, new GetResourceLogic() {
//            @Override
//            public ServiceResponse run(Resource resource) {
////            	ServiceResponse serviceResponse = new ServiceResponse(batchJobList, batchJobList.size());
//            	ServiceResponse serviceResponse = new ServiceResponse(null, 0L);
//                return serviceResponse;
//            }
//        });

    }

    @Override
    @GET
    @Path("{id}")
    @RightsAllowed({ Right.INGESTION_LOG_VIEW})
    public Response getWithId(@PathParam("id") final String idList, @Context final UriInfo uriInfo) {
//        return getResponseBuilder.build(uriInfo, getTwoPartTemplate(), ResourceMethod.GET, new GetResourceLogic() {
//            @Override
//            public ServiceResponse run(Resource resource) {
//                return resourceService.getEntitiesByIds(resource, idList, uriInfo.getRequestUri());
//            }
//        });
    	throw new APIAccessDeniedException("No ID allowed on ingestion job query");
    }

//    @Override
//    @RightsAllowed({ Right.INGESTION_LOG_VIEW})
//    public CustomEntityResource getCustomResource(final String id, final UriInfo uriInfo) {
//        return new CustomEntityResource(id, resourceHelper.getEntityDefinition(RESOURCE_NAME), resourceHelper);
//    }

    @Override
    @POST
    @RightsAllowed({ Right.INGESTION_LOG_VIEW})
    public Response post(final EntityBody entityBody, @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP POST is forbidden for ingestion jobs");
    }

    @PUT
    @Path("{id}")
    @RightsAllowed({ Right.INGESTION_LOG_VIEW})
    public Response put(@PathParam("id") final String id, final EntityBody entityBody,
            @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP PUT is forbidden for ingestion jobs");
    }

    @Override
    @DELETE
    @Path("{id}")
    @RightsAllowed({ Right.INGESTION_LOG_VIEW})
    public Response delete(@PathParam("id") final String id, @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP DELETE is forbidden for ingestion jobs");
    }

    @Override
    @PATCH
    @Path("{id}")
    @RightsAllowed({ Right.INGESTION_LOG_VIEW})
    public Response patch(@PathParam("id") final String id,
                          final EntityBody entityBody,
                          @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP PATCH is forbidden for ingestion jobs");
    }

    /**
     * Make sure that HTTP OPTIONS requests are forbidden.
     */
    @OPTIONS
    @RightsAllowed({Right.INGESTION_LOG_VIEW})
    public Response options() {
        throw new APIAccessDeniedException("HTTP OPTIONS is forbidden");
    }

}

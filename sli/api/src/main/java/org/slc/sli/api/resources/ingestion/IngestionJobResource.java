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
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.UnversionedResource;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.v1.CustomEntityResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.util.PATCH;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.model.da.BatchJobMongoDA;

/**
 * Provides read access to Ingestion Jobs through the /securityEvent path.
 * For more information, see the schema for the $$securityEvent$$ entity.
 *
 * Caution must be exercised so that nothing from any parent classes of this class exposes
 * anything unwanted.
 *
 * This has been changed to use a custom SecurityEventService rather than BasicService, with
 * most of the security checking moved to that service.
 *
 * @author ldalgado
 * @author Andrew D. Ball
 * @author rcook
 */
@Component
@Scope("request") // TODO Why does this need request scope?
@Path(ResourceNames.INGESTION_JOBS)
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class IngestionJobResource extends UnversionedResource {

    public static final String RESOURCE_NAME = ResourceNames.INGESTION_JOBS;
    private BatchJobMongoDA ingestionJobDA = new BatchJobMongoDA();

    @Override
    @GET
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW })
    public Response getAll(@Context final UriInfo uriInfo) {

        return getAllResponseBuilder.build(uriInfo, getOnePartTemplate(), ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {
            	List<NewBatchJob> batchJobList = ingestionJobDA.findLatestBatchJobs(10);
//            	ServiceResponse serviceResponse = new ServiceResponse(batchJobList, batchJobList.size());
            	ServiceResponse serviceResponse = new ServiceResponse(null, 0L);
                return serviceResponse;
            }
        });

    }

    @Override
    @GET
    @Path("{id}")
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public Response getWithId(@PathParam("id") final String idList, @Context final UriInfo uriInfo) {
        return getResponseBuilder.build(uriInfo, getTwoPartTemplate(), ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {
                return resourceService.getEntitiesByIds(resource, idList, uriInfo.getRequestUri());
            }
        });
    }

    @Override
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public CustomEntityResource getCustomResource(final String id, final UriInfo uriInfo) {
        return new CustomEntityResource(id, resourceHelper.getEntityDefinition(RESOURCE_NAME), resourceHelper);
    }

    @Override
    @POST
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public Response post(final EntityBody entityBody, @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP POST is forbidden for ingestion jobs");
    }

    @PUT
    @Path("{id}")
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public Response put(@PathParam("id") final String id, final EntityBody entityBody,
            @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP PUT is forbidden for ingestion jobs");
    }

    @Override
    @DELETE
    @Path("{id}")
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public Response delete(@PathParam("id") final String id, @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP DELETE is forbidden for ingestion jobs");
    }

    @Override
    @PATCH
    @Path("{id}")
    @RightsAllowed({ Right.SECURITY_EVENT_VIEW})
    public Response patch(@PathParam("id") final String id,
                          final EntityBody entityBody,
                          @Context final UriInfo uriInfo) {
        throw new APIAccessDeniedException("HTTP PATCH is forbidden for ingestion jobs");
    }

    /**
     * Make sure that HTTP OPTIONS requests are forbidden.
     */
    @OPTIONS
    @RightsAllowed({Right.SECURITY_EVENT_VIEW})
    public Response options() {
        throw new APIAccessDeniedException("HTTP OPTIONS is forbidden");
    }

}

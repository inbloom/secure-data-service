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
package org.slc.sli.api.resources.generic.response;

import com.sun.jersey.server.impl.application.WebApplicationContext;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.slc.sli.api.resources.generic.representation.HateoasLink;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.EntityDecorator;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ChangedUriInfo;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.view.View;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.common.util.entity.EntityManipulator;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Builds a get response
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 */

@Scope("request")
@Component
public class GetResponseBuilder extends ResponseBuilder {

    public static final String ORIGINAL_REQUEST_KEY = "original-request";

    @Autowired
    @Qualifier("defaultResourceService")
    protected ResourceService resourceService;

    @Autowired
    private List<EntityDecorator> entityDecorators;

    @Autowired
    private View optionalView;

    @Autowired
    private ResourceEndPoint resourceEndPoint;

    @Autowired
    private HateoasLink hateoasLink;
    
    /**
     * Throws a QueryParseException if the end user tried to query an endpoint that does
     * not support querying.
     * 
     * 
     * @param uriInfo
     */
    protected void validatePublicResourceQuery(final UriInfo uriInfo) {
        List<PathSegment> uriPathSegments = uriInfo.getPathSegments();
        
        if (uriPathSegments != null) {
         // if of the form "v1/foo"
            if (uriPathSegments.size() == 2) {
                String endpoint = uriPathSegments.get(1).getPath();
                
                // if that endpoint does not allow querying
                if (this.resourceEndPoint.getQueryingDisallowedEndPoints().contains(endpoint)) {
                    ApiQuery apiQuery = new ApiQuery(uriInfo);
                    
                    // if the user tried to execute a query/filter
                    if (apiQuery.getCriteria().size() > 0) {
                        throw new QueryParseException("Querying not allowed", apiQuery.toString());
                    }
                }
            }
        }
    }

    public Response build(final UriInfo uriInfo, final ResourceTemplate template,
                          final ResourceMethod method, final GenericResource.GetResourceLogic logic) {
        
        validatePublicResourceQuery(uriInfo);
        
        //get the resource container
        Resource resource = constructAndCheckResource(uriInfo, template, method);

        // Handle exclude fields in code
        Set<String> excludeFields = new HashSet<String>();
        List<String> uriExcludeFields = uriInfo.getQueryParameters().remove(ParameterConstants.EXCLUDE_FIELDS);
        if (uriExcludeFields != null && !uriExcludeFields.isEmpty()) {
            for (String excludeField : uriExcludeFields) {
                excludeFields.addAll(Arrays.asList(excludeField.split(",")));
            }
        }

        //run the resource logic
        ServiceResponse serviceResponse = logic.run(resource);
        List<EntityBody> entities = serviceResponse.getEntityBodyList();

        //add the optional fields
        entities = optionalView.add(entities, resource.getResourceType(), uriInfo.getQueryParameters());

        //add the links
        entities = hateoasLink.add(resource.getResourceType(), entities, uriInfo);

        //apply the decorators and strip out excludedFields
        for (EntityBody entityBody : entities) {
            EntityManipulator.removeFields(entityBody, new ArrayList<String>(excludeFields));
            for (EntityDecorator entityDecorator : entityDecorators) {
                entityDecorator.decorate(entityBody, resourceHelper.getEntityDefinition(resource), uriInfo.getQueryParameters());
            }
        }

        //get the page count
        long pagingHeaderTotalCount = serviceResponse.getEntityCount();

        Object retValue = getResponseObject(entities);

        //add the paging headers and return the data
        return addPagingHeaders(Response.ok(new EntityResponse(resourceService.getEntityType(resource), retValue)),
                pagingHeaderTotalCount, uriInfo).build();
    }

    protected Object getResponseObject(List<EntityBody> entities) {
        final Object retVal = entities.size() == 1 ? entities.get(0) : entities;
        return retVal;
    }

    protected Response.ResponseBuilder addPagingHeaders(Response.ResponseBuilder resp, long total, UriInfo info) {
        if (info != null && resp != null) {
            NeutralQuery neutralQuery = new ApiQuery(info);
            int offset = neutralQuery.getOffset();
            int limit = neutralQuery.getLimit();

            int nextStart = offset + limit;
            final String originalRequest = getOriginalRequestPath(info);
            if (nextStart < total) {
                neutralQuery.setOffset(nextStart);

                String nextLink = info.getRequestUriBuilder().replacePath(originalRequest).replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + nextLink + ">; rel=next");
            }

            if (offset > 0) {
                int prevStart = Math.max(offset - limit, 0);
                neutralQuery.setOffset(prevStart);

                String prevLink = info.getRequestUriBuilder().replacePath(originalRequest).replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + prevLink + ">; rel=prev");
            }

            resp.header(ParameterConstants.HEADER_TOTAL_COUNT, total);
        }

        return resp;
    }

    private String getOriginalRequestPath(final UriInfo context) {
        if (context instanceof WebApplicationContext) {
            String originalUri = context.getBaseUri() + ((WebApplicationContext) context).getProperties().get(ORIGINAL_REQUEST_KEY).toString();
            return originalUri;
        } else if (context instanceof ChangedUriInfo) {
            return ((ChangedUriInfo)context).getOriginalUri();
        } else {
            return context.getRequestUri().toString();
        }
    }
}

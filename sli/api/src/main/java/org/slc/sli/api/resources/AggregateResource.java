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


package org.slc.sli.api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.url.URLCreator;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;


/**
 * Jersey resource for aggregate discovery
 *
 * @author srupasinghe
 *
 */

@Path("aggregation")
@Component
@Scope("request")
@Produces(HypermediaType.JSON + ";charset=utf-8")
public class AggregateResource {
    final EntityDefinitionStore entityDefs;

    @Autowired
    private URLCreator aggregateURLCreator;

    @Autowired
    private URLCreator associationURLCreator;

    @Autowired
    AggregateResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }

    /**
     * Returns the uppermost associations for the logged in user
     * i.e For a staff(superintendent) it'll return a list of associated educationalOrganizations
     *
     * @entity.type Home
     * @response.representation.200.mediaType application/json
     * @response.representation.200.qname Home
     *
     * @param uriInfo
     * @return
     */
    @GET
    public Response getUpperMostAssociationsForUser(@Context final UriInfo uriInfo) {
        // get the user entity
        Entity userEntity = ResourceUtil.getSLIPrincipalFromSecurityContext().getEntity();

        // build the param map
        NeutralQuery neutralQuery = new ApiQuery(uriInfo);
        if (userEntity != null) {
            if (userEntity.getType().equals("staff")) {
                List<String> associatedEdOrgs = new ArrayList<String>();
                for (EntityBody e : this.entityDefs.lookupByEntityType("educationOrganization").getService().list(neutralQuery)) {
                    associatedEdOrgs.add((String) e.get("id"));
                }
                neutralQuery.addCriteria(new NeutralCriteria("groupBy.districtId" , "in", associatedEdOrgs));
            }

        }

        // return as browser response
        return getLinksResponse(associationURLCreator, uriInfo, neutralQuery);
    }

    /**
     * Returns the aggregations based on district and the given query params
     *
     * @param districtId
     * @param gradeId
     * @param subjectId
     * @param typeId
     * @param uriInfo
     * @return
     */
    @Path("/district/{districtId}")
    @GET
    public Response getDistrictBasedAggregates(@Context final UriInfo uriInfo) {

        NeutralQuery neutralQuery = new ApiQuery(uriInfo);

        return getLinksResponse(aggregateURLCreator, uriInfo, neutralQuery);
    }

    /**
     * Returns the aggregations based on school and the given query params
     *
     * @param districtId
     * @param gradeId
     * @param subjectId
     * @param typeId
     * @param uriInfo
     * @return
     */
    @Path("/school/{schoolId}")
    @GET
    public Response getSchoolBasedAggregates(@Context final UriInfo uriInfo) {

        NeutralQuery neutralQuery = new ApiQuery(uriInfo);

        return getLinksResponse(aggregateURLCreator, uriInfo, neutralQuery);
    }

    /**
     * Returns a response object with aggregate links filtered by the given params
     *
     * @param uriInfo
     * @param params
     * @return
     */
    private Response getLinksResponse(URLCreator creator, final UriInfo uriInfo, NeutralQuery neutralQuery) {
        // get the aggregate URLs
        Entity userEntity = ResourceUtil.getSLIPrincipalFromSecurityContext().getEntity();
        List<EmbeddedLink> links = null;

        if (userEntity != null) {
            links = creator.getUrls(uriInfo, userEntity.getEntityId(), userEntity.getType(), neutralQuery);
        } else {
            links = creator.getUrls(uriInfo, "", "", neutralQuery);
        }


        // create a final map of links to relevant links
        Map<String, Object> linksMap = new HashMap<String, Object>();
        linksMap.put(ResourceConstants.LINKS, links);

        // return as browser response
        return Response.ok(linksMap).build();
    }

    public void setAggregateURLCreator(URLCreator aggregateURLCreator) {
        this.aggregateURLCreator = aggregateURLCreator;
    }

    public void setAssociationURLCreator(URLCreator associationURLCreator) {
        this.associationURLCreator = associationURLCreator;
    }
}

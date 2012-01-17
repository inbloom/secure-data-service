package org.slc.sli.api.resources;

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
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.url.URLCreator;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.domain.Entity;

/**
 * Jersey resource for aggregate discovery
 * 
 * @author srupasinghe
 * 
 */

@Path("aggregation")
@Component
@Scope("request")
@Produces(Resource.JSON_MEDIA_TYPE)
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
     * @param uriInfo
     * @return
     */
    @GET
    public Response getUpperMostAssociationsForUser(@Context final UriInfo uriInfo) {
        //get the user entity
        Entity userEntity = ResourceUtil.getSLIPrincipalFromSecurityContext().getEntity();

        //build the param map
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", userEntity.getEntityId());
        params.put("type", userEntity.getType());
        
        // return as browser response
        return getLinksResponse(associationURLCreator, uriInfo, params);
    }
    
    /**
     * Returns the aggregations based on distrct and the given query params
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
        
        // build the param map
        Map<String, String> params = combineParameters(uriInfo.getPathParameters(), uriInfo.getQueryParameters());
        
        return getLinksResponse(aggregateURLCreator, uriInfo, params);
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
        
        Map<String, String> params = combineParameters(uriInfo.getPathParameters(), uriInfo.getQueryParameters());
        
        return getLinksResponse(aggregateURLCreator, uriInfo, params);
    }
    
    /**
     * Combines the query params and the path params to create one map
     * 
     * @param pathParams
     *            The path params from the request
     * @param queryParams
     *            The query params from the request
     * @return
     */
    protected Map<String, String> combineParameters(Map<String, List<String>> pathParams,
            Map<String, List<String>> queryParams) {
        // build the param map
        Map<String, String> params = ResourceUtil.convertToMap(pathParams);
        
        params.putAll(ResourceUtil.convertToMap(queryParams));
        
        return params;
    }
    
    /**
     * Returns a response object with aggregate links filtered by the given params
     * 
     * @param uriInfo
     * @param params
     * @return
     */
    private Response getLinksResponse(URLCreator creator, final UriInfo uriInfo, Map<String, String> params) {
        // get the aggregate URLs
        List<EmbeddedLink> links = creator.getUrls(uriInfo, params);
        
        // create a final map of links to relevant links
        Map<String, Object> linksMap = new HashMap<String, Object>();
        linksMap.put(ResourceUtil.LINKS, links);
        
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

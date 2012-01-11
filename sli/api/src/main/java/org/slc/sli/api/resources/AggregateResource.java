package org.slc.sli.api.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.url.URLCreator;
import org.slc.sli.api.resources.util.ResourceUtil;

import static org.slc.sli.api.resources.util.ResourceConstants.QUERY_PARAM_GRADE;
import static org.slc.sli.api.resources.util.ResourceConstants.QUERY_PARAM_SUBJECT;
import static org.slc.sli.api.resources.util.ResourceConstants.QUERY_PARAM_TYPE;
import static org.slc.sli.api.resources.util.ResourceConstants.PATH_PARAM_DISTRICT;
import static org.slc.sli.api.resources.util.ResourceConstants.PATH_PARAM_SCHOOL;

/**
 * Jersey resource for aggregate discovery
 * @author srupasinghe
 *
 */

@Path("agg")
@Component
@Scope("request")
@Produces(Resource.JSON_MEDIA_TYPE)
public class AggregateResource {

	private static final Logger LOG = LoggerFactory.getLogger(AggregateResource.class);
    
    final EntityDefinitionStore entityDefs;
    
    @Autowired
    private URLCreator aggregateURLCreator;
    
    @Autowired
    AggregateResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }
    
    /**
     * Returns the uppermost associations for the logged in user
     * @param uriInfo
     * @return
     */
    @GET
    public Response getUpperMostAssociationsForUser(@Context final UriInfo uriInfo) {
    	
    	// create a final map of links to relevant links
        Map<String, Object> linksMap = new HashMap<String, Object>();
        
        // return as browser response
        return Response.ok(linksMap).build();
    }
        
    /**
     * Returns the aggregations based on distrct and the given query params
     * @param districtId
     * @param gradeId
     * @param subjectId
     * @param typeId
     * @param uriInfo
     * @return
     */
    @Path("/district/{district}")
    @GET
    public Response getDistrictBasedAggregates(@PathParam(PATH_PARAM_DISTRICT) String districtId,
    											@QueryParam(QUERY_PARAM_GRADE) String gradeId,
    											@QueryParam(QUERY_PARAM_SUBJECT) String subjectId,
    											@QueryParam(QUERY_PARAM_TYPE) String typeId,
    											@Context final UriInfo uriInfo) {

    	//build the param map
    	Map<String, String> params = ResourceUtil.converToMap(uriInfo.getQueryParameters());
    	params.put(PATH_PARAM_DISTRICT, districtId);
    	
    	return getAggregateLinksResponse(uriInfo, params);
    }
    
    /**
     * Returns the aggregations based on school and the given query params
     * @param districtId
     * @param gradeId
     * @param subjectId
     * @param typeId
     * @param uriInfo
     * @return
     */
    @Path("/school/{school}")
    @GET
    public Response getSchoolBasedAggregates(@PathParam(PATH_PARAM_SCHOOL) String schoolId,
    											@QueryParam(QUERY_PARAM_GRADE) String gradeId,
    											@QueryParam(QUERY_PARAM_SUBJECT) String subjectId,
    											@QueryParam(QUERY_PARAM_TYPE) String typeId,
    											@Context final UriInfo uriInfo) {

    	//build the param map
    	Map<String, String> params = ResourceUtil.converToMap(uriInfo.getQueryParameters());
    	params.put(PATH_PARAM_SCHOOL, schoolId);
    	
    	return getAggregateLinksResponse(uriInfo, params);
    }

    /**
     * Returns a response object with aggregate links filtered by the given params
     * @param uriInfo
     * @param params
     * @return
     */
	private Response getAggregateLinksResponse(final UriInfo uriInfo, Map<String, String> params) {
		//get the aggregate URLs
    	List<EmbeddedLink> links = aggregateURLCreator.getUrls(uriInfo, params);
    	
    	// create a final map of links to relevant links
        Map<String, Object> linksMap = new HashMap<String, Object>();
        linksMap.put(ResourceUtil.LINKS, links);
        
        // return as browser response
        return Response.ok(linksMap).build();
	}

	public void setAggregateURLCreator(URLCreator aggregateURLCreator) {
		this.aggregateURLCreator = aggregateURLCreator;
	}
}

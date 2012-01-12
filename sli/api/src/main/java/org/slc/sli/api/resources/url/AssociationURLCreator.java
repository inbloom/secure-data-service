package org.slc.sli.api.resources.url;

import static org.slc.sli.api.resources.util.ResourceConstants.PATH_PARAM_DISTRICT;
import static org.slc.sli.api.resources.util.ResourceConstants.PATH_PARAM_SCHOOL;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_STAFF_EDORG_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_STAFF_SCHOOL_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_EDORG_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_STAFF_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_SCHOOL_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ROLE_TYPE_SUPERINTENDENT;
import static org.slc.sli.api.resources.util.ResourceConstants.ROLE_TYPE_PRINCIPAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.domain.Entity;

public class AssociationURLCreator extends URLCreator {

	@Override
	/**
	 * Returns a list association links for the logged in user
	 */
	public List<EmbeddedLink> getUrls(final UriInfo uriInfo,
			Map<String, String> params) {
		List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
		
		// use login data to resolve what type of user and ID of user
        Map<String, String> map = ResourceUtil.resolveUserDataFromSecurityContext();
        
        // remove user's type from map
        String userId = map.remove("id");
        String roleType = map.remove("roleType");
        
        if(roleType.equals(ROLE_TYPE_SUPERINTENDENT)) {
        	params.put(ENTITY_BODY_STAFF_ID, userId);
        
        	//get the staff-edorg links
        	results = getStaffEducationOrganizationAssociationLinks(uriInfo, params);
        }
        else if(roleType.equals(ROLE_TYPE_PRINCIPAL)) {
        	params.put(ENTITY_BODY_STAFF_ID, userId);
            
        	//get the staff-edorg links
        	results = getStaffSchoolAssociationLinks(uriInfo, params);
        }
        
        return results;
	}
	
	/**
	 * Returns the staff-educationorganization association links for the logged in user
	 * @param uriInfo
	 * @param params
	 * @return
	 */
	protected List<EmbeddedLink> getStaffEducationOrganizationAssociationLinks(final UriInfo uriInfo, Map<String, String> params) {
		List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
         
        //get the aggregations that match the query params
        Iterable<Entity> assocations = repo.findByFields(ENTITY_TYPE_STAFF_EDORG_ASSOC, params);
      		
        //iterate through the aggregations and build the embedded links list
        for(Entity e : assocations) {
        	System.out.println(e.getType());
        	Map<String, Object> body = e.getBody();
        	System.out.println(body.get(ENTITY_BODY_EDORG_ID));
        	results.add(new EmbeddedLink(ResourceUtil.LINKS, e.getType(), ResourceUtil.getURI(uriInfo, PATH_PARAM_DISTRICT, (String)body.get(ENTITY_BODY_EDORG_ID)).toString()));        			
        }

        
		return results;
	}
	
	/**
	 * Returns the staff-school association links for the logged in user
	 * @param uriInfo
	 * @param params
	 * @return
	 */
	protected List<EmbeddedLink> getStaffSchoolAssociationLinks(final UriInfo uriInfo, Map<String, String> params) {
		List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
        
        //get the aggregations that match the query params
        Iterable<Entity> assocations = repo.findByFields(ENTITY_TYPE_STAFF_SCHOOL_ASSOC, params);
      		
        //iterate through the aggregations and build the embedded links list
        for(Entity e : assocations) {
        	Map<String, Object> body = e.getBody();
        	results.add(new EmbeddedLink(ResourceUtil.LINKS, e.getType(), ResourceUtil.getURI(uriInfo, PATH_PARAM_SCHOOL, (String)body.get(ENTITY_BODY_SCHOOL_ID)).toString()));        			
        }

        
		return results;
	}

}

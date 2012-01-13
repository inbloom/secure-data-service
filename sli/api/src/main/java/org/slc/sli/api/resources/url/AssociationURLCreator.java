package org.slc.sli.api.resources.url;

import static org.slc.sli.api.resources.util.ResourceConstants.RESOURCE_PATH_DISTRICT;
import static org.slc.sli.api.resources.util.ResourceConstants.RESOURCE_PATH_SCHOOL;
import static org.slc.sli.api.resources.util.ResourceConstants.RESOURCE_PATH_AGG;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_STAFF_EDORG_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_STAFF_SCHOOL_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_EDORG_SCHOOL_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_EDORG_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_STAFF_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_SCHOOL_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ROLE_TYPE_SUPERINTENDENT;
import static org.slc.sli.api.resources.util.ResourceConstants.ROLE_TYPE_PRINCIPAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.domain.Entity;

/**
 * Creates URL sets for associations.
 * Return URL format /agg/district/{JUUID}, /agg/school/{JUUID}
 * 
 * @author srupasinghe
 * 
 */
public class AssociationURLCreator extends URLCreator {
    @Override
    /**
     * Returns a list association links for the logged in user
     */
    public List<EmbeddedLink> getUrls(final UriInfo uriInfo, Map<String, String> params) {
        List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
        
        // use login data to resolve what type of user and ID of user
        Map<String, String> map = ResourceUtil.resolveUserDataFromSecurityContext();
        
        // remove user's type from map
        String userId = map.remove("id");
        String roleType = map.remove("roleType");
        
        if (roleType.equals(ROLE_TYPE_SUPERINTENDENT)) {
            params.put(ENTITY_BODY_STAFF_ID, userId);
            
            // get the staff-edorg links
            results = getStaffEducationOrganizationAssociationLinks(uriInfo, params);
        } else if (roleType.equals(ROLE_TYPE_PRINCIPAL)) {
            params.put(ENTITY_BODY_STAFF_ID, userId);
            
            // get the staff-edorg links
            results = getStaffSchoolAssociationLinks(uriInfo, params);
        }
        
        return results;
    }
    
    /**
     * Returns a list of association links for a given user
     * @param uriInfo The URIInfo context
     * @param assocationType The type of association to look for
     * @param entityBodyIdName The entity Id to search for
     * @param params
     * @return
     */
    protected List<String> getAssociationLinks(final UriInfo uriInfo, String assocationType, 
            String entityBodyIdName, Map<String, String> params) {
        List<String> results = new ArrayList<String>();
        
        // get the associations that match the query params
        Iterable<Entity> assocations = repo.findByFields(assocationType, params);
        
        // iterate through the associations and build the id list
        for (Entity e : assocations) {
            Map<String, Object> body = e.getBody();
            
            results.add((String) body.get(entityBodyIdName));
        }
        
        return results;        
    }
    
    /**
     * Builds a list of embedded link objects
     * @param uriInfo The context
     * @param associations The associations to build the list for
     * @param associationType The type of assocation
     * @param resourcePathType The resource path for the returned Url
     * @return
     */
    protected List<EmbeddedLink> buildEmbeddedLinks(final UriInfo uriInfo, List<String> associations, String associationType, 
            String resourcePathType) {
        List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
        
        // iterate through the associations and build the embedded links list
        for (String s : associations) {
            results.add(new EmbeddedLink(ResourceUtil.LINKS, associationType, 
                    uriInfo.getBaseUriBuilder().path(RESOURCE_PATH_AGG).path(resourcePathType).
                    path(s).build().toString()));
        }
        
        return results;
    }
    
    /**
     * Returns the staff-educationorganization association links for the logged in user
     * 
     * @param uriInfo
     * @param params
     * @return
     */
    protected List<EmbeddedLink> getStaffEducationOrganizationAssociationLinks(final UriInfo uriInfo,
            Map<String, String> params) {
        List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
        
        //get the associations for the district
        List<String> ids = getAssociationLinks(uriInfo, ENTITY_TYPE_STAFF_EDORG_ASSOC, ENTITY_BODY_EDORG_ID, params);
        //add it to the list
        results.addAll(buildEmbeddedLinks(uriInfo, ids, ENTITY_TYPE_STAFF_EDORG_ASSOC, RESOURCE_PATH_DISTRICT));
        
        for (String s : ids) {
            Map<String, String> edOrgIds = new HashMap<String, String>();
            edOrgIds.put(ENTITY_BODY_EDORG_ID, s);
            
            //get the associations for school under the district
            List<String> list = getAssociationLinks(uriInfo, ENTITY_TYPE_EDORG_SCHOOL_ASSOC, ENTITY_BODY_SCHOOL_ID, edOrgIds);
            //add it to the list
            results.addAll(buildEmbeddedLinks(uriInfo, list, ENTITY_TYPE_EDORG_SCHOOL_ASSOC, RESOURCE_PATH_SCHOOL));
        }
        
        return results;
    }
    
    /**
     * Returns the staff-school association links for the logged in user
     * 
     * @param uriInfo
     * @param params
     * @return
     */
    protected List<EmbeddedLink> getStaffSchoolAssociationLinks(final UriInfo uriInfo, Map<String, String> params) {
        List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();
        
        //get the associations for the school
        List<String> ids = getAssociationLinks(uriInfo, ENTITY_TYPE_STAFF_SCHOOL_ASSOC, ENTITY_BODY_SCHOOL_ID, params);
        //add it to the list
        results.addAll(buildEmbeddedLinks(uriInfo, ids, ENTITY_TYPE_STAFF_SCHOOL_ASSOC, RESOURCE_PATH_SCHOOL));
        
        return results;
    }
    
}

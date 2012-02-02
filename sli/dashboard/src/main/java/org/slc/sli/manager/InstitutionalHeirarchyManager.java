package org.slc.sli.manager;

import com.google.gson.Gson;

import org.slc.sli.entity.School;
import org.slc.sli.entity.EducationalOrganization;
import org.slc.sli.util.SecurityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 * 
 * @author syau
 *
 */
public class InstitutionalHeirarchyManager extends Manager {

    public School[] getSchools() {
        return apiClient.getSchools(SecurityUtil.getToken());
    }
    public EducationalOrganization[] getParentEducationalOrganizations(School s) {
        return apiClient.getParentEducationalOrganizations(SecurityUtil.getToken(), s);
    }
    public EducationalOrganization[] getParentEducationalOrganizations(EducationalOrganization edOrg) {
        return apiClient.getParentEducationalOrganizations(SecurityUtil.getToken(), edOrg);
    }

    // JSON key names
    public static final String NAME = "name"; 
    public static final String SCHOOLS = "schools"; 

    /**
     * Returns the institutional heirarchy visible to the current user as a JSON string, 
     * with the ed-org level flattened  
     * This assumes there are no cycles in the education organization heirarchy tree. 
     * @return 
     */
    public String getInstHeirarchyJSON() {
        // Okay, this function should arguably be placed in a view package, but if the school JSONs 
        // are already being constructed in the *API Client* level (WTF was that about anyway?!?),
        // constructing inst heirarchy as JSON here isn't making things worse than they already are. 

        // Call API to get relationships. 
        School[] schools = getSchools();

        // This maps ids from educational organisations to schools reachable from it via the "child" relationship   
        Map<String, HashSet<School>> schoolReachableFromEdOrg = new HashMap<String, HashSet<School>>();
        // This just maps ed org ids to ed org objects. 
        Map<String, EducationalOrganization> edOrgIdMap = new HashMap<String, EducationalOrganization>();
        
        // traverse the ancestor chain from each school and find ed orgs that the school is reachable from
        for (int i = 0; i < schools.length; i++) {
            HashMap<String, EducationalOrganization> ancestorEdOrgs = new HashMap<String, EducationalOrganization>(); // strictly not needed, but makes the code easier to read. 
            EducationalOrganization[] edOrgs = getParentEducationalOrganizations(schools[i]); 
            for (int j = 0; j < edOrgs.length; j++) {
                insertEdOrgAndAncesterIntoSet(ancestorEdOrgs, edOrgs[j]);
            }
            // insert into the reverse map
            edOrgIdMap.putAll(ancestorEdOrgs);
            for (String edOrgId : ancestorEdOrgs.keySet()) {
                if (!schoolReachableFromEdOrg.keySet().contains(edOrgId)) {
                    schoolReachableFromEdOrg.put(edOrgId, new HashSet<School>());
                }
                schoolReachableFromEdOrg.get(edOrgId).add(schools[i]);
            }
        }

        // write out the result
        JSONArray retVal = new JSONArray();
        for (String edOrgId : schoolReachableFromEdOrg.keySet()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(NAME, edOrgIdMap.get(edOrgId).getNameOfInstitution());
                // convert school ids to the school object array, to be converted into JSON
                Set<School> reachableSchools = schoolReachableFromEdOrg.get(edOrgId);
                School[] reachableSchoolsArr = new School [reachableSchools.size()];
                Gson gson = new Gson();
                String schoolJSONString = gson.toJson(reachableSchools.toArray(reachableSchoolsArr));
                obj.put(SCHOOLS, new JSONArray(schoolJSONString));
                retVal.put(obj);
            } catch (JSONException e) {
                throw new RuntimeException("error creating json object for " + edOrgId);
            }
        }
        
        // TODO: remove this block when ed-org is implemented on live server
        // Temporary: insert a dummy edorg for all orphan schools.  
        Vector<School> orphanSchools = new Vector<School>();
        for (int i = 0; i < schools.length; i++) {
            School s = schools[i];
            boolean isOrphan = true;
            for (HashSet<School> reachableSchools : schoolReachableFromEdOrg.values()) {
                if (reachableSchools.contains(s)) { isOrphan = false; break; }
            }
            if (isOrphan) {
                orphanSchools.add(s);
            }
        }
        if (orphanSchools.size() > 0) {
            try {
                JSONObject obj = new JSONObject();
                obj.put(NAME, "Dummy Ed-Org");
                School [] orphanSchoolsArr = new School[orphanSchools.size()];
                Gson gson = new Gson();
                String schoolJSONString = gson.toJson(orphanSchools.toArray(orphanSchoolsArr));
                obj.put(SCHOOLS, new JSONArray(schoolJSONString));
                retVal.put(obj);
            } catch (JSONException e) {
                throw new RuntimeException("error creating json object for dummy edOrg");
            }
        }
        return retVal.toString();
    }
    // helper function: 
    // This assumes there is no cycle in the education organization heirarchy tree. 
    private void insertEdOrgAndAncesterIntoSet(Map<String, EducationalOrganization> map, EducationalOrganization edOrg) {
        map.put(edOrg.getId(), edOrg);
        EducationalOrganization[] parentEdOrgs = getParentEducationalOrganizations(edOrg);
        for (int i = 0; i < parentEdOrgs.length; i++) {
            insertEdOrgAndAncesterIntoSet(map, parentEdOrgs[i]);
        }
    }
}

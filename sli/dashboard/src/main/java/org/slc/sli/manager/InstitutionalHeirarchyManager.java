package org.slc.sli.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 *
 * @author syau
 *
 */
public class InstitutionalHeirarchyManager extends Manager {

    // resource String
    public static final String DUMMY_EDORG_NAME = "No Ed-Org";

    // accessors 
    public List<GenericEntity> getSchools(String token) {
        return apiClient.getSchools(token, null);
    }
    public List<GenericEntity> getAssociatedEducationalOrganizations(String token, GenericEntity s) {
        return apiClient.getAssociatedEducationalOrganizations(token, s);
    }
    public List<GenericEntity> getParentEducationalOrganizations(String token, GenericEntity edOrg) {
        return apiClient.getParentEducationalOrganizations(token, edOrg);
    }

    /**
     * Returns the institutional heirarchy visible to the user with the given auth token as a JSON string,
     * with the ed-org level flattened
     * This assumes there are no cycles in the education organization heirarchy tree.
     * @return
     */
    public String getInstHeirarchyJSON(String token) {
        // Okay, this function should arguably be placed in a view package, but if the school JSONs
        // are already being constructed in the *API Client* level (WTF was that about anyway?!?),
        // constructing inst heirarchy as JSON here isn't making things worse than they already are.

        // Find all the schools first.
        List<GenericEntity> schools = getSchools(token);
        if (schools == null) { return new JSONArray().toString(); }

        // This maps ids from educational organisations to schools reachable from it via the "child" relationship
        Map<String, HashSet<GenericEntity>> schoolReachableFromEdOrg = new HashMap<String, HashSet<GenericEntity>>();
        // This just maps ed org ids to ed org objects.
        Map<String, GenericEntity> edOrgIdMap = new HashMap<String, GenericEntity>();

        // traverse the ancestor chain from each school and find ed orgs that the school is reachable from
        for (int i = 0; i < schools.size(); i++) {
            HashMap<String, GenericEntity> ancestorEdOrgs = new HashMap<String, GenericEntity>(); // strictly not needed, but makes the code easier to read.
            List<GenericEntity> edOrgs = getAssociatedEducationalOrganizations(token, schools.get(i));
            for (int j = 0; j < edOrgs.size(); j++) {
                insertEdOrgAndAncesterIntoSet(token, ancestorEdOrgs, edOrgs.get(j));
            }
            // insert ed-org - school mapping into the reverse map
            edOrgIdMap.putAll(ancestorEdOrgs);
            for (String edOrgId : ancestorEdOrgs.keySet()) {
                if (!schoolReachableFromEdOrg.keySet().contains(edOrgId)) {
                    schoolReachableFromEdOrg.put(edOrgId, new HashSet<GenericEntity>());
                }
                schoolReachableFromEdOrg.get(edOrgId).add(schools.get(i));
            }
        }

        // write out the result
        JSONArray retVal = new JSONArray();
        for (String edOrgId : schoolReachableFromEdOrg.keySet()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(Constants.ATTR_NAME, edOrgIdMap.get(edOrgId).get(Constants.ATTR_NAME_OF_INST));
                // convert school ids to the school object array, to be converted into JSON
                Set<GenericEntity> reachableSchools = schoolReachableFromEdOrg.get(edOrgId);
                GenericEntity[] reachableSchoolsArr = new GenericEntity [reachableSchools.size()];
                Gson gson = new Gson();
                String schoolJSONString = gson.toJson(reachableSchools.toArray(reachableSchoolsArr));
                obj.put(Constants.ATTR_SCHOOLS, new JSONArray(schoolJSONString));
                retVal.put(obj);
            } catch (JSONException e) {
                throw new RuntimeException("error creating json object for " + edOrgId);
            }
        }

        // TODO: remove this block when ed-org is implemented on live server
        // Temporary: insert a dummy edorg for all orphan schools.
        Vector<GenericEntity> orphanSchools = new Vector<GenericEntity>();
        for (int i = 0; i < schools.size(); i++) {
            GenericEntity s = schools.get(i);
            boolean isOrphan = true;
            for (HashSet<GenericEntity> reachableSchools : schoolReachableFromEdOrg.values()) {
                if (reachableSchools.contains(s)) { isOrphan = false; break; }
            }
            if (isOrphan) {
                orphanSchools.add(s);
            }
        }
        if (orphanSchools.size() > 0) {
            try {
                JSONObject obj = new JSONObject();
                obj.put(Constants.ATTR_NAME, DUMMY_EDORG_NAME);
                GenericEntity [] orphanSchoolsArr = new GenericEntity[orphanSchools.size()];
                Gson gson = new Gson();
                String schoolJSONString = gson.toJson(orphanSchools.toArray(orphanSchoolsArr));
                obj.put(Constants.ATTR_SCHOOLS, new JSONArray(schoolJSONString));
                retVal.put(obj);
            } catch (JSONException e) {
                throw new RuntimeException("error creating json object for dummy edOrg");
            }
        }
        return retVal.toString();
    }
    // helper function:
    // This assumes there is no cycle in the education organization heirarchy tree.
    private void insertEdOrgAndAncesterIntoSet(String token, Map<String, GenericEntity> map, GenericEntity edOrg) {
        map.put((String) (edOrg.get(Constants.ATTR_ID)), edOrg);
        List<GenericEntity> parentEdOrgs = getParentEducationalOrganizations(token, edOrg);
        for (int i = 0; i < parentEdOrgs.size(); i++) {
            insertEdOrgAndAncesterIntoSet(token, map, parentEdOrgs.get(i));
        }
    }

}

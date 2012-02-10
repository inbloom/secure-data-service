package org.slc.sli.manager;

import com.google.gson.Gson;

import org.slc.sli.entity.School;
import org.slc.sli.entity.EducationalOrganization;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 *
 * @author syau
 *
 */
public class InstitutionalHierarchyManager extends Manager {

    // JSON key names
    public static final String NAME = "name";
    public static final String SCHOOLS = "schools";
    // resource String
    public static final String DUMMY_EDORG_NAME = "No Ed-Org";

    // accessors 
    public School[] getSchools(String token) {
        return apiClient.getSchools(token);
    }
    public EducationalOrganization[] getAssociatedEducationalOrganizations(String token, School s) {
        return apiClient.getAssociatedEducationalOrganizations(token, s);
    }
    public EducationalOrganization[] getParentEducationalOrganizations(String token, EducationalOrganization edOrg) {
        return apiClient.getParentEducationalOrganizations(token, edOrg);
    }

    /**
     * Returns the institutional hierarchy visible to the user with the given auth token as a JSON string,
     * with the ed-org level flattened
     * This assumes there are no cycles in the education organization hierarchy tree.
     * @return
     */
    public String getInstHierarchyJSON(String token) {
        // Find all the schools first.
        School[] schools = getSchools(token);
        if (schools == null) { return new JSONArray().toString(); }

        // This maps ids from educational organisations to schools reachable from it via the "child" relationship
        Map<String, HashSet<School>> schoolReachableFromEdOrg = new HashMap<String, HashSet<School>>();
        // This just maps ed org ids to ed org objects.
        Map<String, EducationalOrganization> edOrgIdMap = new HashMap<String, EducationalOrganization>();

        // traverse the ancestor chain from each school and find ed orgs that the school is reachable from
        for (int i = 0; i < schools.length; i++) {
            HashMap<String, EducationalOrganization> ancestorEdOrgs = new HashMap<String, EducationalOrganization>(); // strictly not needed, but makes the code easier to read.
            EducationalOrganization[] edOrgs = getAssociatedEducationalOrganizations(token, schools[i]);
            for (int j = 0; j < edOrgs.length; j++) {
                insertEdOrgAndAncesterIntoSet(token, ancestorEdOrgs, edOrgs[j]);
            }
            // insert ed-org - school mapping into the reverse map
            edOrgIdMap.putAll(ancestorEdOrgs);
            for (String edOrgId : ancestorEdOrgs.keySet()) {
                if (!schoolReachableFromEdOrg.keySet().contains(edOrgId)) {
                    schoolReachableFromEdOrg.put(edOrgId, new HashSet<School>());
                }
                schoolReachableFromEdOrg.get(edOrgId).add(schools[i]);
            }
        }

        // write out the result
        // Okay, this part should arguably be placed in a view package, but if the school JSONs
        // are already being constructed in the *API Client* level (WTF was that about anyway?!?),
        // constructing inst hierarchy as JSON here isn't making things worse than they already are.
        JSONArray retVal = makeInstHierarchyJSON(schoolReachableFromEdOrg, edOrgIdMap);
        
        // Temporary: insert a dummy edorg for all orphan schools.
        Collection<School> orphanSchools = findOrphanSchools(schools, schoolReachableFromEdOrg);
        if (orphanSchools.size() > 0) {
            insertSchoolsUnderDummyEdOrg(retVal, orphanSchools);
        }
        return retVal.toString();
    }

    // ------------- helper functions ----------------
    // This assumes there is no cycle in the education organization hierarchy tree.
    private void insertEdOrgAndAncesterIntoSet(String token, Map<String, EducationalOrganization> map, EducationalOrganization edOrg) {
        map.put(edOrg.getId(), edOrg);
        EducationalOrganization[] parentEdOrgs = getParentEducationalOrganizations(token, edOrg);
        for (int i = 0; i < parentEdOrgs.length; i++) {
            insertEdOrgAndAncesterIntoSet(token, map, parentEdOrgs[i]);
        }
    }
    // Creates a JSONArray to represent the ed-org hierarchy
    private static JSONArray makeInstHierarchyJSON(Map<String, HashSet<School>> schoolReachableFromEdOrg,
                                                   Map<String, EducationalOrganization> edOrgIdMap) {
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
        return retVal;
    }
    // Finds schools that do not belong to any ed-orgs
    private static Collection<School> findOrphanSchools(School[] schools, Map<String, HashSet<School>> schoolReachableFromEdOrg) {
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
        return orphanSchools;
    }
    // Insert schools into the JSONArray under a "dummy" ed-org
    private static JSONArray insertSchoolsUnderDummyEdOrg(JSONArray retVal, Collection<School> schools) {
        try {
            JSONObject obj = new JSONObject();
            obj.put(NAME, DUMMY_EDORG_NAME);
            School [] orphanSchoolsArr = new School[schools.size()];
            Gson gson = new Gson();
            String schoolJSONString = gson.toJson(schools.toArray(orphanSchoolsArr));
            obj.put(SCHOOLS, new JSONArray(schoolJSONString));
            retVal.put(obj);
        } catch (JSONException e) {
            throw new RuntimeException("error creating json object for dummy edOrg");
        }
        return retVal;
    }

}

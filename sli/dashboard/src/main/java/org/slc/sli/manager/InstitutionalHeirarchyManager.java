package org.slc.sli.manager;

import com.google.gson.Gson;

import org.slc.sli.entity.School;
import org.slc.sli.entity.EducationalOrganization;
import org.slc.sli.entity.EducationalOrganizationAssociation;
import org.slc.sli.entity.SchoolEducationalOrganizationAssociation;
import org.slc.sli.util.SecurityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

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
    public EducationalOrganization[] getEducationalOrganizations() {
        return apiClient.getEducationalOrganizations(SecurityUtil.getToken());
    }
    public SchoolEducationalOrganizationAssociation[] getSchoolEducationalOrganizationAssociation() {
        return apiClient.getSchoolEducationalOrganizationAssociations(SecurityUtil.getToken());
    }
    public EducationalOrganizationAssociation[] getEducationalOrganizationAssociations() {
        return apiClient.getEducationalOrganizationAssociations(SecurityUtil.getToken());
    }

    // JSON key names
    public static final String NAME = "name"; 
    public static final String SCHOOLS = "schools"; 

    /**
     * Returns the institutional heirarchy visible to the current user as a JSON string, 
     * with the ed-org level flattened  
     * @return 
     */
    public String getInstHeirarchyJSON() {
        // Okay, this function should arguably be placed in a view package, but if the school JSONs 
        // are already being constructed in the *API Client* level (WTF was that about anyway?!?),
        // constructing inst heirarchy as JSON here isn't making things worse than they already are. 

        // Call API to get relationships. 
        School[] schools = getSchools();
        EducationalOrganization[] edorgs = getEducationalOrganizations();
        SchoolEducationalOrganizationAssociation[] schoolEdorgAss = getSchoolEducationalOrganizationAssociation();
        EducationalOrganizationAssociation[] edorgAss = getEducationalOrganizationAssociations();

        // build a mapping of school and ed orgs from id to the objects
        Map<String, School> schoolIds = new HashMap<String, School>();
        for (int i = 0; i < schools.length; i++) {
            schoolIds.put(schools[i].getId(), schools[i]);
        }
        Map<String, EducationalOrganization> edOrgIds = new HashMap<String, EducationalOrganization>();
        for (int i = 0; i < edorgs.length; i++) {
            edOrgIds.put(edorgs[i].getId(), edorgs[i]);
        }

        // This maps ids from educational organisations to ids of all schools reachable from it via the "child" relationship   
        Map<String, Set<String>> flatEdOrgSchoolMap = new HashMap<String, Set<String>>();
        for (int i = 0; i < edorgs.length; i++) {
            flatEdOrgSchoolMap.put(edorgs[i].getId(), new HashSet<String>());
        }
        // build a collection of all edorgs ids directly associated with the schools
        for (int i = 0; i < schoolEdorgAss.length; i++) {
            String edOrgId = schoolEdorgAss[i].getEducationOrganizationId();
            String schoolId = schoolEdorgAss[i].getSchoolId();
            if (schoolIds.keySet().contains(schoolId)) {
                if (!flatEdOrgSchoolMap.containsKey(edOrgId)) {
                    continue; // this *shouldn't* be reached if the data integrity is okay. Maybe log an error? 
                }
                flatEdOrgSchoolMap.get(edOrgId).add(schoolId);   
            }
        }

        // traverse the ed-orgs heirarchy and add schools to from an ed-org to all ed-orgs reachable under  
        // the "parent of" relation.
        while (true) {
            boolean reachedClosure = true; // flag to tell whether we've reached closure
            for (int i = 0; i < edorgAss.length; i++) {
                // check if the parent gains any schools by absorbing the child ed org's schools 
                Set<String> reachableSchoolIds = flatEdOrgSchoolMap.get(edorgAss[i].getEducationOrganizationParentId());
                int countBefore = reachableSchoolIds.size();
                reachableSchoolIds.addAll(flatEdOrgSchoolMap.get(edorgAss[i].getEducationOrganizationChildId()));
                int countAfter = reachableSchoolIds.size();
                if (countBefore < countAfter) { reachedClosure = false; }
            }
            if (reachedClosure) { break; } 
        }

        // write out the result
        JSONArray retVal = new JSONArray();
        for (String edOrgId : edOrgIds.keySet()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put(NAME, edOrgIds.get(edOrgId).getNameOfInstitution());
                // convert school ids to the school object array, to be converted into JSON
                Set<String> edOrgSchoolIds = flatEdOrgSchoolMap.get(edOrgId);
                School[] edOrgSchools = new School [edOrgSchoolIds.size()];
                int i = 0;
                for (String s : edOrgSchoolIds) {
                    edOrgSchools[i] = schoolIds.get(s);
                    i++;
                }
                Gson gson = new Gson();
                String schoolJSONString = gson.toJson(edOrgSchools);
                obj.put(SCHOOLS, new JSONArray(schoolJSONString));
                retVal.put(obj);
            } catch (JSONException e) {
                throw new RuntimeException("error creating json object for " + edOrgId);
            }
        }

        return retVal.toString();
    }


}

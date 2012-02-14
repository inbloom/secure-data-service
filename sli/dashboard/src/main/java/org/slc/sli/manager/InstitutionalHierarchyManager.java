package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Collection;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 *
 * @author syau
 *
 */
public class InstitutionalHierarchyManager extends Manager {

    // resource String
    public static final String DUMMY_EDORG_NAME = "No Ed-Org";

    // accessors 
    public List<GenericEntity> getSchools(String token) {
        return apiClient.getSchools(token, null);
    }
    public List<GenericEntity> getAssociatedEducationalOrganizations(String token, GenericEntity school) {
        return apiClient.getAssociatedEducationalOrganizations(token, school);
    }
    public List<GenericEntity> getParentEducationalOrganizations(String token, GenericEntity edOrg) {
        return apiClient.getParentEducationalOrganizations(token, edOrg);
    }

    /**
     * Returns the institutional hierarchy visible to the user with the given auth token as a list of generic entities,
     * with the ed-org level flattened
     * This assumes there are no cycles in the education organization hierarchy tree.
     * @return
     */
    public List<GenericEntity> getInstHierarchy(String token) {

        // Find all the schools first.
        List<GenericEntity> schools = getSchools(token);
        if (schools == null) { 
            return new ArrayList<GenericEntity>();
        }

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

        // build result list
        List<GenericEntity> retVal = new ArrayList<GenericEntity>();
        for (String edOrgId : schoolReachableFromEdOrg.keySet()) {
            GenericEntity obj = new GenericEntity();
            try {
                obj.put(Constants.ATTR_NAME, edOrgIdMap.get(edOrgId).get(Constants.ATTR_NAME_OF_INST));
                // convert school ids to the school object array
                Set<GenericEntity> reachableSchools = schoolReachableFromEdOrg.get(edOrgId);
                GenericEntity[] reachableSchoolsArr = new GenericEntity [reachableSchools.size()];
                obj.put(Constants.ATTR_SCHOOLS, reachableSchools);
                retVal.add(obj);
            } catch (Exception e) {
                throw new RuntimeException("error creating json object for " + edOrgId);
            }
        }

        Collection<GenericEntity> orphanSchools = findOrphanSchools(schools, schoolReachableFromEdOrg);
        // Temporary: insert a dummy edorg for all orphan schools.
        if (orphanSchools.size() > 0) {
            insertSchoolsUnderDummyEdOrg(retVal, orphanSchools);
        }
        return retVal;
    }
    
    // ------------- helper functions ----------------
    // This assumes there is no cycle in the education organization hierarchy tree.
    private void insertEdOrgAndAncesterIntoSet(String token, Map<String, GenericEntity> map, GenericEntity edOrg) {
        map.put((String) (edOrg.get(Constants.ATTR_ID)), edOrg);
        List<GenericEntity> parentEdOrgs = getParentEducationalOrganizations(token, edOrg);
        for (int i = 0; i < parentEdOrgs.size(); i++) {
            insertEdOrgAndAncesterIntoSet(token, map, parentEdOrgs.get(i));
        }
    }

    private static Collection<GenericEntity> findOrphanSchools(List<GenericEntity> schools, Map<String, HashSet<GenericEntity>> schoolReachableFromEdOrg) {
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
        return orphanSchools;
    }
    
    // Insert schools into the list under a "dummy" ed-org
    private static List<GenericEntity> insertSchoolsUnderDummyEdOrg(List<GenericEntity> retVal, Collection<GenericEntity> schools) {
        try {
            GenericEntity obj = new GenericEntity();
            obj.put(Constants.ATTR_NAME, DUMMY_EDORG_NAME);
            GenericEntity [] orphanSchoolsArr = new GenericEntity[schools.size()];
            obj.put(Constants.ATTR_SCHOOLS, schools);
            retVal.add(obj);
        } catch (Exception e) {
            throw new RuntimeException("error creating json object for dummy edOrg");
        }
        return retVal;
    }
}

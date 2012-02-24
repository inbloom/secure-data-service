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
    public GenericEntity getAssociatedEducationalOrganization(String token, GenericEntity school) {
        return apiClient.getAssociatedEducationalOrganization(token, school);
    }
    public GenericEntity getParentEducationalOrganization(String token, GenericEntity edOrg) {
        return apiClient.getParentEducationalOrganization(token, edOrg);
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
            GenericEntity edOrg = getAssociatedEducationalOrganization(token, schools.get(i));
            while(edOrg != null) {
                String edOrgId = edOrg.getString(Constants.ATTR_ID);
                // insert ed-org id to - edOrg mapping 
                edOrgIdMap.put(edOrgId, edOrg);
                // insert ed-org - school mapping into the reverse map
                if (!schoolReachableFromEdOrg.keySet().contains(edOrgId)) {
                    schoolReachableFromEdOrg.put(edOrgId, new HashSet<GenericEntity>());
                }
                schoolReachableFromEdOrg.get(edOrgId).add(schools.get(i));
                edOrg = getParentEducationalOrganization(token, edOrg); // next in the ancestor chain
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

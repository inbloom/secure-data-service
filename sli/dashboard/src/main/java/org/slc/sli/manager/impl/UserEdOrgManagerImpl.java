package org.slc.sli.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.googlecode.ehcache.annotations.Cacheable;

import org.slc.sli.entity.Config.Data;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.manager.ApiClientManager;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.util.Constants;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 * 
 * @author syau
 * 
 */
public class UserEdOrgManagerImpl extends ApiClientManager implements UserEdOrgManager {
    
    private GenericEntity getParentEducationalOrganization(String token, GenericEntity edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganization(token, edOrgOrSchool);
    }
    
    /**
     * read token. Then, find district name associated with school.
     * 
     * @param token
     *            token-id, it is also using token as key value for cache.
     * @return District name
     */
    @Cacheable(cacheName = "user.district")
    public EdOrgKey getUserEdOrg(String token) {
        // get list of school
        // Delete this comment after Replace getToken() to token when token
        // passes actual token
        // instead of username
        List<GenericEntity> schools = getApiClient().getSchools(getToken(), null);
        if (!schools.isEmpty()) {
            // read first school
            GenericEntity school = schools.get(0);
            // read parenet organization
            // Delete this comment after Replace getToken() to token when token
            // passes actual token
            // instead of username
            GenericEntity parentEdOrg = getParentEducationalOrganization(getToken(), school);
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> metaData = (LinkedHashMap<String, Object>) parentEdOrg
                    .get(Constants.METADATA);
            if (!metaData.isEmpty()) {
                if (metaData.containsKey(Constants.EXTERNAL_ID)) {
                    return new EdOrgKey(metaData.get(Constants.EXTERNAL_ID).toString());
                }
            }
        }
        return null;
    }
    
    /**
     * Returns the institutional hierarchy visible to the user with the given
     * auth token as a list of generic entities, with the ed-org level flattened
     * This assumes there are no cycles in the education organization hierarchy
     * tree.
     * 
     * @return
     */
    @Cacheable(cacheName = "user.hierarchy")
    public List<GenericEntity> getUserInstHierarchy(String token) {
        
        // Find all the schools first.
        List<GenericEntity> schools = getApiClient().getSchools(token, null);
        if (schools == null) {
            return new ArrayList<GenericEntity>();
        }
        
        // This maps ids from educational organisations to schools reachable
        // from it via the "child"
        // relationship
        Map<String, HashSet<GenericEntity>> schoolReachableFromEdOrg = new HashMap<String, HashSet<GenericEntity>>();
        // This just maps ed org ids to ed org objects.
        Map<String, GenericEntity> edOrgIdMap = new HashMap<String, GenericEntity>();
        
        // traverse the ancestor chain from each school and find ed orgs that
        // the school is
        // reachable from
        for (int i = 0; i < schools.size(); i++) {
            GenericEntity edOrg = getParentEducationalOrganization(token, schools.get(i));
            while (edOrg != null) {
                String edOrgId = edOrg.getString(Constants.ATTR_ID);
                // insert ed-org id to - edOrg mapping
                edOrgIdMap.put(edOrgId, edOrg);
                // insert ed-org - school mapping into the reverse map
                if (!schoolReachableFromEdOrg.keySet().contains(edOrgId)) {
                    schoolReachableFromEdOrg.put(edOrgId, new HashSet<GenericEntity>());
                }
                schoolReachableFromEdOrg.get(edOrgId).add(schools.get(i));
                edOrg = getParentEducationalOrganization(token, edOrg); // next
                                                                        // in
                                                                        // the
                                                                        // ancestor
                                                                        // chain
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
    
    private static Collection<GenericEntity> findOrphanSchools(List<GenericEntity> schools,
            Map<String, HashSet<GenericEntity>> schoolReachableFromEdOrg) {
        Vector<GenericEntity> orphanSchools = new Vector<GenericEntity>();
        for (int i = 0; i < schools.size(); i++) {
            GenericEntity s = schools.get(i);
            boolean isOrphan = true;
            for (HashSet<GenericEntity> reachableSchools : schoolReachableFromEdOrg.values()) {
                if (reachableSchools.contains(s)) {
                    isOrphan = false;
                    break;
                }
            }
            if (isOrphan) {
                orphanSchools.add(s);
            }
        }
        return orphanSchools;
    }
    
    // Insert schools into the list under a "dummy" ed-org
    private static List<GenericEntity> insertSchoolsUnderDummyEdOrg(List<GenericEntity> retVal,
            Collection<GenericEntity> schools) {
        try {
            GenericEntity obj = new GenericEntity();
            obj.put(Constants.ATTR_NAME, DUMMY_EDORG_NAME);
            obj.put(Constants.ATTR_SCHOOLS, schools);
            retVal.add(obj);
        } catch (Exception e) {
            throw new RuntimeException("error creating json object for dummy edOrg");
        }
        return retVal;
    }
    
    /**
     * Override from UserEdOrgManager.
     * Signature is pre-defined by the architect.
     */
    public GenericEntity getUserInstHierarchy(String token, Object key, Data config) {
        List<GenericEntity> entities = getUserInstHierarchy(token);
        GenericEntity entity = new GenericEntity();
        //Dashboard expects return one GenericEntity.
        entity.put("root", entities);
        return entity;
    }
    
}

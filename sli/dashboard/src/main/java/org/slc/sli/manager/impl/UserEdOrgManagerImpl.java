package org.slc.sli.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.googlecode.ehcache.annotations.Cacheable;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

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
    
    private static final String USER_SCHOOLS = "user.schools";
    private CacheManager cacheManager;
    
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.cacheManager.addCache(USER_SCHOOLS);
    }
    
    
    private GenericEntity getParentEducationalOrganization(String token, GenericEntity edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganization(token, edOrgOrSchool);
    }
    private List<GenericEntity> getParentEducationalOrganizations(String token, List<GenericEntity> edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganizations(token, edOrgOrSchool);
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
        List<GenericEntity> schools = getSchools();
        
        if (schools != null && !schools.isEmpty()) {
            
            // read first school
            GenericEntity school = schools.get(0);
        
            // read parent organization
            GenericEntity parentEdOrg = getParentEducationalOrganization(getToken(), school);
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> metaData = (LinkedHashMap<String, Object>) parentEdOrg
                    .get(Constants.METADATA);
            if (metaData != null && !metaData.isEmpty()) {
                if (metaData.containsKey(Constants.EXTERNAL_ID)) {
                    return new EdOrgKey(metaData.get(Constants.EXTERNAL_ID).toString());
                }
            }
        }
        return null;
    }
    
    /**
     * Get user's schools. Cache the results so we don't have to make the call twice.
     * 
     * @return
     */
    public List<GenericEntity> getSchools() {
       
        List<GenericEntity> schools = null;
        
        // get it from the cache if you can
        if (cacheManager != null) {
            Element elem = cacheManager.getCache(USER_SCHOOLS).get(getToken());
            if (elem != null) {
                schools = (List<GenericEntity>) elem.getValue();
            }
        }
        
        // otherwise, call the api
        if (schools == null) {
            schools = getApiClient().getSchools(getToken(), null);
        
            // cache it
            if (schools != null) {
                if (cacheManager != null) {
                    cacheManager.getCache(USER_SCHOOLS).put(new Element(getToken(), schools));
                }
            }
        }
        
        return schools; 
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
        List<GenericEntity> schools = getSchools();
        if (schools == null) {
            return Collections.emptyList();
        }
        
        // This maps ids from educational organisations to schools reachable
        // from it via the "child" relationship
        Map<String, Set<GenericEntity>> schoolReachableFromEdOrg = new HashMap<String, Set<GenericEntity>>();
        
        // This just maps ed org ids to ed org objects.
        Map<String, GenericEntity> edOrgIdMap = new HashMap<String, GenericEntity>();
        
        for (GenericEntity school:schools) {
            String parentEdOrgId = (String) school.get(Constants.ATTR_PARENT_EDORG);
            if (parentEdOrgId != null) {
                if (!schoolReachableFromEdOrg.keySet().contains(parentEdOrgId)) {
                    schoolReachableFromEdOrg.put(parentEdOrgId, new HashSet<GenericEntity>());
                }
                schoolReachableFromEdOrg.get(parentEdOrgId).add(school);
            }
        }
        
        // traverse the ancestor chain from each school and find ed orgs that
        // the school is reachable from
        List<GenericEntity> edOrgs = getParentEducationalOrganizations(token, schools);
        while (!edOrgs.isEmpty()) {
            for (GenericEntity edOrg:edOrgs) {
                String parentEdOrgId = (String) edOrg.get(Constants.ATTR_PARENT_EDORG);
                String edOrgId = edOrg.getId();
                // insert ed-org id to - edOrg mapping
                edOrgIdMap.put(edOrgId, edOrg);
                
                //if parentedOrgId is not null, it means you are the top organization
                if (parentEdOrgId != null) {
                    
                    // insert ed-org - school mapping into the reverse map
                    if (!schoolReachableFromEdOrg.keySet().contains(parentEdOrgId)) {
                        schoolReachableFromEdOrg.put(parentEdOrgId, new HashSet<GenericEntity>());
                    }
                    Set<GenericEntity> reachableSchool = schoolReachableFromEdOrg.get(edOrgId);
                    if (reachableSchool != null)
                        schoolReachableFromEdOrg.get(parentEdOrgId).addAll(reachableSchool);
                }
            }
            edOrgs = getParentEducationalOrganizations(token, edOrgs); // next in the ancestor chain
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
            Map<String, Set<GenericEntity>> schoolReachableFromEdOrg) {
        Vector<GenericEntity> orphanSchools = new Vector<GenericEntity>();
        for (int i = 0; i < schools.size(); i++) {
            GenericEntity s = schools.get(i);
            boolean isOrphan = true;
            for (Set<GenericEntity> reachableSchools : schoolReachableFromEdOrg.values()) {
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

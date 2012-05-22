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

import org.slc.sli.entity.Config.Data;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.ApiClientManager;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.SecurityUtil;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 * 
 * @author syau
 * 
 */
public class UserEdOrgManagerImpl extends ApiClientManager implements UserEdOrgManager {
    
    private static final String USER_SCHOOLS_CACHE = "user.schools";
    private static final String USER_ED_ORG_CACHE = "user.district";
    private static final String USER_HIERARCHY_CACHE = "user.hierarchy";
    
    private GenericEntity getParentEducationalOrganization(String token, GenericEntity edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganization(token, edOrgOrSchool);
    }
    
    private List<GenericEntity> getParentEducationalOrganizations(String token, List<GenericEntity> edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganizations(token, edOrgOrSchool);
    }
    
    protected boolean isEducator() {
        return !SecurityUtil.isNotEducator();
    }
    
    /**
     * read token. Then, find district name associated with school.
     * 
     * @param token
     *            token-id, it is also using token as key value for cache.
     * @return District name
     */
    @Override
    public EdOrgKey getUserEdOrg(String token) {
        EdOrgKey edOrgKey = getFromCache(USER_ED_ORG_CACHE, token);
        if (edOrgKey != null) {
            return edOrgKey;
        }
        
        GenericEntity edOrg = null;
        
        // For state-level ed-org - need to take default config, so keep state
        // ed org
        if (!isEducator()) {
            
            GenericEntity staff = getApiClient().getStaffInfo(token);
            if (staff != null) {
                
                GenericEntity staffEdOrg = (GenericEntity) staff.get(Constants.ATTR_ED_ORG);
                if (staffEdOrg != null) {
                    
                    @SuppressWarnings("unchecked")
                    List<String> edOrgCategories = (List<String>) staffEdOrg.get(Constants.ATTR_ORG_CATEGORIES);
                    if (edOrgCategories != null && !edOrgCategories.isEmpty()) {
                        
                        for (String edOrgCategory : edOrgCategories) {
                            if (edOrgCategory.equals(Constants.STATE_EDUCATION_AGENCY)) {
                                edOrg = staffEdOrg;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // otherwise get school's parent ed-org
        if (edOrg == null) {
            
            // get list of school
            List<GenericEntity> schools = getSchools(token);
            
            if (schools != null && !schools.isEmpty()) {
                
                // read first school
                GenericEntity school = schools.get(0);
                
                // read parent organization
                edOrg = getParentEducationalOrganization(getToken(), school);
                if (edOrg == null) {
                    throw new DashboardException(
                            "No data is available for you to view. Please contact your IT administrator.");
                }
            }
        }
        
        // create ed-org key and save to cache
        if (edOrg != null) {
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> metaData = (LinkedHashMap<String, Object>) edOrg.get(Constants.METADATA);
            if (metaData != null && !metaData.isEmpty()) {
                if (metaData.containsKey(Constants.EXTERNAL_ID)) {
                    edOrgKey = new EdOrgKey(metaData.get(Constants.EXTERNAL_ID).toString(), edOrg.getId());
                    putToCache(USER_ED_ORG_CACHE, token, edOrgKey);
                    return edOrgKey;
                }
            }
        }
        return null;
    }
    
    /**
     * Get user's schools. Cache the results so we don't have to make the call
     * twice.
     * 
     * @return
     */
    private List<GenericEntity> getSchools(String token) {
        
        List<GenericEntity> schools = getFromCache(USER_SCHOOLS_CACHE, token);
        
        // otherwise, call the api
        if (schools == null) {
            schools = getApiClient().getSchools(token, null);
            
            // cache it
            putToCache(USER_SCHOOLS_CACHE, token, schools);
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
    @Override
    public List<GenericEntity> getUserInstHierarchy(String token) {
        List<GenericEntity> hierarchy = getFromCache(USER_HIERARCHY_CACHE, token);
        if (hierarchy != null) {
            return hierarchy;
        }
        // Find all the schools first.
        List<GenericEntity> schools = getSchools(token);
        if (schools == null) {
            return Collections.emptyList();
        }
        
        // This maps ids from educational organisations to schools reachable
        // from it via the "child" relationship
        Map<String, Set<GenericEntity>> schoolReachableFromEdOrg = new HashMap<String, Set<GenericEntity>>();
        
        // This just maps ed org ids to ed org objects.
        Map<String, GenericEntity> edOrgIdMap = new HashMap<String, GenericEntity>();
        
        for (GenericEntity school : schools) {
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
            for (GenericEntity edOrg : edOrgs) {
                String parentEdOrgId = (String) edOrg.get(Constants.ATTR_PARENT_EDORG);
                String edOrgId = edOrg.getId();
                
                // if parentEdOrgId exists, you are not the top organization
                if (parentEdOrgId != null) {
                    
                    // insert ed-org id to - edOrg mapping
                    edOrgIdMap.put(edOrgId, edOrg);
                    
                    // insert ed-org - school mapping into the reverse map
                    if (!schoolReachableFromEdOrg.keySet().contains(parentEdOrgId)) {
                        schoolReachableFromEdOrg.put(parentEdOrgId, new HashSet<GenericEntity>());
                    }
                    Set<GenericEntity> reachableSchool = schoolReachableFromEdOrg.get(edOrgId);
                    if (reachableSchool != null) {
                        schoolReachableFromEdOrg.get(parentEdOrgId).addAll(reachableSchool);
                    }
                }
            }
            // next in the ancestor chain
            edOrgs = getParentEducationalOrganizations(token, edOrgs);
        }
        
        // build result list
        List<GenericEntity> retVal = new ArrayList<GenericEntity>();
        for (String edOrgId : schoolReachableFromEdOrg.keySet()) {
            GenericEntity obj = new GenericEntity();
            try {
                GenericEntity edOrgEntity = edOrgIdMap.get(edOrgId);
                // if edOrgEntity is null, it may be API could not return entity
                // because of error
                // code 403.
                if (edOrgEntity != null) {
                    obj.put(Constants.ATTR_NAME, edOrgIdMap.get(edOrgId).get(Constants.ATTR_NAME_OF_INST));
                    // convert school ids to the school object array
                    Set<GenericEntity> reachableSchools = schoolReachableFromEdOrg.get(edOrgId);
                    obj.put(Constants.ATTR_SCHOOLS, reachableSchools);
                    retVal.add(obj);
                }
            } catch (Exception e) {
                throw new RuntimeException("error creating json object for " + edOrgId);
            }
        }
        
        Collection<GenericEntity> orphanSchools = findOrphanSchools(schools, schoolReachableFromEdOrg);
        // Temporary: insert a dummy edorg for all orphan schools.
        if (!orphanSchools.isEmpty()) {
            insertSchoolsUnderDummyEdOrg(retVal, orphanSchools);
        }
        putToCache(USER_HIERARCHY_CACHE, token, retVal);
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
    @SuppressWarnings("unchecked")
    @Override
    public GenericEntity getUserInstHierarchy(String token, Object key, Data config) {
        List<GenericEntity> entities = getUserInstHierarchy(token);
        GenericEntity entity = new GenericEntity();
        // Dashboard expects return one GenericEntity.
        entity.put(Constants.ATTR_ROOT, entities);
        if (key != null) {
            // TODO: a better way of searching should be implemented.
            for (GenericEntity org : entities) {
                HashSet schools = ((HashSet) org.get(Constants.ATTR_SCHOOLS));
                for (Object school : schools) {
                    for (Object course : ((GenericEntity) school).getList(Constants.ATTR_COURSES)) {
                        for (Object section : ((GenericEntity) course).getList(Constants.ATTR_SECTIONS)) {
                            if (((GenericEntity) section).getId().equals(key)) {
                                GenericEntity selectedOrg = new GenericEntity();
                                selectedOrg.put(Constants.ATTR_NAME, org.get(Constants.ATTR_NAME));
                                selectedOrg.put(Constants.ATTR_SECTION, section);
                                entity.put(Constants.ATTR_SELECTED_POPULATION, selectedOrg);
                                return entity;
                            }
                        }
                    }
                }
            }
        }
        return entity;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public GenericEntity getStaffInfo(String token) {
        GenericEntity staffEntity = getApiClient().getStaffInfo(token);
        if (staffEntity == null) {
            staffEntity = new GenericEntity();
        }
        
        // temporary Generic Entity Element to indicate he/she is district level user or not
        staffEntity.put(Constants.LOCAL_EDUCATION_AGENCY, false);
        GenericEntity edOrg = (GenericEntity) staffEntity.get(Constants.ATTR_ED_ORG);
        if (edOrg != null) {
            List<String> organizationCategories = (List<String>) edOrg.get(Constants.ATTR_ORG_CATEGORIES);
            if (organizationCategories != null && !organizationCategories.isEmpty()) {
                for (String educationAgency : organizationCategories) {
                    if (educationAgency != null && educationAgency.equals(Constants.LOCAL_EDUCATION_AGENCY)) {
                        staffEntity.put(Constants.LOCAL_EDUCATION_AGENCY, true);
                        break;
                    }
                }
            }
        }
        return staffEntity;
    }
}

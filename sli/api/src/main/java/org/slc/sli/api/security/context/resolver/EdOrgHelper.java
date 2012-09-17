package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Contains helper methods for traversing the edorg hierarchy.
 * 
 * Assumptions it makes
 * 
 * <ul>
 * <li>SEAs, LEAs, and Schools are all edorgs with organizationCategories of 'State Education Agency'
 * 'Local Education Agency', and 'School' respectively.</li>
 * <li>The parentEducationAgencyReference of a school always points to an LEA</li>
 * <li>The parentEducationAgencyReference of an LEA can point to either an SEA or another LEA</li>
 * <li>SEAs don't have a parentEducationAgencyReference and therefore are always at the top of the tree</li>
 * </ul>
 * 
 *
 */
@Component
public class EdOrgHelper {
    
    @Autowired
    protected PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private AssociativeContextHelper helper;
    
    
    /**
     * Traverse the edorg hierarchy and find all the SEAs the user is associated with, directly or indirectly.
     * @param user
     * @return a list of entity IDs
     */
    public List<String> getSEAs(Entity user) {
        List<String> directAssoc = getDirectEdOrgAssociations(user);
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("_id", "in", directAssoc, false));
        
        Set<String> entities = new HashSet<String>();
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            entities.add(getSEAOfEdOrg(entity));
        }
        
        return new ArrayList<String>(entities);
    }
    
    /**
     * Determine the district of the user.
     * 
     * If the user is directly associated with an SEA, this is any LEA directly below the SEA.
     * If the user is directly associated with an LEA, this is the top-most LEA
     * i.e. the LEA directly associated with the SEA.
     * 
     * @param user
     * @return a list of entity IDs
     */
    public List<String> getDistricts(Entity user) {
        List<String> directAssoc = getDirectEdOrgAssociations(user);
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("_id", "in", directAssoc, false));
        
        Set<String> entities = new HashSet<String>();
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (isLEA(entity)) {
                entities.add(getTopLEAOfEdOrg(entity).getEntityId());
            } else if (isSchool(entity)) {
                entities.add(getTopLEAOfEdOrg(entity).getEntityId());
            } else {    //isSEA
                entities.addAll(getChildLEAsOfEdOrg(entity));
            }
        }
        
        return new ArrayList<String>(entities);
    }
    
    public List<String> getChildLEAsOfEdOrg(Entity edorgEntity) {
        List<String> toReturn = new ArrayList<String>();
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("parentEducationAgencyReference", "=", edorgEntity.getEntityId()));
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (isLEA(entity)) {
                toReturn.add(entity.getEntityId());

            }
        }
        
        return toReturn;
    }
    
    /**
     * Get an ordered list of the parents of an edorg.
     * 
     * The order of the list starts with the direct parent of the edorg
     * and ends with the SEA
     * 
     * @param edOrg
     * @return
     */
    public List<String> getParentEdOrgs(Entity edOrg) {
        List<String> toReturn = new ArrayList<String>();
        Entity curEdOrg = edOrg;
        while (curEdOrg.getBody().get("parentEducationAgencyReference") != null) {
            String parentId = (String) curEdOrg.getBody().get("parentEducationAgencyReference");
            toReturn.add(parentId);
            curEdOrg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, parentId);
        }
        return toReturn;
    }
    
    public List<String> getDirectEdOrgAssociations(Entity principal) {
        List<String> ids = new ArrayList<String>();
        if (isTeacher(principal)) {
            ids.addAll(helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS)));
        } else {
            ids.addAll(helper.findAccessible(principal, Arrays.asList(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)));
        }
        return ids;
    }
    
    private Entity getTopLEAOfEdOrg(Entity entity) {
        Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, (String) entity.getBody().get("parentEducationAgencyReference"));
        if (isLEA(parentEdorg)) {
            return getTopLEAOfEdOrg(parentEdorg);
        } else { //sea
            return entity;
        }
    }
    

    private String getSEAOfEdOrg(Entity entity) {
        if (isSEA(entity)) {
            return entity.getEntityId();
        } else {
            Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, (String) entity.getBody().get("parentEducationAgencyReference"));
            if  (parentEdorg != null) {
                return getSEAOfEdOrg(parentEdorg);
            } else {
                warn("EdOrg {} is missing parent SEA", entity.getEntityId());
                return null;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean isLEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("Local Education Agency"))
            return true;
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public boolean isSEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("State Education Agency"))
            return true;
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public boolean isSchool(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("School"))
            return true;
        return false;
    }

    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }

}

package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("validationRepo")
    private Repository<Entity> repo;
    
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
     * Traverse the edorg hierarchy and find all LEAs the user is associated with, directly or indirectly.
     * 
     * @param user
     * @return a list of entity IDs
     */
    public List<String> getLEAs(Entity user) {
        List<String> directAssoc = getDirectEdOrgAssociations(user);
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "in", directAssoc, false));
        
        Set<String> entities = new HashSet<String>();
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (isLEA(entity)) {
                entities.add(entity.getEntityId());
                entities.addAll(getParentLEAsOfEdOrg(entity));
                entities.addAll(getChildLEAsOfEdOrg(entity));
            } else if (isSchool(entity)) {
                entities.addAll(getParentLEAsOfEdOrg(entity));
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
                toReturn.addAll(getChildLEAsOfEdOrg(entity));
            }
        }
        
        return toReturn;
    }
    
    private List<String> getDirectEdOrgAssociations(Entity principal) {
        List<String> ids = new ArrayList<String>();
        if (isTeacher(principal)) {
            ids.addAll(helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS)));
        } else {
            ids.addAll(helper.findAccessible(principal, Arrays.asList(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)));
        }
        return ids;
    }
    
    private List<String> getParentLEAsOfEdOrg(Entity entity) {
        List<String> toReturn = new ArrayList<String>();
        Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, (String) entity.getBody().get("parentEducationAgencyReference"));
        if (isLEA(parentEdorg)) {
            toReturn.add(parentEdorg.getEntityId());
            toReturn.addAll(getParentLEAsOfEdOrg(parentEdorg));
        }
        return toReturn;
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
    private boolean isLEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("Local Education Agency"))
            return true;
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private boolean isSEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("State Education Agency"))
            return true;
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private boolean isSchool(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("School"))
            return true;
        return false;
    }

    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }

}

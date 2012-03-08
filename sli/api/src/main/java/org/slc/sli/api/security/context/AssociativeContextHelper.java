package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Resolves Context based permissions.
 * Determines if an associative path exists between a source and target entity.
 */
@Component
public class AssociativeContextHelper {
    
    @Autowired
    private EntityRepository repository;
    
    @Autowired
    private EntityDefinitionStore definitions;
    
    /**
     * Provides list of entity ids that given actor has access to
     * 
     * @param principal user currently accessing the system
     * @return List of string ids
     */
    public List<String> findAccessible(Entity principal, List<String> associativeNames) {
        
        List<AssociationDefinition> associativeContextPath = getDefinitions(associativeNames);
        
        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));
        String searchType = principal.getType();
        for (AssociationDefinition ad : associativeContextPath) {
            List<String> keys = getAssocKeys(searchType, ad);
            String sourceKey = keys.get(0);
            String targetKey = keys.get(1);
            Iterable<Entity> entities = this.repository.findByQuery(ad.getStoredCollectionName(), new Query(Criteria.where("body." + sourceKey).in(ids)), 0, 9999);
            
            ids.clear();
            for (Entity e : entities) {
                ids.add((String) e.getBody().get(targetKey));
            }
            searchType = getTargetType(searchType, ad);
        }
        
        return ids;
    }
    
    private List<AssociationDefinition> getDefinitions(List<String> associativeNames) {
        List<AssociationDefinition> adl = new ArrayList<AssociationDefinition>();
        
        for (String name : associativeNames) {
            adl.add((AssociationDefinition) this.definitions.lookupByResourceName(name));
        }
        
        return adl;
    }
    
    private String getTargetType(String searchEntityType, AssociationDefinition ad) {
        if (ad.getSourceEntity().getType().equals(searchEntityType)) {
            return ad.getTargetEntity().getType();
        } else if (ad.getTargetEntity().getType().equals(searchEntityType)) {
            return ad.getSourceEntity().getType();
        } else {
            throw new IllegalArgumentException("Entity is not a member of association " + searchEntityType + " " + ad.getType());
        }
        
    }
    
    private List<String> getAssocKeys(String entityType, AssociationDefinition ad) {
        
        if (ad.getSourceEntity().getType().equals(entityType)) {
            return Arrays.asList(ad.getSourceKey(), ad.getTargetKey());
        } else if (ad.getTargetEntity().getType().equals(entityType)) {
            return Arrays.asList(ad.getTargetKey(), ad.getSourceKey());
        } else {
            throw new IllegalArgumentException("Entity is not a member of association " + entityType + " " + ad.getType());
        }
    }
}

package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;

/**
 * Resolves Context based permissions.
 * Determines if an associative path exists between a source and target entity.
 */
@Component
public class AssociativeContextResolver implements EntityContextResolver {
    
    private EntityRepository            repository;
    
    private String                      sourceType;
    private String                      targetType;
    private List<AssociationDefinition> associativeContextPath;
    
    /**
     * Provides list of entity ids that given actor has access to
     * 
     * @param principal user currently accessing the system
     * @return List of string ids
     */
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));
        String searchType = principal.getType();
        for (AssociationDefinition ad : this.associativeContextPath) {
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
    
    @Override
    public boolean hasPermission(Entity principal, Entity resource) {
        return true;  // TODO stub. traverse associative context path
    }
    
    @Override
    public String getSourceType() {
        return sourceType;
    }
    
    @Override
    public String getTargetType() {
        return targetType;
    }
    
    public void setSourceType(String type) {
        this.sourceType = type;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public void setAssociativeContextPath(List<AssociationDefinition> associativeContextPath) {
        this.associativeContextPath = associativeContextPath;
    }
    
    public void setRepository(EntityRepository repository) {
        this.repository = repository;
    }
}

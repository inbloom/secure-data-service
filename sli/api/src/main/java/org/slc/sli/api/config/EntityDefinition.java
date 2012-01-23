package org.slc.sli.api.config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.api.service.EntityService;
import org.slc.sli.dal.repository.EntityRepository;

/**
 * Definition of an entity resource
 * 
 * @author nbrown
 * 
 */
public class EntityDefinition {
    
    private final String type;
    private final String resourceName;
    private final EntityService service;
    private final String collectionName;
    private final List<AssociationDefinition> linkedAssociations;
    private static EntityRepository defaultRepo;
    
    protected EntityDefinition(String type, String resourceName, String collectionName, EntityService service) {
        this.type = type;
        this.resourceName = resourceName;
        this.collectionName = collectionName;
        this.service = service;
        this.linkedAssociations = new LinkedList<AssociationDefinition>();
    }
    
    public final void addLinkedAssoc(AssociationDefinition assocDefn) {
        this.linkedAssociations.add(assocDefn);
    }
    
    public final Collection<AssociationDefinition> getLinkedAssoc() {
        return this.linkedAssociations;
    }
    
    public String getStoredCollectionName() {
        return this.collectionName;
    }
    
    public String getType() {
        return type;
    }
    
    /**
     * The name of the resource name in the ReST URI
     * 
     * @return
     */
    public String getResourceName() {
        return resourceName;
    }
    
    public EntityService getService() {
        return service;
    }
    
    public boolean isOfType(String id) {
        return service.exists(id);
    }
    
    public static void setDefaultRepo(EntityRepository defaultRepo) {
        EntityDefinition.defaultRepo = defaultRepo;
    }
    
    public static EntityRepository getDefaultRepo() {
        return defaultRepo;
    }
    
}

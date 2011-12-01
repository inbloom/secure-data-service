package org.slc.sli.api.service;

/**
 * Definition of an entity resource
 * 
 * @author nbrown
 * 
 */
public class EntityDefinition {
    private final String collectionName;
    private final String resourceName;
    
    public EntityDefinition(String collectionName, String resourceName) {
        super();
        this.collectionName = collectionName;
        this.resourceName = resourceName;
    }
    
    public EntityDefinition(String entityName) {
        this(entityName, entityName);
    }
    
    /**
     * The name of the entity's db collection
     * 
     * @return
     */
    public String getCollectionName() {
        return collectionName;
    }
    
    /**
     * The name of the resource name in the ReST URI
     * 
     * @return
     */
    public String getResourceName() {
        return resourceName;
    }
    
}

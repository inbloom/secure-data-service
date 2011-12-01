package org.slc.sli.api.service;

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
    
    public String getCollectionName() {
        return collectionName;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
}

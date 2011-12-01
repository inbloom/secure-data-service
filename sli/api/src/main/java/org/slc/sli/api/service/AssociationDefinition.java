package org.slc.sli.api.service;

/**
 * Definition of an association resource
 * 
 * @author nbrown
 * 
 */
public class AssociationDefinition extends EntityDefinition {
    private final EntityDefinition sourceEntity;
    private final EntityDefinition targetEntity;
    
    public AssociationDefinition(String collectionName, String resourceName, EntityDefinition sourceEntity,
            EntityDefinition targetEntity) {
        super(collectionName, resourceName);
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
    }
    
    public AssociationDefinition(String assocName, EntityDefinition sourceEntity, EntityDefinition targetEntity) {
        this(assocName, assocName, sourceEntity, targetEntity);
    }
    
    /**
     * The source of the association
     * 
     * @return
     */
    public EntityDefinition getSourceEntity() {
        return sourceEntity;
    }
    
    /**
     * The target of the association
     * 
     * @return
     */
    public EntityDefinition getTargetEntity() {
        return targetEntity;
    }
    
}

package org.slc.sli.api.config;

import java.util.Collection;

/**
 * Store to look up entity definition information
 * 
 * @author nbrown
 * 
 */
public interface EntityDefinitionStore {
    
    /**
     * Find an entity definition based on the resource name in the URI
     * 
     * @param resourceName
     *            the resource name (from the URI)
     * @return the definition of the entity
     */
    public EntityDefinition lookupByResourceName(String resourceName);

    /**
     * Find an entity definition based on the entity type
     * 
     * @param entityType
     *            the entity type
     * @return the definition of the entity
     */
    public EntityDefinition lookupByEntityType(String entityType);
    
    /**
     * Gets the collection of association definitions that are linked to the given definition
     * 
     * @param defn
     *            the definition to look up
     * @return the linked entity definitions
     */
    public Collection<AssociationDefinition> getLinked(EntityDefinition defn);
    
    public void init();
    
}

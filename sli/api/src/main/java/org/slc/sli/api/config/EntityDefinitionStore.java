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
     * Gets the collection of entity definitions that are linked to the given definition. With the
     * current model, if the definition is an entity object, the results will be its associations.
     * If the definition is an association, the results will be the target and source of that
     * association.
     * 
     * @param defn
     *            the definition to look up
     * @return the linked entity definitions
     */
    public Collection<EntityDefinition> getLinked(EntityDefinition defn);
    
}

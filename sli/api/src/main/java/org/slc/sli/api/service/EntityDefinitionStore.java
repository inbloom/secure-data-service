package org.slc.sli.api.service;

import java.util.Collection;
import java.util.List;

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
    
    /**
     * Returns the list of validators that should be applied to new (or modified entities of the
     * given type). They should ideally be applied in the given order
     * 
     * @param defn
     *            the definition to look up validators for
     * @return a list of validators that should be applied to the given entity definition
     */
    public List<Validator> getValidators(EntityDefinition defn);
    
    /**
     * Returns the list of treatments that should be applied to entities. They must be applied in
     * the given order
     * 
     * @param defn
     *            the definition to look up treatments for
     * @return a list of treatments that should be applied to the given entity definition
     */
    public List<Treatment> getTreatments(EntityDefinition defn);
    
    /**
     * Returns the list of implied filters that should be applied when quering entities
     * 
     * @param defn
     *            the definition to look up filters for
     * @return a list of filters that should be applied to the given entity definition
     */
    public List<Filter> getImpliedFilters(EntityDefinition defn);
}

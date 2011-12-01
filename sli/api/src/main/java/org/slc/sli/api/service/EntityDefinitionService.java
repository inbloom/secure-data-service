package org.slc.sli.api.service;

import java.util.Collection;
import java.util.List;

public interface EntityDefinitionService {
    
    public EntityDefinition lookupByResourceName(String resourceName);
    
    public Collection<EntityDefinition> getLinked(EntityDefinition defn);
    
    public List<Validator> getValidators(EntityDefinition defn);
    
    public List<Transformer> getTransformers(EntityDefinition defn);
}

package org.slc.sli.api.security.context;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.dal.repository.EntityRepository;

/**
 * Factory class for building AssociativeContextResolvers.
 */
@Component
public class ContextResolverFactory {
    
    @Autowired
    private EntityDefinitionStore definitionStore;
    
    @Autowired
    private EntityRepository repository;
    
    public AssociativeContextBuilder makeAssoc() {
        return new AssociativeContextBuilder();
    }
    
    /**
     * Builder pattern
     * 
     * @author mlane
     * 
     */
    public class AssociativeContextBuilder {
        private String source;
        private String target;
        private List<AssociationDefinition> associationPath = new ArrayList<AssociationDefinition>();
        private EntityDefinitionStore entityDefs;
        private EntityRepository repo;
        
        public AssociativeContextBuilder() {
            entityDefs = ContextResolverFactory.this.definitionStore;
            repo = ContextResolverFactory.this.repository;
        }
        
        public AssociativeContextBuilder setSource(String source) {
            this.source = source;
            return this;
        }
        
        public AssociativeContextBuilder setTarget(String target) {
            this.target = target;
            return this;
        }
        
        public AssociativeContextBuilder setAssociationPath(String... associationNames) {
            for (String assocName : associationNames) {
                EntityDefinition entityDefinition = entityDefs.lookupByResourceName(assocName);
                if (entityDefinition != null && entityDefinition instanceof AssociationDefinition) {
                    associationPath.add((AssociationDefinition) entityDefinition);
                } else {
                    throw new InvalidParameterException("cannot find associationType in definition store" + assocName);
                }
            }
            return this;
        }
        
        public AssociativeContextResolver build() {
            AssociativeContextResolver assocContext = new AssociativeContextResolver();
            assocContext.setSourceType(source);
            assocContext.setTargetType(target);
            assocContext.setAssociativeContextPath(associationPath);
            assocContext.setRepository(repo);
            return assocContext;
        }
    }
}
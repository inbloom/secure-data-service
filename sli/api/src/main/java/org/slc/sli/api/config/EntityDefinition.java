package org.slc.sli.api.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.api.service.BasicService;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.validation.EntityValidator;

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
    private static final List<Treatment> GLOBAL_TREATMENTS = new LinkedList<Treatment>();
    
    protected EntityDefinition(String type, String resourceName, String collectionName, EntityService service) {
        super();
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
    
    /**
     * Adds a global treatment to be applied to every entity definition. Note that this has to be
     * called prior to defining any entity definitions
     * 
     * @param treatment
     */
    public static void addGlobalTreatment(Treatment treatment) {
        GLOBAL_TREATMENTS.add(treatment);
    }
    
    /**
     * Create a builder for an entity definition with the same collection and resource name as the
     * type.
     * 
     * TODO: Remove validator param when class is spring managed
     * 
     * @param entityName
     *            the collection/resource name
     * @param validator
     *            The validator to use.
     * @return the builder to create the entity definition
     */
    public static Builder makeEntity(String entityName, EntityValidator validator) {
        return new Builder(entityName, validator);
    }
    
    /**
     * Fluent builder for EntityDefinition.
     */
    public static class Builder {
        private String type;
        private String collectionName;
        private String resourceName;
        private List<Treatment> treatments = new LinkedList<Treatment>(GLOBAL_TREATMENTS);
        private EntityRepository repo;
        private EntityValidator validator;
        
        /**
         * Create a builder for an entity definition
         * 
         * @param collectionName
         *            the name of the entity in the db
         * @param resourceName
         *            the name of the entity in the ReST uri
         */
        Builder(String type, EntityValidator validator) {
            this.type = type;
            this.collectionName = type;
            this.resourceName = type;
            this.repo = getDefaultRepo();
            this.validator = validator;
        }
        
        /**
         * Add a list of treatments to the definition
         * 
         * @param treatments
         *            the list of treatments
         * @return the builder
         */
        public Builder withTreatments(Treatment... treatments) {
            this.treatments.addAll(Arrays.asList(treatments));
            return this;
        }
        
        /**
         * Sets the collection to store the entities in
         * 
         * @param collectionName
         *            the name of the collection to store the entities in, if different from the
         *            entity name
         * @return the builder
         */
        public Builder storeAs(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }
        
        /**
         * TODO
         * 
         * @param repo
         * @return
         */
        public Builder storeIn(EntityRepository repo) {
            this.repo = repo;
            return this;
        }
        
        /**
         * Sets the name of the ReSTful resource to expose the resource as, if different from the
         * entity name
         * 
         * @param resourceName
         *            the name of the ReSTful resource to expose the resource as
         * @return the builder
         */
        public Builder exposeAs(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }
        
        protected String getType() {
            return type;
        }
        
        protected String getCollectionName() {
            return collectionName;
        }
        
        protected String getResourceName() {
            return resourceName;
        }
        
        protected List<Treatment> getTreatments() {
            return treatments;
        }
        
        protected EntityRepository getRepo() {
            return repo;
        }
        
        protected EntityValidator getValidator() {
            return validator;
        }
        
        /**
         * Create the actual entity definition
         * 
         * @return the entity definition
         */
        public EntityDefinition build() {
            
            BasicService entityService = new BasicService(collectionName, treatments, repo, validator);
            EntityDefinition entityDefinition = new EntityDefinition(type, resourceName, collectionName, entityService);
            entityService.setDefn(entityDefinition);
            return entityDefinition;
        }
        
    }
}

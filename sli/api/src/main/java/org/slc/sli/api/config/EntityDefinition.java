package org.slc.sli.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.api.service.BasicService;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.api.service.Validator;
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
    private static EntityRepository defaultRepo;
    private final static List<Treatment> globalTreatments = new LinkedList<Treatment>();
    
    protected EntityDefinition(String type, String resourceName, EntityService service) {
        super();
        this.type = type;
        this.resourceName = resourceName;
        this.service = service;
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
        globalTreatments.add(treatment);
    }
    
    /**
     * Create a builder for an entity definition with the same collection and resource name as the
     * type
     * 
     * @param entityName
     *            the collection/resource name
     * @return the builder to create the entity definition
     */
    public static Builder makeEntity(String entityName) {
        return new Builder(entityName);
    }
    
    public static class Builder {
        private String type;
        private String collectionName;
        private String resourceName;
        private List<Treatment> treatments = new ArrayList<Treatment>(globalTreatments);
        private List<Validator> validators = new ArrayList<Validator>();
        private EntityRepository repo;
        
        /**
         * Create a builder for an entity definition
         * 
         * @param collectionName
         *            the name of the entity in the db
         * @param resourceName
         *            the name of the entity in the ReST uri
         */
        Builder(String type) {
            this.type = type;
            this.collectionName = type;
            this.resourceName = type;
            this.repo = getDefaultRepo();
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
         * Add a list of validators to the definition
         * 
         * @param validators
         *            the list of validators
         * @return the builder
         */
        public Builder withValidators(Validator... validators) {
            this.validators.addAll(Arrays.asList(validators));
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
        
        protected List<Validator> getValidators() {
            return validators;
        }
        
        protected EntityRepository getRepo() {
            return repo;
        }
        
        /**
         * Create the actual entity definition
         * 
         * @return the entity definition
         */
        public EntityDefinition build() {
            BasicService entityService = new BasicService(collectionName, treatments, validators, repo);
            EntityDefinition entityDefinition = new EntityDefinition(type, resourceName, entityService);
            entityService.setDefinition(entityDefinition);
            return entityDefinition;
        }
        
    }
}

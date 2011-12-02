package org.slc.sli.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slc.sli.api.service.BasicService;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.api.service.Validator;

/**
 * Definition of an entity resource
 * 
 * @author nbrown
 * 
 */
public class EntityDefinition {
    private final String type;
    private final String collectionName;
    private final String resourceName;
    private final List<Treatment> treatments;
    private final List<Validator> validators;
    private final EntityService service;
    
    public EntityDefinition(String type, String collectionName, String resourceName, List<Treatment> treatments,
            List<Validator> validators) {
        super();
        this.type = type;
        this.collectionName = collectionName;
        this.resourceName = resourceName;
        this.treatments = treatments;
        this.validators = validators;
        this.service = new BasicService(collectionName, treatments, validators);
    }
    
    public String getType() {
        return type;
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
    
    public EntityService getService() {
        return service;
    }
    
    /**
     * Returns the list of treatments that should be applied to entities. They must be applied in
     * the given order
     * 
     * @return a list of treatments that should be applied to the given entity definition
     */
    public List<Treatment> getTreatments() {
        return treatments;
    }
    
    /**
     * Returns the list of validators that should be applied to new (or modified entities of the
     * given type). They should ideally be applied in the given order
     * 
     * @return a list of validators that should be applied to the given entity definition
     */
    public List<Validator> getValidators() {
        return validators;
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
        private List<Treatment> treatments = new ArrayList<Treatment>();
        private List<Validator> validators = new ArrayList<Validator>();
        
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
        public Builder storeIn(String collectionName) {
            this.collectionName = collectionName;
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
        
        /**
         * Create the actual entity definition
         * 
         * @return the entity definition
         */
        public EntityDefinition build() {
            return new EntityDefinition(type, collectionName, resourceName, treatments, validators);
        }
    }
}

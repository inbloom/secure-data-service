package org.slc.sli.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.slc.sli.api.service.BasicAssocService;
import org.slc.sli.api.service.BasicService;
import org.slc.sli.api.service.CoreBasicService;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.dal.repository.EntityRepository;

/**
 * Factory class for building Entity and Association definition objects.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class DefinitionFactory {
    
    @Autowired
    EntityRepository defaultRepo;
    
    @Autowired
    IdTreatment idTreatment;
    
    @Autowired
    ApplicationContext beanFactory;
    
    public EntityBuilder makeEntity(String type) {
        return new EntityBuilder(type);
    }
    
    public AssocBuilder makeAssoc(String type) {
        return new AssocBuilder(type);
    }
    
    /**
     * Fluent builder for EntityDefinitions.
     * This class is not static; it requires fields from it's parent factory to build the EntityDef.
     * 
     * @author Nick Brown <nbrown@wgen.net>
     * @author Ryan Farris <rfarris@wgen.net>
     * 
     */
    public class EntityBuilder {
        protected String type;
        protected String collectionName;
        protected String resourceName;
        protected List<Treatment> treatments = new ArrayList<Treatment>();
        protected EntityRepository repo;
        
        /**
         * Create a builder for an entity definition
         * 
         * @param collectionName
         *            the name of the entity in the db
         * @param resourceName
         *            the name of the entity in the ReST uri
         */
        EntityBuilder(String type) {
            this.type = type;
            this.collectionName = type;
            this.resourceName = type;
            this.repo = DefinitionFactory.this.defaultRepo;
            this.treatments.add(DefinitionFactory.this.idTreatment);
        }
        
        /**
         * Add a list of treatments to the definition
         * 
         * @param treatments
         *            the list of treatments
         * @return the builder
         */
        public EntityBuilder withTreatments(Treatment... treatments) {
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
        public EntityBuilder storeAs(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }
        
        /**
         * Set the repository the service layer will use to get/update/delete/query entities
         * 
         * @param repo
         * @return
         */
        public EntityBuilder storeIn(EntityRepository repo) {
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
        public EntityBuilder exposeAs(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }
        
        /**
         * Create the actual entity definition
         * 
         * @return the entity definition
         */
        public EntityDefinition build() {
            CoreBasicService coreService = (CoreBasicService) DefinitionFactory.this.beanFactory.getBean(
                    "coreBasicService", collectionName);
            
            BasicService entityService = (BasicService) DefinitionFactory.this.beanFactory.getBean("basicService",
                    collectionName, treatments, coreService);
            
            EntityDefinition entityDefinition = new EntityDefinition(type, resourceName, collectionName, entityService);
            entityService.setDefn(entityDefinition);
            return entityDefinition;
        }
    }
    
    /**
     * Fluent builder for AssocBuilder.
     */
    public class AssocBuilder extends EntityBuilder {
        private EntityInfo source;
        private EntityInfo target;
        private String relNameFromSource;
        private String relNameFromTarget;
        
        /**
         * Create a builder for an association definition
         * 
         * @param collectionName
         *            the name of the association in the db
         * @param resourceName
         *            the name of the association in the ReST uri
         */
        AssocBuilder(String type) {
            super(type);
        }
        
        /**
         * Sets the source definition. The link will get name get{type} and the id will be {type}Id
         * 
         * @param source
         *            the source of the association
         * @return the builder
         */
        public AssocBuilder from(EntityDefinition source) {
            this.source = new EntityInfo(source, "get" + StringUtils.capitalize(source.getType()), source.getType()
                    + "Id");
            return this;
        }
        
        /**
         * Sets the target definition. The link will get name get{Type} and the id will be {type}Id
         * 
         * @param target
         *            the target definition
         * @return the builder
         */
        public AssocBuilder to(EntityDefinition target) {
            this.target = new EntityInfo(target, "get" + StringUtils.capitalize(target.getType()), target.getType()
                    + "Id");
            return this;
        }
        
        /**
         * Sets the source definition
         * 
         * @param source
         *            the source of the association
         * @param sourceLink
         *            the name of the link
         * @param hopLink
         *            the name of the link from the target to the source
         * @return the builder
         */
        public AssocBuilder from(EntityDefinition source, String sourceLink, String hopLink) {
            this.source = new EntityInfo(source, sourceLink, hopLink, source.getType() + "Id");
            return this;
        }
        
        /**
         * Sets the source definition
         * 
         * @param source
         *            the source of the association
         * @param sourceLink
         *            the name of the link
         * @param hopLink
         *            the name of the link from the target to the source
         * @param sourceKey
         *            the key to look up the source with
         * @return the builder
         */
        public AssocBuilder from(EntityDefinition source, String sourceLink, String hopLink, String sourceKey) {
            this.source = new EntityInfo(source, sourceLink, hopLink, sourceKey);
            return this;
        }
        
        /**
         * Sets the target definition
         * 
         * @param target
         *            the target definition
         * @param targetLink
         *            the name of the link
         * @param hopLink
         *            the name of the link from the source to the target
         * @return the builder
         */
        public AssocBuilder to(EntityDefinition target, String targetLink, String hopLink) {
            this.target = new EntityInfo(target, targetLink, hopLink, target.getType() + "Id");
            return this;
        }
        
        /**
         * Sets the target definition
         * 
         * @param target
         *            the target definition
         * @param targetLink
         *            the name of the link
         * @param hopLink
         *            the name of the link from the source to the target
         * @param targetKey
         *            the key to look up the target with
         * @return the builder
         */
        public AssocBuilder to(EntityDefinition target, String targetLink, String hopLink, String targetKey) {
            this.target = new EntityInfo(target, targetLink, hopLink, targetKey);
            return this;
        }
        
        /**
         * The name of the link from either entity to the relationship
         * 
         * @param relName
         * @return
         */
        public AssocBuilder called(String relName) {
            this.relNameFromSource = relName;
            this.relNameFromTarget = relName;
            return this;
        }
        
        /**
         * The name of the link on the source entity to the relationship
         * 
         * @param relName
         * @return
         */
        public AssocBuilder calledFromSource(String relName) {
            this.relNameFromSource = relName;
            return this;
        }
        
        /**
         * The name of the link of the target entity of the relationship
         * 
         * @param relName
         * @return
         */
        public AssocBuilder calledFromTarget(String relName) {
            this.relNameFromTarget = relName;
            return this;
        }
        
        @Override
        public AssocBuilder withTreatments(Treatment... treatments) {
            super.withTreatments(treatments);
            return this;
        }
        
        @Override
        public AssocBuilder storeAs(String collectionName) {
            super.storeAs(collectionName);
            return this;
        }
        
        @Override
        public AssocBuilder storeIn(EntityRepository repo) {
            super.storeIn(repo);
            return this;
        }
        
        @Override
        public AssocBuilder exposeAs(String resourceName) {
            super.exposeAs(resourceName);
            return this;
        }
        
        @Override
        public AssociationDefinition build() {
            CoreBasicService coreService = (CoreBasicService) DefinitionFactory.this.beanFactory.getBean(
                    "coreBasicService", collectionName);
            // (String collectionName, List<Treatment> treatments, CoreBasicService coreService,
            // EntityDefinition sourceDefn, String sourceKey, EntityDefinition targetDefn, String
            // targetKey)
            BasicAssocService service = (BasicAssocService) DefinitionFactory.this.beanFactory.getBean(
                    "basicAssociationService", collectionName, treatments, coreService, source.getDefn(),
                    source.getKey(), target.getDefn(), target.getKey());
            
            source.setLinkToAssociation(this.relNameFromSource);
            target.setLinkToAssociation(this.relNameFromTarget);
            AssociationDefinition associationDefinition = new AssociationDefinition(super.type, super.resourceName,
                    super.collectionName, service, source, target);
            service.setDefn(associationDefinition);
            return associationDefinition;
        }
    }
    
    /**
     * Holder class for entity information
     * Mostly so checkstyle doesn't whine about private association definition methods having too
     * many parameters
     * 
     * @author nbrown
     * 
     */
    public static class EntityInfo {
        private final EntityDefinition defn;
        private final String linkName;
        private final String key;
        private final String hopLinkName;
        private String linkToAssociation;
        
        public EntityInfo(EntityDefinition defn, String linkName, String hopLinkName, String key) {
            super();
            this.defn = defn;
            this.linkName = linkName;
            this.hopLinkName = hopLinkName;
            this.key = key;
        }
        
        public EntityInfo(EntityDefinition defn, String linkName, String key) {
            super();
            this.defn = defn;
            this.linkName = linkName;
            this.hopLinkName = linkName;
            this.key = key;
        }
        
        public EntityDefinition getDefn() {
            return defn;
        }
        
        public String getLinkName() {
            return linkName;
        }
        
        public String getHopLinkName() {
            return hopLinkName;
        }
        
        public String getKey() {
            return key;
        }
        
        public void setLinkToAssociation(String linkToAssociation) {
            this.linkToAssociation = linkToAssociation;
        }
        
        public String getLinkToAssociation() {
            return linkToAssociation;
        }
        
    }
}

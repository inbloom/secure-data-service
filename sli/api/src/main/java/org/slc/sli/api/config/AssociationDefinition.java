package org.slc.sli.api.config;

import org.apache.commons.lang3.StringUtils;

import org.slc.sli.api.service.AssociationService;
import org.slc.sli.api.service.BasicAssocService;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.api.service.Validator;
import org.slc.sli.dal.repository.EntityRepository;

/**
 * Definition of an association resource
 * 
 * @author nbrown
 * 
 */
public final class AssociationDefinition extends EntityDefinition {
    private final EntityDefinition sourceEntity;
    private final EntityDefinition targetEntity;
    private final String relName;
    private final String sourceLink;
    private final String targetLink;
    private final String sourceKey;
    private final String targetKey;
    
    private AssociationDefinition(String type, String resourceName, AssociationService service, EntityInfo source,
            EntityInfo target, String relName) {
        super(type, resourceName, service);
        this.sourceEntity = source.getDefn();
        this.targetEntity = target.getDefn();
        this.relName = relName;
        this.sourceLink = source.getLinkName();
        this.targetLink = target.getLinkName();
        this.sourceKey = source.getKey();
        this.targetKey = target.getKey();
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
    
    /**
     * Gets the name of the relationship
     * 
     * @return
     */
    public String getRelName() {
        return relName;
    }

    /**
     * The label for the link to the source
     * 
     * @return
     */
    public String getSourceLink() {
        return sourceLink;
    }
    
    /**
     * The label for the link to the target
     * 
     * @return
     */
    public String getTargetLink() {
        return targetLink;
    }
    
    /**
     * The key for the target
     * 
     * @return
     */
    public String getSourceKey() {
        return sourceKey;
    }
    
    /**
     * The key for the source
     * 
     * @return
     */
    public String getTargetKey() {
        return targetKey;
    }

    @Override
    public AssociationService getService() {
        return (AssociationService) super.getService();
    }
    
    /**
     * Create a builder for an entity definition with the same collection and resource name
     * 
     * @param entityName
     *            the collection/resource name
     * @return the builder to create the entity definition
     */
    public static AssocBuilder makeAssoc(String entityName) {
        return new AssocBuilder(entityName);
    }
    
    /**
     * Create a builder for an entity definition
     * 
     * @param collectionName
     *            the name of the entity in the db
     * @param resourceName
     *            the name of the entity in the ReST uri
     * @return the builder to create the entity definition
     */
    public static AssocBuilder makeAssoc(String type, String collectionName, String resourceName) {
        return new AssocBuilder(type);
    }
    
    /**
     * Fluent builder for AssocBuilder.
     */
    public static class AssocBuilder extends EntityDefinition.Builder {
        private EntityInfo source;
        private EntityInfo target;
        private String relName;
        
        /**
         * Create a builder for an association definition
         * 
         * @param collectionName
         *            the name of the association in the db
         * @param resourceName
         *            the name of the association in the ReST uri
         */
        public AssocBuilder(String type) {
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
         * @param sourceKey
         *            the key to look up the source with
         * @return the builder
         */
        public AssocBuilder from(EntityDefinition source, String sourceLink, String sourceKey) {
            this.source = new EntityInfo(source, sourceLink, sourceKey);
            return this;
        }
        
        /**
         * Sets the target definition
         * 
         * @param target
         *            the target definition
         * @param targetLink
         *            the name of the link
         * @param targetKey
         *            the key to look up the target with
         * @return the builder
         */
        public AssocBuilder to(EntityDefinition target, String targetLink, String targetKey) {
            this.target = new EntityInfo(target, targetLink, targetKey);
            return this;
        }
        
        public AssocBuilder called(String relName) {
            this.relName = relName;
            return this;
        }

        @Override
        public AssocBuilder withTreatments(Treatment... treatments) {
            super.withTreatments(treatments);
            return this;
        }
        
        @Override
        public AssocBuilder withValidators(Validator... validators) {
            super.withValidators(validators);
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
            BasicAssocService service = new BasicAssocService(getCollectionName(), getTreatments(), getValidators(),
                    getRepo(), source.getDefn(), source.getKey());
            AssociationDefinition associationDefinition = new AssociationDefinition(getType(), getResourceName(),
                    service, source, target, relName);
            service.setDefn(associationDefinition);
            return associationDefinition;
        }
        
    }
    
    /**
     * Holder class for entity information
     * 
     * @author nbrown
     * 
     */
    private static class EntityInfo {
        private final EntityDefinition defn;
        private final String linkName;
        private final String key;
        
        public EntityInfo(EntityDefinition defn, String linkName, String key) {
            super();
            this.defn = defn;
            this.linkName = linkName;
            this.key = key;
        }
        
        public EntityDefinition getDefn() {
            return defn;
        }
        
        public String getLinkName() {
            return linkName;
        }
        
        public String getKey() {
            return key;
        }
    }
}

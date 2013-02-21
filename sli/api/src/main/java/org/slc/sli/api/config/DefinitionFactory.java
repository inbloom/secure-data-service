/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.slc.sli.api.service.BasicAssocService;
import org.slc.sli.api.service.BasicService;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Factory class for building Entity and Association definition objects.
 *
 * The quantity of fluent builder code is large enough to warrant capturing it in a factory class,
 * yet breaking this into two factories (entity and association) seemed unneccesarily complex.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@Component
public class DefinitionFactory {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> defaultRepo;

    @Autowired
    private IdTreatment idTreatment;

    @Autowired
    private TypeTreatment typeTreatment;

    @Autowired
    private MetaDataTreatment metaDataTreatment;

    @Autowired
    private AttendanceTreatment attendanceTreatment;

    @Autowired
    private ApplicationContext beanFactory;

    public EntityBuilder makeEntity(String type) {
        return new EntityBuilder(type, type);
    }

    public EntityBuilder makeEntity(String type, String resourceName) {
        return new EntityBuilder(type, resourceName);
    }

    public AssocBuilder makeAssoc(String type, String resourceName) {
        return new AssocBuilder(type, resourceName);
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
        protected Repository<Entity> repo;
        protected Right readRight;
        protected Right writeRight;
        private boolean supportsAggregates;
        private boolean skipContextValidation;
        private boolean wrapperEntity;

        /**
         * Create a builder for an entity definition. The collection name and resource name will
         * default to the type
         *
         * @param type
         *            the type of the entity.
         */
        EntityBuilder(String type) {
            this(type, type);
        }

        /**
         * Create a builder for an entity definition. The collection name will default to the type
         *
         * @param type
         *            the type of the entity.
         * @param resourceName
         *            the name of the resource the entity will be exposed as
         */
        EntityBuilder(String type, String resourceName) {
            this.type = type;
            this.collectionName = type;
            this.resourceName = resourceName;
            this.repo = DefinitionFactory.this.defaultRepo;
            this.treatments.add(DefinitionFactory.this.idTreatment);
            this.treatments.add(DefinitionFactory.this.typeTreatment);
            this.treatments.add(DefinitionFactory.this.metaDataTreatment);
            this.treatments.add(DefinitionFactory.this.attendanceTreatment);
            this.readRight = Right.READ_GENERAL;
            this.writeRight = Right.WRITE_GENERAL;
            this.supportsAggregates = false;
        }

        public EntityBuilder setRequiredReadRight(Right right) {
            this.readRight = right;
            return this;
        }


        public EntityBuilder setRequiredWriteRight(Right right) {
            this.writeRight = right;
            return this;
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
        public EntityBuilder storeIn(Repository<Entity> repo) {
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

        public EntityBuilder supportsAggregates() {
            this.supportsAggregates = true;
            return this;
        }

        public EntityBuilder skipContextValidation() {
            this.skipContextValidation = true;
            return this;
        }

        public EntityBuilder wrapperEntity() {
            this.wrapperEntity = true;
            return this;
        }

        /**
         * Create the actual entity definition
         *
         * @return the entity definition
         */
        public EntityDefinition build() {

            BasicService entityService = (BasicService) DefinitionFactory.this.beanFactory.getBean("basicService",
                    collectionName, treatments, this.readRight, this.writeRight, this.repo);

            EntityDefinition entityDefinition = new EntityDefinition(type, resourceName, collectionName, entityService,
                    supportsAggregates, skipContextValidation, wrapperEntity);
            entityService.setDefn(entityDefinition);
            return entityDefinition;
        }

        /**
         * Create the actual entity definition
         *
         * @return the entity definition
         */
        public EntityDefinition buildAndRegister(BasicDefinitionStore store) {

            EntityDefinition entityDefinition = this.build();
            store.addDefinition(entityDefinition);
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
        AssocBuilder(String type, String resourceName) {
            super(type, resourceName);
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
        public AssocBuilder storeIn(Repository<Entity> repo) {
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

            BasicAssocService service = (BasicAssocService) DefinitionFactory.this.beanFactory.getBean(
                    "basicAssociationService", collectionName, treatments, source.getDefn(), source.getKey(),
                    target.getDefn(), target.getKey(), this.repo);

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

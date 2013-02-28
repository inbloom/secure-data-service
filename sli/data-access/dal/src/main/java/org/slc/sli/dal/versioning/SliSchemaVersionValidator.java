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

package org.slc.sli.dal.versioning;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slc.sli.common.migration.config.MigrationConfig;
import org.slc.sli.common.migration.config.Strategy;
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.dal.repository.ValidationWithoutNaturalKeys;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Validates versions of XSD to versions of documents and signals for
 * up-versioning (if necessary).
 * 
 * @author kmyers
 * 
 */
public class SliSchemaVersionValidator {

    private static final Logger LOG = LoggerFactory.getLogger(SliSchemaVersionValidator.class);

    public static final String SARJE = "SARJE";
    public static final String DAL_SV = "dal_sv";
    public static final String ID = "_id";
    public static final String MONGO_SV = "mongo_sv";
    public static final String METADATA_COLLECTION = "metaData";

    private static final String VERSION_NUMBER_FIELD = "version";
    private static final int NOT_VERSIONED_YET = 0;
    
    private static final List<MigrationStrategy> NO_STRATEGIES_DEFINED = new ArrayList<MigrationStrategy>();

    @Autowired
    protected SchemaRepository entitySchemaRepository;

    @Autowired
    protected Resource migrationConfigResource;

    protected MongoTemplate mongoTemplate;
    

    private Map<String, Integer> entityTypesBeingMigrated;
    private Map<String, Integer> currentEntityTypeVersions;

    private Map<String, Map<Integer, List<MigrationStrategy>>> migrationStrategyMap;
    
    /**
     * If the entity is non null, has a metaData map, and its entity type has a version,
     * this method inserts that version into the metaData.
     * 
     * @param entity
     */
    public void insertVersionInformation(Entity entity) {
        if (entity != null) {
            Integer version = this.currentEntityTypeVersions.get(entity.getType());
            
            if (version != null) {
                Map<String, Object> metaData = entity.getMetaData();
                
                if (metaData != null) {
                    metaData.put(VERSION_NUMBER_FIELD, version);
                }
            }
        }
    }

    @PostConstruct
    public void initMigration() {
        this.detectMigrations();
        this.migrationStrategyMap = this.buildMigrationStrategyMap();
        this.warnForEachMissingMigrationStrategyList();
    }
    
    /**
     * Validates that all entities being migrated have migration strategies defined, or else
     * logs a warning for each missing list-of-strategies.
     * 
     */
    private void warnForEachMissingMigrationStrategyList() {
        for (Entry<String, Integer> entry : this.entityTypesBeingMigrated.entrySet()) {
            String entityType = entry.getKey();
            int newVersion = entry.getValue();
            
            if (this.getMigrationStrategies(entityType, 0, newVersion) == NO_STRATEGIES_DEFINED) {
                LOG.warn("Migration of entity type [{}] to version [{}] is undefined", entry.getKey(), entry.getValue());
            }
        }
    }

    private void detectMigrations() {

        this.entityTypesBeingMigrated = new HashMap<String, Integer>();
        this.currentEntityTypeVersions = new HashMap<String, Integer>();

        for (NeutralSchema neutralSchema : this.entitySchemaRepository.getSchemas()) {
            AppInfo appInfo = neutralSchema.getAppInfo();

            if (appInfo != null) {
                int schemaVersion = appInfo.getSchemaVersion();
                if (schemaVersion != AppInfo.NOT_VERSIONED) {
                    String entityType = neutralSchema.getType();
                    this.currentEntityTypeVersions.put(entityType, schemaVersion);
                    
                    Query query = new Query();
                    query.addCriteria(Criteria.where(ID).is(entityType));

                    DBObject dbObject = this.mongoTemplate.findOne(query, BasicDBObject.class, METADATA_COLLECTION);

                    if (dbObject == null) {
                        int sarjeFlag = (schemaVersion > 1) ? 1 : 0;
                        int storedSchemaVersion = schemaVersion - sarjeFlag; // because migrating from x to x might be odd
                        
                        Map<String, Object> objectToSave = new HashMap<String, Object>();
                        objectToSave.put(ID, entityType);
                        objectToSave.put(DAL_SV, schemaVersion);
                        objectToSave.put(MONGO_SV, storedSchemaVersion);
                        objectToSave.put(SARJE, sarjeFlag);
                        this.mongoTemplate.insert(objectToSave, METADATA_COLLECTION);
                    } else {
                        int lastKnownDalVersion = Double.valueOf(dbObject.get(DAL_SV).toString()).intValue();
                        int currentMongoVersion = Double.valueOf(dbObject.get(MONGO_SV).toString()).intValue();

                        if (lastKnownDalVersion < schemaVersion) {
                            // write a signal for the entity type to be upversioned
                            Update update = new Update().set(DAL_SV, schemaVersion).set(SARJE, 1);
                            this.mongoTemplate.updateFirst(query, update, METADATA_COLLECTION);
                        }
                        
                        if (currentMongoVersion < schemaVersion) {
                            // remember that the entity's schema is being upversioned
                            this.entityTypesBeingMigrated.put(entityType, schemaVersion);
                        }
                    }
                }
            }
        }
    }

    private int getEntityVersionNumber(Entity entity) {
        Map<String, Object> entityMetaData = entity.getMetaData();

        if (entityMetaData.containsKey(VERSION_NUMBER_FIELD)) {
            return (Integer) entityMetaData.get(VERSION_NUMBER_FIELD);
        }

        return NOT_VERSIONED_YET;
    }

    public Entity migrate(String collectionName, Entity entity, ValidationWithoutNaturalKeys repo)
            throws MigrationException {

        if (entity == null) {
            return null;
        }

        String entityType = entity.getType();
        Entity localEntity = entity;

        if (isMigrationNeeded(entityType, localEntity)) {
            localEntity = performMigration(entityType, localEntity, repo, collectionName, true);
        }

        return localEntity;
    }

    protected boolean isMigrationNeeded(String entityType, Entity entity) {
        if (this.entityTypesBeingMigrated.containsKey(entityType)) {
            int entityVersionNumber = this.getEntityVersionNumber(entity);
            int newVersionNumber = this.entityTypesBeingMigrated.get(entityType);

            if (entityVersionNumber < newVersionNumber) {
                return true;
            }
        }

        return false;
    }

    protected Entity performMigration(String entityType, Entity entity, ValidationWithoutNaturalKeys repo,
                          String collectionName, boolean doUpdate) {
        int newVersionNumber = this.entityTypesBeingMigrated.get(entityType);
        int entityVersionNumber = this.getEntityVersionNumber(entity);

        Entity localEntity = entity;
        for (MigrationStrategy migrationStrategy : this.getMigrationStrategies(entityType, entityVersionNumber, newVersionNumber)) {
            localEntity = (Entity) migrationStrategy.migrate(localEntity);
        }

        localEntity.getMetaData().put(VERSION_NUMBER_FIELD, newVersionNumber);

        if (doUpdate) {
            repo.updateWithoutValidatingNaturalKeys(collectionName, localEntity);
        }

        return localEntity;
    }


    public Iterable<Entity> migrate(String collectionName, Iterable<Entity> entities, ValidationWithoutNaturalKeys repo)
            throws MigrationException {

        if (entities == null) {
            return null;
        }

        List<Entity> migratedEntities = new ArrayList<Entity>();
        List<Entity> returnEntities = new ArrayList<Entity>();

        for (Entity entity : entities) {
            if (isMigrationNeeded(entity.getType(), entity)) {
                entity = performMigration(entity.getType(), entity, repo, collectionName, false);
                migratedEntities.add(entity);
            }

            returnEntities.add(entity);
        }

        for (Entity migratedEntity : migratedEntities) {
            repo.updateWithoutValidatingNaturalKeys(collectionName, migratedEntity);
        }

        return returnEntities;
    }

    /**
     * This method should be called post construct to load the strategies per entity type
     */
    private Map<String, Map<Integer, List<MigrationStrategy>>> buildMigrationStrategyMap() {

        Map<String, Map<Integer, List<MigrationStrategy>>> migrationStrategyMap = new HashMap<String, Map<Integer, List<MigrationStrategy>>>();
        
        MigrationConfig config = null;
        try {
            config = MigrationConfig.parse(migrationConfigResource.getInputStream());
        } catch (IOException e) {
            LOG.error("Unable to read migration config file", e);
            return migrationStrategyMap;
        }

        Map<String, Map<Integer, List<Map<Strategy, Map<String, Object>>>>> entityConfig = config.getEntities();

        // iterate over entities
        for (Map.Entry<String, Map<Integer, List<Map<Strategy, Map<String, Object>>>>> entityEntry : entityConfig
                .entrySet()) {

            String entityType = entityEntry.getKey();
            Map<Integer, List<Map<Strategy, Map<String, Object>>>> versionUpdates = entityEntry.getValue();

            Map<Integer, List<MigrationStrategy>> migrationsForVersion = new HashMap<Integer, List<MigrationStrategy>>();

            // iterate over version updates for a single entity
            for (Map.Entry<Integer, List<Map<Strategy, Map<String, Object>>>> versionEntry : versionUpdates.entrySet()) {

                Integer versionNumber = versionEntry.getKey();
                List<Map<Strategy, Map<String, Object>>> versionStrategies = versionEntry.getValue();

                List<MigrationStrategy> strategies = new ArrayList<MigrationStrategy>();
                migrationsForVersion.put(versionNumber, strategies);

                for (Map<Strategy, Map<String, Object>> versionStrategy : versionStrategies) {

                    // iterate over migration strategies for a single version update
                    for (Map.Entry<Strategy, Map<String, Object>> strategy : versionStrategy.entrySet()) {
                        try {
                            MigrationStrategy migrationStrategy = strategy.getKey().getNewImplementation();
                            migrationStrategy.setParameters(strategy.getValue());
                            strategies.add(migrationStrategy);
                        } catch (MigrationException e) {
                            LOG.error("Unable to instantiate TransformStrategy: " + strategy, e);
                        }
                    }
                }

            }
            migrationStrategyMap.put(entityType, migrationsForVersion);
        }
        
        return migrationStrategyMap;

    }

    protected List<MigrationStrategy> getMigrationStrategies(String entityType, int entityVersionNumber, int newVersionNumber) {

        Map<Integer, List<MigrationStrategy>> entityMigrations = migrationStrategyMap.get(entityType);
        List<MigrationStrategy> allStrategies = new LinkedList<MigrationStrategy>();

        if (entityMigrations != null) {
            for (int version = entityVersionNumber+1; version <= newVersionNumber; version++) {
                List<MigrationStrategy> strategies = entityMigrations.get(version);

                if (strategies != null) {
                    allStrategies.addAll(strategies);
                }
            }

            if (!allStrategies.isEmpty()) {
                return allStrategies;
            }
        }

        // for the cases where it is undefined
        return NO_STRATEGIES_DEFINED;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}

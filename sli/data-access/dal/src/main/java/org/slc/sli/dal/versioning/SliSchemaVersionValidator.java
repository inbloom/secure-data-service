/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slc.sli.dal.migration.config.Strategy;
import org.slc.sli.dal.migration.strategy.MigrationException;
import org.slc.sli.dal.migration.strategy.MigrationStrategy;
import org.slc.sli.dal.migration.strategy.config.MigrationConfig;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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

    @Autowired
    @Qualifier("mongoTemplate")
    protected MongoTemplate mongoTemplate;

    private Map<String, Integer> entitiesBeingUpversioned;

    private Map<String, Map<Integer, List<MigrationStrategy>>> migrationStrategyMap;

    @PostConstruct
    public void initMigration() {
        this.detectMigrations();
        this.migrationStrategyMap = this.buildMigrationStrategyMap();
    }

    private void detectMigrations() {

        this.entitiesBeingUpversioned = new HashMap<String, Integer>();

        for (NeutralSchema neutralSchema : entitySchemaRepository.getSchemas()) {
            AppInfo appInfo = neutralSchema.getAppInfo();

            if (neutralSchema.getType().equals("teacher")) {
                System.out.print("");
            }

            if (appInfo != null) {
                int schemaVersion = appInfo.getSchemaVersion();
                if (schemaVersion != AppInfo.NOT_VERSIONED) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where(ID).is(neutralSchema.getType()));

                    DBObject dbObject = mongoTemplate.findOne(query, BasicDBObject.class, METADATA_COLLECTION);

                    if (dbObject == null) {
                        Map<String, Object> objectToSave = new HashMap<String, Object>();
                        objectToSave.put(ID, neutralSchema.getType());
                        objectToSave.put(DAL_SV, 1);
                        objectToSave.put(MONGO_SV, 1);
                        objectToSave.put(SARJE, 0);
                        mongoTemplate.insert(objectToSave, METADATA_COLLECTION);
                    } else {
                        int lastKnownDalVersion = Double.valueOf(dbObject.get(DAL_SV).toString()).intValue();

                        if (lastKnownDalVersion < schemaVersion) {

                            // write a signal for the entity type to be upversioned
                            Update update = new Update().set(DAL_SV, schemaVersion).set(SARJE, 1);
                            mongoTemplate.updateFirst(query, update, METADATA_COLLECTION);

                            // remember that the entity's schema is being upversioned
                            entitiesBeingUpversioned.put(neutralSchema.getType(), schemaVersion);
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

    public Entity migrate(Entity entity) throws MigrationException {

        if (entity == null) {
            return null;
        }

        String entityType = entity.getType();

        Entity localEntity = entity;

        if (this.entitiesBeingUpversioned.containsKey(entityType)) {
            int entityVersionNumber = this.getEntityVersionNumber(entity);
            int newVersionNumber = this.entitiesBeingUpversioned.get(entityType);

            if (entityVersionNumber < newVersionNumber) {

                for (MigrationStrategy migrationStrategy : getMigrationStrategies(entityType, newVersionNumber)) {
                    localEntity = migrationStrategy.migrate(localEntity);
                }
                
                localEntity.getMetaData().put(VERSION_NUMBER_FIELD, newVersionNumber);
            }
        }

        return localEntity;
    }

    public List<Entity> migrate(List<Entity> entities) throws MigrationException {

        if (entities == null) {
            return null;
        }

        List<Entity> migratedEntities = new ArrayList<Entity>();

        for (Entity entity : entities) {
            migratedEntities.add(this.migrate(entity));
        }

        return migratedEntities;
    }

    /**
     * This method should be called post construct to load the strategies per entity type
     */
    private  Map<String, Map<Integer, List<MigrationStrategy>>> buildMigrationStrategyMap() {

        Map<String, Map<Integer, List<MigrationStrategy>>> migrationStrategyMap = new HashMap<String, Map<Integer, List<MigrationStrategy>>>();
        
        MigrationConfig config = null;
        try {
            config = MigrationConfig.parse(migrationConfigResource.getInputStream());
        } catch (IOException e) {
            LOG.error("Unable to read migration config file", e);
            return migrationStrategyMap;
        }

        Map<String, Map<Integer, Map<Strategy, Map<String, Object>>>> entityConfig = config.getEntities();

        // iterate over entities
        for (Map.Entry<String, Map<Integer, Map<Strategy, Map<String, Object>>>> entityEntry : entityConfig.entrySet()) {

            String entityType = entityEntry.getKey();
            Map<Integer, Map<Strategy, Map<String, Object>>> versionUpdates = entityEntry.getValue();

            Map<Integer, List<MigrationStrategy>> migrationsForVersion = new HashMap<Integer, List<MigrationStrategy>>();

            // iterate over version updates for a single entity
            for (Map.Entry<Integer, Map<Strategy, Map<String, Object>>> versionEntry : versionUpdates.entrySet()) {

                Integer versionNumber = versionEntry.getKey();
                Map<Strategy, Map<String, Object>> versionStrategies = versionEntry.getValue();

                List<MigrationStrategy> strategies = new ArrayList<MigrationStrategy>();
                migrationsForVersion.put(versionNumber, strategies);

                // iterate over migration strategies for a single version update
                for (Map.Entry<Strategy, Map<String, Object>> strategy : versionStrategies.entrySet()) {
                    try {
                        MigrationStrategy migrationStrategy = strategy.getKey().getNewImplementation();
                        migrationStrategy.setParameters(strategy.getValue());
                        strategies.add(migrationStrategy);
                    } catch (MigrationException e) {
                        LOG.error("Unable to instantiate TransformStrategy: " + strategy, e);
                    }
                }
            }
            migrationStrategyMap.put(entityType, migrationsForVersion);
        }
        
        return migrationStrategyMap;

    }

    private List<MigrationStrategy> getMigrationStrategies(String entityType, int newVersionNumber) {

        Map<Integer, List<MigrationStrategy>> entityMigrations = migrationStrategyMap.get(entityType);

        if (entityMigrations != null) {
            List<MigrationStrategy> strategies = entityMigrations.get(newVersionNumber);

            if (strategies != null) {
                return strategies;
            }
        }

        // for the cases where it is undefined
        return NO_STRATEGIES_DEFINED;
    }

}

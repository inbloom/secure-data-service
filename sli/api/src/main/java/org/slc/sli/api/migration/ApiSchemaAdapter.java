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
package org.slc.sli.api.migration;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.common.migration.config.MigrationConfig;
import org.slc.sli.common.migration.config.Strategy;
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Schema adapter for API version migration
 *
 * @author jtully
 *
 */
public class ApiSchemaAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiSchemaAdapter.class);

    protected Resource upMigrationConfigResource;
    protected Resource downMigrationConfigResource;
    protected Resource entityTransformConfigResource;

    private Map<String, Map<Integer, List<MigrationStrategy>>> upMigrationStrategyMap;
    private Map<String, Map<Integer, List<MigrationStrategy>>> downMigrationStrategyMap;
    private Map<String, Map<Integer, List<MigrationStrategy>>> entityTransformStrategyMap;

    @PostConstruct
    public void initMigration() {
        upMigrationStrategyMap = buildMigrationStrategyMap(upMigrationConfigResource);
        downMigrationStrategyMap = buildMigrationStrategyMap(downMigrationConfigResource);
        entityTransformStrategyMap = buildMigrationStrategyMap(entityTransformConfigResource);
    }

    /**
     * This method should be called post construct to load the strategies per entity type
     * TODO this code is common with SliSchemaVersionValidator - refactor
     */
    private Map<String, Map<Integer, List<MigrationStrategy>>> buildMigrationStrategyMap(Resource migrationConfigResource) {

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

    /**
     * get the migration strategies for converting from the current DB version to the specified API version
     */
    public List<MigrationStrategy> getUpMigrationStrategies(String entityType, int versionNumber) {

        List<MigrationStrategy> strategies = null;
        Map<Integer, List<MigrationStrategy>> entityMigrations = upMigrationStrategyMap.get(entityType);

        if (entityMigrations != null) {
            strategies = entityMigrations.get(versionNumber);
        }

        return strategies;
    }

    /**
     * get the migration strategies for converting from the specified API version to the current DB version
     */
    public List<MigrationStrategy> getDownMigrationStrategies(String entityType, int versionNumber) {

        List<MigrationStrategy> strategies = null;
        Map<Integer, List<MigrationStrategy>> entityMigrations = downMigrationStrategyMap.get(entityType);

        if (entityMigrations != null) {
            strategies = entityMigrations.get(versionNumber);
        }

        return strategies;
    }

    public List<MigrationStrategy> getEntityTransformMigrationStrategies(String entityType, int versionNumber) {

        List<MigrationStrategy> strategies = null;
        Map<Integer, List<MigrationStrategy>> entityMigrations = null;
        if(entityTransformStrategyMap.containsKey(entityType)) {
            entityMigrations = entityTransformStrategyMap.get(entityType);
        }

        if (entityMigrations != null) {
            strategies = entityMigrations.get(versionNumber);
        }

        return strategies;
    }
    /**
     * Migrate an entity to or from a specified API version
     */
    public Entity migrate(Entity entity, int apiVersion, boolean upConversion)
            throws MigrationException {

        if (entity == null) {
            return null;
        }

        String entityType = entity.getType();

        List<MigrationStrategy> migrationStrategies = null;
        if (upConversion) {
            migrationStrategies = getUpMigrationStrategies(entityType, apiVersion);
        } else {
            migrationStrategies = getDownMigrationStrategies(entityType, apiVersion);
        }

        Entity migratedEntity = entity;
        if (migrationStrategies != null) {
            for (MigrationStrategy migrationStrategy : migrationStrategies) {
                migratedEntity = (Entity) migrationStrategy.migrate(migratedEntity);
            }
        }

        return migratedEntity;
    }

    /**
     * Migrate a list of entities to or from a specified API version
     */
    public Iterable<Entity> migrate(Iterable<Entity> entities, int apiVersion, boolean upConversion)
            throws MigrationException {

        if (entities == null) {
            return null;
        }

        List<Entity> migratedEntities = new ArrayList<Entity>();

        for (Entity entity : entities) {
            migratedEntities.add(this.migrate(entity, apiVersion, upConversion));
        }

        return migratedEntities;
    }

    public List<EntityBody> migrate(EntityBody entityBody, String entityType, int versionNumber) {

        if (entityBody == null) {
            return null;
        }
        List<EntityBody> entityBodies = new ArrayList<EntityBody>();
        entityBodies.add(entityBody);
        List<MigrationStrategy> migrationStrategies = getEntityTransformMigrationStrategies(entityType, versionNumber);
        for(MigrationStrategy migrationStrategy: migrationStrategies) {
            entityBodies = migrationStrategy.migrate(entityBodies);
        }
       return  entityBodies;
    }
    public Resource getUpMigrationConfigResource() {
        return upMigrationConfigResource;
    }

    public void setUpMigrationConfigResource(Resource upMigrationConfigResource) {
        this.upMigrationConfigResource = upMigrationConfigResource;
    }

    public Resource getDownMigrationConfigResource() {
        return downMigrationConfigResource;
    }

    public void setDownMigrationConfigResource(Resource downMigrationConfigResource) {
        this.downMigrationConfigResource = downMigrationConfigResource;
    }
    public Resource getEntityTransformConfigResource() {
        return entityTransformConfigResource;
    }

    public void setEntityTransformConfigResource(Resource entityTransformConfigResource) {
        this.entityTransformConfigResource = entityTransformConfigResource;
    }

}

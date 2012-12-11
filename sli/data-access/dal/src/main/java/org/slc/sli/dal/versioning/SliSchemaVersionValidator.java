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

import java.util.*;

import javax.annotation.PostConstruct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slc.sli.dal.migration.config.Strategy;
import org.slc.sli.dal.migration.strategy.TransformStrategy;
import org.slc.sli.dal.migration.strategy.impl.AddStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.dal.migration.strategy.TransformStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Validates versions of XSD to versions of documents and signals for
 * up-versioning (if necessary).
 *
 * @author kmyers
 *
 */
public class SliSchemaVersionValidator {

    public static final String SARJE = "SARJE";
    public static final String DAL_SV = "dal_sv";
    public static final String ID = "_id";
    public static final String MONGO_SV = "mongo_sv";
    public static final String METADATA_COLLECTION = "metaData";
    
    private static final String VERSION_NUMBER_FIELD = "version";
    private static final int NOT_VERSIONED_YET = 0;

    @Autowired
    protected SchemaRepository entitySchemaRepository;

    @Autowired
    @Qualifier("mongoTemplate")
    protected MongoTemplate mongoTemplate;
    
    private Map<String, Integer> entitiesBeingUpversioned;

    @Autowired
    private ApplicationContext applicationContext;


    @PostConstruct
    public void validate() {

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
    
    public Entity migrate(Entity entity) {
        
        String entityType = entity.getType();
        
        Entity localEntity = entity;
        
        if (this.entitiesBeingUpversioned.containsKey(entityType)) {
            int entityVersionNumber = this.getEntityVersionNumber(entity);
            int newVersionNumber = this.entitiesBeingUpversioned.get(entityType);
            
            if (entityVersionNumber < newVersionNumber) {
                
                for (TransformStrategy transformStrategy : whatever.getTransformStrategies(entityType, newVersionNumber)) {
                    localEntity = transformStrategy.transform(localEntity);
                }
                
                // perform on-demand upversioning
                //List<TransformStrategy> strategyList = migrationStrategyMap.get(entityType);
                //for(TransformStrategy strategy: strategyList) {
                //    strategy.transform(entity);
            }
        }
        
        return localEntity;
    }

    public List<Entity> migrate(List<Entity> entities) {

        List<Entity> migratedEntities = new ArrayList<Entity>();
        
        for (Entity entity : entities) {
            migratedEntities.add(this.migrate(entity));
        }
        
        return migratedEntities;
    }
    private Map<String,List<TransformStrategy>> migrationStrategyMap = new HashMap<String, List<TransformStrategy>>();
    private Map<String,Map<Strategy, Map<String,Object>>> entityConfig; //this will use object mapper to map json to config

    /**
     * This method should be called post construct to load the strategies per entity type
     */
    private void buildMigrationStrategyMap () {
        readConfig();
        for (Map.Entry<String, Map<Strategy, Map<String, Object>>> entry : entityConfig.entrySet()) {
           List<TransformStrategy> strategies = new ArrayList<TransformStrategy>();
            for (Map.Entry<Strategy,Map<String,Object>> strategy: entry.getValue().entrySet()){
                strategies.addAll(getTransformStrategy(strategy.getKey(),strategy.getValue()));
            }
            migrationStrategyMap.put(entry.getKey(),strategies);
        }

    }

    /**
     * This method use the spring context to pass name of the strategy and get the instance
     * @param strategy
     * @param params
     * @return
     */

    private List<TransformStrategy> getTransformStrategy(Strategy strategy, Map<String,Object> params) {
        List<TransformStrategy> transformStrategyList = new ArrayList<TransformStrategy>();
        switch (strategy) {
            case ADD:
                //This might be a place for builder to build up the strategy
                //need a way to pass the parameters read from the resource config json
                TransformStrategy transformStrategy = applicationContext.getBean(AddStrategy.class);
                transformStrategy.setParameters(params);
                transformStrategyList.add(transformStrategy);
                break;
            case CUSTOM:
                //for custom class implementation the class name is passed in the param map key
                for (String className: params.keySet()) {
                    TransformStrategy custTransformStrategy = (TransformStrategy)applicationContext.getBean(className);
                    custTransformStrategy.setParameters(params);
                    transformStrategyList.add(custTransformStrategy);
                }
                break;

            case DEFAULT:
                transformStrategy = null;

        }
        return transformStrategyList;
    }

    /**
     * This method will use the object parser and read the json config file and create list of operation per entity
     */

    private void readConfig() {
        entityConfig = new HashMap<String, Map<Strategy, Map<String, Object>>>();
        Map<Strategy,Map<String,Object>> dummyOpp = new HashMap<Strategy, Map<String, Object>>();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("foo","bar");
        dummyOpp.put(Strategy.ADD,paramMap);
        entityConfig.put("student", dummyOpp);
    }

}

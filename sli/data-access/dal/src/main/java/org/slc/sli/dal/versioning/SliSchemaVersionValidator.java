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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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

    @Autowired
    protected SchemaRepository entitySchemaRepository;

    @Autowired
    @Qualifier("mongoTemplate")
    protected MongoTemplate mongoTemplate;
    
    private List<String> entitiesBeingUpversioned;


    @PostConstruct
    public void validate() {

        this.entitiesBeingUpversioned = new ArrayList<String>();
        
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
                            
                            // remember that the entity is being upversioned
                            entitiesBeingUpversioned.add(neutralSchema.getType());
                        }
                    }
                }
            }
        }
    }
    
    private boolean requiresMigration(String entityType) {
        if (this.entitiesBeingUpversioned.contains(entityType)) {
            return true;
        }
        
        
        
        return false;
    }


    public Entity migrate(Entity entity) {
        
        if (this.requiresMigration(entity.getType())) {
            
        }
        
        return entity;
    }

    public List<Entity> migrate(List<Entity> entities) {
        List<Entity> migratedEntities = new ArrayList<Entity>();
        
        for (Entity entity : entities) {
            migratedEntities.add(this.migrate(entity));
        }
        
        return migratedEntities;
    }

}

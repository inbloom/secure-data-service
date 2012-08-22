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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

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
@Component
public class SliSchemaVersionValidator {

    @Autowired
    protected SchemaRepository entitySchemaRepository;

    @Autowired
    protected MongoTemplate mongoTemplate;

    private static final String METADATA_COLLECTION = "metaData";

    @PostConstruct
    public void validate() {

        for (NeutralSchema neutralSchema : entitySchemaRepository.getSchemas()) {
            AppInfo appInfo = neutralSchema.getAppInfo();
            if (appInfo != null) {
                int schemaVersion = appInfo.getSchemaVersion();
                if (schemaVersion != AppInfo.NOT_VERSIONED) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(neutralSchema.getType()));

                    DBObject dbObject = mongoTemplate.findOne(query, BasicDBObject.class, METADATA_COLLECTION);

                    if (dbObject == null) {
                        Map<String, Object> objectToSave = new HashMap<String, Object>();
                        objectToSave.put("_id", neutralSchema.getType());
                        objectToSave.put("dal_sv", 1);
                        objectToSave.put("mongo_sv", 1);
                        objectToSave.put("SARJE", 0);
                        mongoTemplate.insert(objectToSave, METADATA_COLLECTION);
                    } else {
                        int lastKnownDalVersion = Double.valueOf(dbObject.get("dal_sv").toString()).intValue();

                        if (lastKnownDalVersion < schemaVersion) {
                            Update update = new Update().set("dal_sv", schemaVersion).set("SARJE", 1);
                            System.out.println("RUNNING UPDATE");
                            mongoTemplate.updateFirst(query, update, METADATA_COLLECTION);
                        }
                    }
                }
            }
        }
    }

}

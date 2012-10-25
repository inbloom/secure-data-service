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
package org.slc.sli.search.connector.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 *  Mongo connector
 * 
 * @author dwu
 * 
 */
public class SourceDatastoreConnectorImpl implements SourceDatastoreConnector {

    private static final String TENANT_COLLECTION = "tenant";

    private MongoTemplate mongoTemplate;

    /**
     * Create DBCUrsor
     * Also, make this method available to Mock for UT
     * 
     * @param collectionName
     * @param fields
     * @return
     */
    public DBCursor getDBCursor(String collectionName, List<String> fields) {
        // execute query, get cursor of results
        BasicDBObject keys = new BasicDBObject();
        for (String field : fields) {
            keys.put(field, 1);
        }

        DBCollection collection = mongoTemplate.getCollection(collectionName);
        return collection.find(new BasicDBObject(), keys);
    }
    
    @SuppressWarnings("unchecked")
    public List<Tenant> getTenants() {
        BasicDBObject keys = new BasicDBObject();
        keys.put("body.tenantId", 1);
        keys.put("body.dbName", 1);
        DBCollection collection = mongoTemplate.getCollection(TENANT_COLLECTION);
        List<DBObject> objects = collection.find(new BasicDBObject(), keys).toArray();
        List<Tenant> tenants = new ArrayList<Tenant>();
        Map<String, Object> body;
        String tenantId, dbName;
        for (DBObject o: objects) {
            body = (Map<String, Object>)o.get("body");
            dbName = (String)body.get("dbName");
            tenantId = (String)body.get("tenantId");
            tenants.add(new Tenant(tenantId, dbName == null ? TenantIdToDbName.convertTenantIdToDbName(tenantId) : dbName));   
        }
        return tenants;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
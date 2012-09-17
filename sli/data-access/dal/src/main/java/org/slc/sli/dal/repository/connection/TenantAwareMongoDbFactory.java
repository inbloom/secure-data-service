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


package org.slc.sli.dal.repository.connection;

import javax.annotation.Resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import org.slc.sli.dal.repository.tenancy.CurrentTenantHolder;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author okrook
 *
 */
public class TenantAwareMongoDbFactory extends SimpleMongoDbFactory {
    
    private static final String tenantCollectionName = "tenant";
    private static final String tenantDatabaseName = "databaseName";

    /**
     * Create an instance of {@link TenantAwareMongoDbFactory} given the {@link Mongo} instance and database name.
     *
     * @param mongo Mongo instance, must not be {@literal null}.
     * @param systemDatabaseName system database name, not be {@literal null}.
     */
    public TenantAwareMongoDbFactory(Mongo mongo, String systemDatabaseName) {
        super(mongo, systemDatabaseName);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.mongodb.core.SimpleMongoDbFactory#getDb()
     */
    @Override
    public DB getDb() throws DataAccessException {
        String tenantId = CurrentTenantHolder.getCurrentTenant();
        if (tenantId == null) {
            return super.getDb();
        } else {
            BasicDBObject query = new BasicDBObject();
            query.put("tenantId", tenantId);
            DB systemDb = super.getDb();
            DBObject dbObject = systemDb.getCollection(tenantCollectionName).findOne(query);
            return super.getDb((String) dbObject.get(tenantDatabaseName));            
        }
    }
    
}

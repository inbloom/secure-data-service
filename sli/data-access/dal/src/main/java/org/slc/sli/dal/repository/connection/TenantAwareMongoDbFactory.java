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

import com.mongodb.DB;
import com.mongodb.Mongo;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author okrook
 *
 */
public class TenantAwareMongoDbFactory extends SimpleMongoDbFactory {

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
        return super.getDb();
    }
}

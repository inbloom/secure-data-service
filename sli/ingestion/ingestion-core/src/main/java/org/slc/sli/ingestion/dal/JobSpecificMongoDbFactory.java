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

package org.slc.sli.ingestion.dal;

import com.mongodb.DB;
import com.mongodb.Mongo;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * JobSpecificMongoDbFactory class.
 *
 */
public class JobSpecificMongoDbFactory extends SimpleMongoDbFactory {

    public JobSpecificMongoDbFactory(Mongo mongo, String databaseName) {
        super(mongo, databaseName);
    }

    @Override
    public DB getDb() throws DataAccessException {

        String jobId = TenantContext.getJobId();
        if (jobId == null) {
            return super.getDb();
        }

        String dbName = BatchJobUtils.jobIdToDbName(jobId);
        return super.getDb(dbName);
    }
}

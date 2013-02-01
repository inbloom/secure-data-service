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
package org.slc.sli.search.connector;

import java.util.List;

import com.mongodb.DBCursor;

public interface SourceDatastoreConnector {
    public static class Tenant {
        private final String tenantId;
        private final String dbName;

        public Tenant(String tenantId, String dbName) {
            this.tenantId = tenantId;
            this.dbName = dbName;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getDbName() {
            return dbName;
        }
    }
    public List<Tenant> getTenants();

    public DBCursor getDBCursor(String collectionName, List<String> fields);

    void save(String collectionName, Object o);

    <T> List<T> findAll(String collectionName, Class<T> entityClass);

}

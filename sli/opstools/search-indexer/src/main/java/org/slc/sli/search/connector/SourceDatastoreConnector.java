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
    
}

package org.inbloom.model;

import org.springframework.data.annotation.Id;

/**
 * pojo to represent Tenant
 * @author ben morgan
 */
public class Tenant {

    @Id
    String id;
    String tenantId;
    String dbName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


}

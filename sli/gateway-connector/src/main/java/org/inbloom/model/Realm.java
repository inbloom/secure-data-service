package org.inbloom.model;


import org.springframework.data.annotation.Id;

/**
 * POJO to represent a realm
 * @author ben morgan
 */
public class Realm {
    @Id
    String id;

    //text name of tenant
    String tenantId;

    //globally unique realm name
    String uniqueIdentifier;

    String name;

    String idpRedirect;

    public String toString()
    {
        return "[\"" + id + "\", \"" + idpRedirect + "\", \"" + name + "\"]";
    }

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

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdpRedirect() {
        return idpRedirect;
    }

    public void setIdpRedirect(String idpRedirect) {
        this.idpRedirect = idpRedirect;
    }
}

package org.slc.sli.domain;

/**
 * The known entity metadata keys.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public enum EntityMetadataKey {
    
    /**
     * Tenant ID is an SLI assigned identifier that is used to distinguish data coming from a
     * state. Data with matching tenant IDs should be consistent.
     */
    TENANT_ID("tenantId"),
    
    /** The external, client generated identifier for this document. */
    EXTERNAL_ID("externalId"),
    
    /** Timestamp that indicates when this document was created */
    CREATED("created"),
    
    /** Timestamp that indicates when this document was last updated */
    UPDATED("updated");
    
    private String key;
    
    private EntityMetadataKey(String key) {
        this.key = key;
    }
    
    /**
     * Returns the key.
     * 
     * @return
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Returns the key
     */
    public String toString() {
        return key;
    }
}

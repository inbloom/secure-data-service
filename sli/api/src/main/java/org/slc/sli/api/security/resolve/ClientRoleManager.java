package org.slc.sli.api.security.resolve;

import java.util.List;

/**
 * Manager for mappings between client roles and SLI roles.
 * 
 */
public interface ClientRoleManager {
    
    /**
     * Given a list of client role names this returns the the corresponding SLI roles.
     * @param realmId
     * @param clientRoleNames
     * @return
     */
    public List<String> resolveRoles(String realmId, List<String> clientRoleNames);
    
    /**
     * Adds a mapping between a client role and an SLI role.
     * @param realmId
     * @param sliRoleName
     * @param clientRoleName
     */
    public void addClientRole(String realmId, String sliRoleName, String clientRoleName);
    
    /**
     * Deletes a mapping between a client role and an SLI role.
     * @param realmId
     * @param clientRoleName
     */
    public void deleteClientRole(String realmId, String clientRoleName);
    
    /**
     * Given a client role this will return the SLI role that it maps to or null if
     * no mapping exists. 
     * @param realmId
     * @param clientRoleName
     * @return
     */
    public String getSliRoleName(String realmId, String clientRoleName);
    
    /**
     * Returns a list of all mappings for this SLI role and realm ID.  
     * @param realmId
     * @param sliRoleName
     * @return
     */
    public List<String> getMappings(String realmId, String sliRoleName);
}

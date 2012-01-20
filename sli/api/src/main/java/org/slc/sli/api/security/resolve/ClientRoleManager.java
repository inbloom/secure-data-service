package org.slc.sli.api.security.resolve;

import java.util.List;

/**
 * Maps client roles to SLI roles
 * 
 * @author dkornishev
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
    
    public void addClientRole(String realmId, String sliRoleName, String clientRoleName);
    
    public void deleteClientRole(String realmId, String clientRoleName);
    
    public String getSliRoleName(String realmId, String clientRoleName);
    
    public List<String> getMappings(String realmId, String sliRoleName);
}

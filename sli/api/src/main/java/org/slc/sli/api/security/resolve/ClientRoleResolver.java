package org.slc.sli.api.security.resolve;

import java.util.List;

/**
 * Manager for mappings between client roles and SLI roles.
 * 
 */
public interface ClientRoleResolver {
    
    /**
     * Given a list of client role names this returns the the corresponding SLI roles.
     * @param realmId
     * @param clientRoleNames
     * @return
     */
    public List<String> resolveRoles(String realmId, List<String> clientRoleNames);
    
}
